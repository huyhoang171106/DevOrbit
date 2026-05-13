#!/usr/bin/env python3
"""
Mô tả chi tiết từng GitHub repo đã duyệt (active=true) bằng LLM.

Kế thừa cấu hình từ review/ module (config.py → LLM_URL, LLM_API_KEY, LLM_MODEL).
Luồng:
  1. Gọi GET /api/courses → danh sách môn học
  2. Với mỗi môn, gọi GET /api/courses/{id} → danh sách repo đã duyệt
  3. Clone từng repo GitHub về temp dir
  4. Quét file, gửi LLM phân tích chi tiết
  5. Ghi mô tả ra JSON

Usage:
  python scripts/describe-repos.py [OPTIONS]

Options:
  --api-base URL        Backend URL (mặc định: http://localhost:8080)
  --output FILE         File JSON đầu ra (mặc định: docs/repo-descriptions.json)
  --dry-run             Chỉ in danh sách repo, không clone/gọi LLM
  --course ID           Chỉ xử lý một course (theo ID)
  --max-files N         Giới hạn số file gửi LLM mỗi repo (mặc định: 40)
  --max-chars N         Giới hạn ký tự nội dung gửi LLM (mặc định: 20000)
  --temp-dir DIR        Thư mục tạm clone repo (mặc định: /tmp/devorbit-describe)
  --keep                Không xoá thư mục tạm sau khi chạy
  --skip-clone          Bỏ qua clone (dùng lại repo đã có trong temp-dir)
  --token TOKEN         GitHub token (tăng rate limit)
"""

import sys
import os
import json
import time
import argparse
import subprocess
import shutil
import urllib.request
import urllib.error
from pathlib import Path

# ─── Kế thừa config và LLM từ review module ──────
_SELF_DIR = Path(__file__).parent.resolve()
sys.path.insert(0, str(_SELF_DIR))

from review.config import LLM_URL, LLM_API_KEY, LLM_MODEL
from review.llm import call_llm, extract_json, LLMError, JSONParseError

# ─── Skip patterns khi quét file ────────────────
SKIP_PATTERNS = {
    ".git", "node_modules", "__pycache__", ".next", "dist", "build",
    ".turbo", ".vercel", ".cache", "coverage", ".nyc_output",
    ".gitnexus", "target", "bin", "obj", ".gradle",
    "*.pyc", "*.pyo", "*.so", "*.dll", "*.dylib",
    "*.png", "*.jpg", "*.jpeg", "*.gif", "*.svg", "*.ico", "*.hprof",
    "*.woff", "*.woff2", "*.ttf", "*.eot",
    "*.zip", "*.tar", "*.gz", "*.tgz",
    "*.lock", "package-lock.json", "yarn.lock", "pnpm-lock.yaml",
    ".DS_Store", "Thumbs.db",
}
SKIP_FILE_PATTERNS = {".classpath", ".factorypath", ".project", "tsconfig.tsbuildinfo"}


def _should_skip(name: str) -> bool:
    if name in SKIP_PATTERNS or name in SKIP_FILE_PATTERNS:
        return True
    for p in SKIP_PATTERNS:
        if p.startswith("*") and name.endswith(p[1:]):
            return True
    return False


# ═══════════════════════════════════════════════
#  API helpers
# ═══════════════════════════════════════════════

def api_get(url: str) -> dict | list:
    req = urllib.request.Request(url, headers={"User-Agent": "DevOrbit-Describe/1.0"})
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        body = e.read().decode()[:300]
        print(f"  HTTP {e.code}: {body}", file=sys.stderr)
        raise
    except Exception as e:
        print(f"  API error: {e}", file=sys.stderr)
        raise


def fetch_courses(api_base: str) -> list[dict]:
    """GET /api/courses → list of CourseSummaryResponse."""
    return api_get(f"{api_base}/api/courses")


def fetch_course_detail(api_base: str, course_id: int) -> dict:
    """GET /api/courses/{id} → CourseDetailResponse (bao gồm repos)."""
    return api_get(f"{api_base}/api/courses/{course_id}")


# ═══════════════════════════════════════════════
#  Git clone
# ═══════════════════════════════════════════════

def parse_github_url(url: str) -> tuple[str, str] | None:
    """Parse 'https://github.com/owner/repo' → (owner, repo)."""
    url = url.rstrip("/")
    if url.endswith(".git"):
        url = url[:-4]
    parts = url.split("/")
    if len(parts) >= 2 and "github.com" in url:
        try:
            owner = parts[-2]
            repo = parts[-1]
            return owner, repo
        except IndexError:
            return None
    return None


def clone_repo(github_url: str, dest: Path) -> bool:
    """Clone a GitHub repo to dest with depth=1. Returns True on success."""
    print(f"     Cloning {github_url} ...", flush=True)
    try:
        result = subprocess.run(
            ["git", "clone", "--depth=1", github_url, str(dest)],
            capture_output=True, text=True, timeout=120,
        )
        if result.returncode == 0:
            return True
        else:
            print(f"     Clone failed: {result.stderr.strip()[:200]}", file=sys.stderr)
            return False
    except subprocess.TimeoutExpired:
        print(f"     Clone timeout (>120s)", file=sys.stderr)
        return False
    except FileNotFoundError:
        print(f"     git not found in PATH", file=sys.stderr)
        return False


# ═══════════════════════════════════════════════
#  File scanning
# ═══════════════════════════════════════════════

def build_file_tree(dir_path: Path, max_files: int = 40) -> list[dict]:
    """Walk dir, collect file paths + contents, up to max_files."""
    entries = []
    try:
        for root, dirs, files in os.walk(dir_path):
            dirs[:] = [d for d in dirs if not _should_skip(d)]
            rel = Path(root).relative_to(dir_path) if root != str(dir_path) else Path(".")
            for f in sorted(files):
                if _should_skip(f):
                    continue
                fp = Path(root) / f
                try:
                    size = fp.stat().st_size
                except OSError:
                    continue
                if size > 500 * 1024:
                    entries.append({"path": str(rel / f) if str(rel) != "." else f, "size": size, "content": "[FILE TOO LARGE - SKIPPED]"})
                    continue
                try:
                    content = fp.read_text(encoding="utf-8", errors="replace")
                except (UnicodeDecodeError, OSError):
                    entries.append({"path": str(rel / f) if str(rel) != "." else f, "size": size, "content": "[BINARY FILE - SKIPPED]"})
                    continue
                entries.append({"path": str(rel / f) if str(rel) != "." else f, "size": size, "content": content})
                if len(entries) >= max_files:
                    return entries
    except (PermissionError, OSError) as e:
        entries.append({"path": f"[ERROR: {e}]", "size": 0, "content": ""})
    return entries


# ═══════════════════════════════════════════════
#  LLM prompt
# ═══════════════════════════════════════════════

def build_prompt(repo_name: str, course_code: str, course_name: str, entries: list[dict], max_chars: int = 20000) -> str:
    tree_lines = []
    content_lines = []
    total_chars = 0

    for e in entries:
        tree_lines.append(f"  {e['path']}  ({e['size']} bytes)")

    for e in entries:
        header = f"\n--- {e['path']} ---\n" if content_lines else f"--- {e['path']} ---\n"
        chunk = header + e["content"]
        if total_chars + len(chunk) > max_chars:
            remaining = max_chars - total_chars
            if remaining > 100:
                content_lines.append(chunk[:remaining] + "\n...[TRUNCATED]")
            break
        content_lines.append(chunk)
        total_chars += len(chunk)

    file_tree_str = "\n".join(tree_lines)
    file_contents_str = "".join(content_lines)

    prompt = f"""Repository: {repo_name}
Course: {course_code} - {course_name}

## File tree ({len(entries)} files):
{file_tree_str}

## File contents:
{file_contents_str}

---

Based on the file structure and contents above, analyze this repository in detail:

1. **Purpose**: What does this repo do? What course/project is it for?
2. **Tech stack**: Languages, frameworks, libraries used.
3. **Architecture**: Directory structure, design patterns, code organization.
4. **Key features**: Main functionality, modules, algorithms implemented.
5. **Code quality**: Readability, structure, comments, naming conventions.
6. **Educational value**: What can a student learn from this repo?

Return ONLY JSON:
{{
  "name": "{repo_name}",
  "course_code": "{course_code}",
  "course_name": "{course_name}",
  "short_description": "<1-2 sentence summary>",
  "tech_stack": ["tech1", "tech2", ...],
  "architecture_summary": "<2-3 sentence architecture>",
  "features": ["feature1", "feature2", ...],
  "code_quality_notes": "<notes on code readability/structure>",
  "educational_value": "<what students can learn>",
  "detailed_description": "<5-7 sentence detailed description>"
}}

JSON ONLY. No other text.
"""
    return prompt


# ═══════════════════════════════════════════════
#  Main
# ═══════════════════════════════════════════════

def main():
    parser = argparse.ArgumentParser(description="Describe approved GitHub repos using LLM")
    parser.add_argument("--api-base", default="http://localhost:8080", help="Backend URL")
    parser.add_argument("--output", default=str(_SELF_DIR.parent / "docs" / "repo-descriptions.json"), help="Output JSON file")
    parser.add_argument("--dry-run", action="store_true", help="List repos only, no clone/LLM")
    parser.add_argument("--course", type=int, default=None, help="Only process this course ID")
    parser.add_argument("--max-files", type=int, default=40, help="Max files per repo to send to LLM")
    parser.add_argument("--max-chars", type=int, default=20000, help="Max chars of file content per repo")
    parser.add_argument("--temp-dir", default="/tmp/devorbit-describe", help="Temp dir for clones")
    parser.add_argument("--keep", action="store_true", help="Don't delete temp dir after run")
    parser.add_argument("--skip-clone", action="store_true", help="Skip cloning (use existing temp dir)")
    parser.add_argument("--token", help="GitHub token")
    args = parser.parse_args()

    # Ensure LLM_API_KEY
    if not LLM_API_KEY:
        k = input("Enter LLM API key: ").strip()
        if not k:
            print("API key required.", file=sys.stderr)
            sys.exit(1)
        import review.config as cfg
        cfg.LLM_API_KEY = k
        import review.llm as llm_mod
        llm_mod.LLM_API_KEY = k

    # Set GitHub token
    gh_token = args.token or os.environ.get("GITHUB_TOKEN") or ""
    if gh_token:
        os.environ["GITHUB_TOKEN"] = gh_token

    # ─── Step 1: Fetch courses ─────────────────
    print("Fetching courses...", flush=True)
    try:
        courses = fetch_courses(args.api_base)
    except Exception as e:
        print(f"Failed to fetch courses: {e}", file=sys.stderr)
        sys.exit(1)

    if not courses:
        print("No courses found.", file=sys.stderr)
        sys.exit(1)

    print(f"Found {len(courses)} courses.")
    if args.course:
        courses = [c for c in courses if c.get("id") == args.course]
        if not courses:
            print(f"Course ID {args.course} not found.", file=sys.stderr)
            sys.exit(1)

    # ─── Step 2: Fetch repos from each course ───
    all_repos = []
    for c in courses:
        cid = c["id"]
        ccode = c.get("code", "?")
        cname = c.get("name", "?")
        repo_count = c.get("repoCount", 0)
        if repo_count == 0:
            print(f"  {ccode} {cname}: 0 repos, skip.")
            continue

        print(f"  {ccode} {cname}: fetching {repo_count} repos...", flush=True)
        try:
            detail = fetch_course_detail(args.api_base, cid)
            repos = detail.get("repos", [])
        except Exception as e:
            print(f"    Failed: {e}", file=sys.stderr)
            continue

        for r in repos:
            r["courseCode"] = ccode
            r["courseName"] = cname
            all_repos.append(r)

        time.sleep(0.2)  # be nice to backend

    if not all_repos:
        print("No approved repos found.")
        sys.exit(0)

    print(f"\nTotal repos to describe: {len(all_repos)}")

    # Dry-run: just list
    if args.dry_run:
        print("\n--- Repo list ---")
        for r in all_repos:
            gh_url = r.get("githubUrl", "?")
            parsed = parse_github_url(gh_url)
            owner_repo = f"{parsed[0]}/{parsed[1]}" if parsed else gh_url
            print(f"  [{r.get('courseCode','?')}] {owner_repo}  ({r.get('displayName','?')})")
        return

    # ─── Step 3: Clone & describe ──────────────
    temp_root = Path(args.temp_dir)
    if not args.skip_clone and temp_root.exists():
        shutil.rmtree(temp_root)
    temp_root.mkdir(parents=True, exist_ok=True)

    results = {}
    total = len(all_repos)

    for idx, r in enumerate(all_repos, 1):
        gh_url = r.get("githubUrl", "")
        parsed = parse_github_url(gh_url)
        if not parsed:
            print(f"\n[{idx}/{total}] Invalid githubUrl: {gh_url}", file=sys.stderr)
            continue

        owner, repo_name = parsed
        display_name = r.get("displayName") or repo_name
        course_code = r.get("courseCode", "?")
        course_name = r.get("courseName", "?")
        repo_key = f"{owner}/{repo_name}"

        print(f"\n[{idx}/{total}] {repo_key} ({course_code})", flush=True)

        # Clone or reuse
        repo_dir = temp_root / f"{owner}__{repo_name}"
        if args.skip_clone and repo_dir.is_dir():
            print(f"  Using existing: {repo_dir}")
        elif not args.skip_clone:
            ok = clone_repo(gh_url, repo_dir)
            if not ok:
                results[repo_key] = {"status": "clone_failed", "github_url": gh_url}
                continue
        else:
            print(f"  Skipped clone, dir not found: {repo_dir}", file=sys.stderr)
            results[repo_key] = {"status": "no_clone_dir", "github_url": gh_url}
            continue

        # Scan files
        entries = build_file_tree(repo_dir, max_files=args.max_files)
        print(f"  {len(entries)} files scanned")

        # Call LLM
        prompt = build_prompt(repo_name, course_code, course_name, entries, max_chars=args.max_chars)
        print(f"  Calling LLM...", flush=True)
        t0 = time.time()

        try:
            raw = call_llm(None, prompt)
        except LLMError as e:
            print(f"  LLM error: {e}", file=sys.stderr)
            results[repo_key] = {"status": "llm_error", "error": str(e), "github_url": gh_url}
            continue

        elapsed = time.time() - t0
        print(f"  Response in {elapsed:.1f}s")

        try:
            data = extract_json(raw)
        except JSONParseError as e:
            print(f"  Parse error: {e}", file=sys.stderr)
            results[repo_key] = {"status": "parse_error", "raw_response": raw[:500], "github_url": gh_url}
            continue

        results[repo_key] = {
            "status": "ok",
            "repo_name": repo_name,
            "display_name": display_name,
            "github_url": gh_url,
            "course_code": course_code,
            "course_name": course_name,
            "short_description": data.get("short_description", ""),
            "tech_stack": data.get("tech_stack", []),
            "architecture_summary": data.get("architecture_summary", ""),
            "features": data.get("features", []),
            "code_quality_notes": data.get("code_quality_notes", ""),
            "educational_value": data.get("educational_value", ""),
            "detailed_description": data.get("detailed_description", ""),
        }

        preview = data.get("short_description", "")[:80]
        print(f"  {' '.join(data.get('tech_stack', []))[:60]} — {preview}...")

        # Be nice to API
        time.sleep(0.5)

    # ─── Cleanup ────────────────────────────────
    if not args.keep and not args.skip_clone and temp_root.exists():
        shutil.rmtree(temp_root)

    # ─── Save output ────────────────────────────
    output_path = Path(args.output)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(results, indent=2, ensure_ascii=False), encoding="utf-8")

    ok_count = sum(1 for v in results.values() if v.get("status") == "ok")
    fail_count = sum(1 for v in results.values() if v.get("status") != "ok")
    print(f"\n{'='*60}")
    print(f"Done! Saved to {output_path}")
    print(f"OK: {ok_count}, Failed: {fail_count}, Total: {len(results)}")


if __name__ == "__main__":
    main()
