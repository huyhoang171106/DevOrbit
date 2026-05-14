#!/usr/bin/env python3
"""
Supabase Full Backup Script
============================
Doc .env trong devorbit-api, ket noi Supabase PostgreSQL,
xuat tat ca tables thanh CSV/JSON files vao thu muc backup/.

Mặc dinh chi backup schema `public` (du lieu app).
Dung `--all-schemas` de backup ca auth, storage, realtime, v.v.

Usage:
    python scripts/backup_supabase.py
    python scripts/backup_supabase.py --output ./my_backup
    python scripts/backup_supabase.py --all-schemas
    python scripts/backup_supabase.py --schema public --format json
"""

import os
import sys
import re
import argparse
import csv
from pathlib import Path
from datetime import datetime
from urllib.parse import quote_plus
from dotenv import load_dotenv

import pandas as pd
from sqlalchemy import create_engine, text, inspect

# Cac schema noi bo cua Supabase, mac dinh bi loai bo
SUPABASE_INTERNAL_SCHEMAS = {
    "auth", "storage", "realtime", "vault",
    "supabase_migrations", "pg_catalog", "information_schema",
}


def load_env_vars(env_path: Path) -> dict:
    """Load DATABASE_* vars from a .env file."""
    if not env_path.exists():
        print(f"[ERROR] .env file not found: {env_path}")
        sys.exit(1)

    load_dotenv(dotenv_path=env_path)

    required = ["DATABASE_URL", "DATABASE_USERNAME", "DATABASE_PASSWORD"]
    env = {}
    for key in required:
        val = os.getenv(key)
        if not val:
            print(f"[ERROR] Missing {key} in {env_path}")
            sys.exit(1)
        env[key] = val
    return env


def build_sqlalchemy_url(env: dict) -> str:
    """
    Convert JDBC URL to SQLAlchemy connection string.

    Input JDBC:
      jdbc:postgresql://host:port/db?sslmode=require&prepareThreshold=0
    Output:
      postgresql+psycopg2://user:pass@host:port/db?sslmode=require
    """
    jdbc = env["DATABASE_URL"]
    match = re.match(r"jdbc:postgresql://(.+?)(?:\?|$)", jdbc)
    if not match:
        print(f"[ERROR] Cannot parse JDBC URL: {jdbc}")
        sys.exit(1)

    host_part = match.group(1)  # e.g. aws-xxx:6543/postgres
    params_part = jdbc.split("?", 1)[1] if "?" in jdbc else ""

    username = quote_plus(env["DATABASE_USERNAME"])
    password = quote_plus(env["DATABASE_PASSWORD"])

    sa_url = f"postgresql+psycopg2://{username}:{password}@{host_part}"

    if "sslmode=require" in params_part:
        sa_url += "?sslmode=require"

    return sa_url


def get_all_tables(engine, all_schemas: bool) -> list[tuple[str, str]]:
    """Get list of (schema, table_name) tuples, optionally excluding internal schemas."""
    sql_exclude = ""
    if not all_schemas:
        internal_list = "', '".join(sorted(SUPABASE_INTERNAL_SCHEMAS))
        sql_exclude = f"AND table_schema NOT IN ('{internal_list}')"

    with engine.connect() as conn:
        result = conn.execute(
            text(f"""
                SELECT table_schema, table_name
                FROM information_schema.tables
                WHERE table_type = 'BASE TABLE'
                  AND table_schema NOT IN ('pg_catalog', 'information_schema')
                  AND table_schema NOT LIKE 'pg_%%'
                  {sql_exclude}
                ORDER BY table_schema, table_name
            """)
        )
        return [(row.table_schema, row.table_name) for row in result]


def get_table_row_count(engine, schema: str, table: str) -> int:
    """Fast approximate row count via pg_stat_user_tables."""
    with engine.connect() as conn:
        row = conn.execute(
            text("""
                SELECT n_live_tup::bigint
                FROM pg_stat_user_tables
                WHERE schemaname = :s AND relname = :t
            """),
            {"s": schema, "t": table},
        ).fetchone()
        return row[0] if row else 0


def export_table(engine, schema: str, table: str, output_dir: Path, fmt: str) -> bool:
    """Export a single table to CSV or JSON."""
    safe_name = f"{schema}__{table}"
    ext = "csv" if fmt == "csv" else "json"
    file_path = output_dir / f"{safe_name}.{ext}"

    print(f"  -> {schema}.{table} ... ", end="", flush=True)

    try:
        df = pd.read_sql_table(table, engine, schema=schema)
    except Exception as e:
        print(f"[ERROR] {e}")
        return False

    if fmt == "csv":
        df.to_csv(file_path, index=False, quoting=csv.QUOTE_ALL)
    else:
        df.to_json(file_path, orient="records", indent=2, force_ascii=False)

    print(f"{len(df):,} rows")
    return True


def main():
    parser = argparse.ArgumentParser(
        description="Backup Supabase tables to CSV/JSON files."
    )
    parser.add_argument("--env", default=None,
                        help="Path to .env file (default: <repo_root>/.env)")
    parser.add_argument("--output", "-o", default=None,
                        help="Output directory (default: ./backups/backup_<timestamp>)")
    parser.add_argument("--format", "-f", choices=["csv", "json"], default="csv",
                        help="Export format (default: csv)")
    parser.add_argument("--schema", default=None,
                        help="Only export tables from this schema (e.g. public)")
    parser.add_argument("--all-schemas", action="store_true",
                        help="Include Supabase internal schemas (auth, storage, ...)")
    args = parser.parse_args()

    # --- Resolve paths ---
    script_dir = Path(__file__).resolve().parent  # scripts/
    repo_root = script_dir.parent                  # devorbit-api/

    env_path = Path(args.env) if args.env else repo_root / ".env"

    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_dir = Path(args.output) if args.output else repo_root / "backups" / f"backup_{timestamp}"

    # --- Load & connect ---
    print(f"[ENV] Loading environment from: {env_path}")
    env = load_env_vars(env_path)

    sa_url = build_sqlalchemy_url(env)
    print(f"[DB] Connecting to Supabase PostgreSQL ...")
    engine = create_engine(sa_url, pool_pre_ping=True, pool_size=2)

    try:
        with engine.connect() as conn:
            version = conn.execute(text("SELECT version()")).scalar()
        print(f"[OK] Connected: {version}")
    except Exception as e:
        print(f"[ERROR] Connection failed: {e}")
        sys.exit(1)

    # --- Discover tables ---
    tables = get_all_tables(engine, args.all_schemas)

    if args.schema:
        tables = [(s, t) for s, t in tables if s == args.schema]

    if not tables:
        print("[WARN] No tables found to export.")
        sys.exit(0)

    scope = "all schemas" if args.all_schemas else "public + user schemas (use --all-schemas for internal)"
    print(f"\n[INFO] Found {len(tables)} table(s) [{scope}]:")
    for s, t in tables:
        cnt = get_table_row_count(engine, s, t)
        print(f"   * {s}.{t} (~{cnt:,} rows)")

    # --- Export ---
    output_dir.mkdir(parents=True, exist_ok=True)
    print(f"\n[INFO] Exporting to: {output_dir}/\n")

    ok_count = 0
    fail_count = 0
    for schema, table in tables:
        if export_table(engine, schema, table, output_dir, args.format):
            ok_count += 1
        else:
            fail_count += 1

    # --- Summary ---
    print(f"\n{'='*50}")
    print("[SUMMARY] Backup complete!")
    print(f"   Path:   {output_dir}")
    print(f"   Format: {args.format}")
    print(f"   OK:     {ok_count} table(s)")
    print(f"   Fail:   {fail_count} table(s)")
    print(f"{'='*50}")

    engine.dispose()


if __name__ == "__main__":
    main()
