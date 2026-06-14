# 0004 Supabase Backend-Owned Database Posture

Date: 2026-06-13

## Status

Accepted

## Context

DevOrbit runs against Supabase PostgreSQL through the Spring Boot backend. The
repository contains SQL files under `devorbit-api/src/main/resources/db/migration`,
and some docs refer to Flyway, but the backend currently has no Flyway dependency
and `devorbit-api/run.bat` starts with Hibernate `JPA_DDL_AUTO=update`.

The existing migration folder is not safe to enable blindly:

- Some versions are duplicated (`V003`, `V004`, `V007`).
- Some migrations are historical data loads or destructive embedding resets.
- The live Supabase migration history only contains the Supabase-applied remote
  migrations, not every repository SQL file.

## Decision

Treat the live Supabase database as backend-owned. Application users access data
through Spring Boot APIs, not direct Supabase Data API table access.

For Supabase RLS:

- Public direct writes to backend-owned tables are denied.
- Backend-only tables have explicit deny-all policies for `anon` and
  `authenticated`.
- Public read/write exceptions must be documented in migrations and tied to a
  real product path.

For schema evolution:

- Continue applying production Supabase hardening through explicit reviewed SQL
  migrations.
- Keep backend-owned Supabase hardening idempotent in
  `SupabaseDatabaseHardeningInitializer` until Flyway is safe to enable, so
  `devorbit-api/run.bat` can maintain the required DB posture.
- Do not enable Flyway automatically until the migration history is reconciled
  and duplicate/destructive migration files are cleaned up.

## Alternatives Considered

1. Enable Flyway immediately. Rejected because duplicate versions and historical
   destructive/data migrations could break startup or mutate production data.
2. Leave RLS tables with no policies. Rejected because the posture is implicit
   and Supabase advisors report it as ambiguous.

## Consequences

Positive:

- Runtime access remains through the tested backend JWT/admin/student flows.
- Direct Data API posture is explicit and deny-by-default.
- Supabase advisor noise is reduced to items that need separate, deliberate work.

Tradeoffs:

- Repository SQL migrations are still not the automatic backend migration source.
- A follow-up migration cleanup is required before Flyway can safely become the
  backend migration runner.

## Follow-Up

- Reconcile duplicate migration versions and decide whether to preserve, merge,
  or retire historical data-load SQL files.
- After migration history is clean, evaluate Flyway with `baseline-on-migrate`
  on existing Supabase databases and a fresh local database bootstrap test.
- `vector` was moved to the `extensions` schema after backend DDL and entity
  mappings were changed to use `extensions.vector`.
