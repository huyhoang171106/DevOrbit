# Scripts

This directory contains automation scripts for the DevOrbit project.

## Harness Installer

The upstream installer applies the Harness v0 operating files and folder
structure to a target project directory. It defaults to the current directory,
accepts a target path, and asks interactive users whether to `1. Merge`,
`2. Override`, or `3. Stop` when the target already contains `AGENTS.md`,
`docs/`, or `scripts/`.
Non-interactive installs stop on those protected paths unless `--merge` or
`--override` is provided.

```bash
curl -fsSL "https://raw.githubusercontent.com/hoangnb24/harness-experimental/main/scripts/install-harness.sh?$(date +%s)" | bash -s -- --yes
```

```bash
curl -fsSL "https://raw.githubusercontent.com/hoangnb24/harness-experimental/main/scripts/install-harness.sh?$(date +%s)" | bash -s -- --merge --yes
```

The installer must stay limited to harness files. Do not use it to scaffold
application source folders, package scripts, CI, tests, platform shells, or fake
validation commands. The installer script is not part of the installed project
payload.

---

## LLM-Based Review Pipeline

### `auto-review-candidates.py`

Auto-review repo candidates using an LLM (Large Language Model).

**Entry point** — delegates to the `review/` module for actual processing.

**Usage:**
```bash
set LLM_API_KEY=your-key
python scripts/auto-review-candidates.py [--dry-run] [--admin-token TOKEN]
```

**Features:**
- Scans unapproved repo candidates
- Sends repo structure + contents to LLM for analysis
- Outputs review decisions to `review_audit.jsonl`
- Dry-run mode to preview without LLM calls

### `describe-repos.py`

Generates detailed descriptions for approved repos (active=true) using LLM.

**Flow:**
1. Fetches course list from backend API
2. For each course, fetches approved repos
3. Clones each repo to a temp directory (depth=1)
4. Scans files and sends structure + contents to LLM
5. Saves structured JSON descriptions

**Usage:**
```bash
set LLM_API_KEY=your-key
python scripts/describe-repos.py [OPTIONS]
```

**Options:**
- `--api-base URL` — Backend URL (default: http://localhost:8080)
- `--output FILE` — Output JSON path (default: docs/repo-descriptions.json)
- `--dry-run` — List repos only, no clone/LLM
- `--course ID` — Process a single course
- `--max-files N` — Max files per repo (default: 40)
- `--max-chars N` — Max characters per repo (default: 20000)
- `--temp-dir DIR` — Clone directory (default: /tmp/devorbit-describe)
- `--keep` — Keep temp directory after run
- `--skip-clone` — Reuse existing clones
- `--token TOKEN` — GitHub token for rate limiting

**Configuration:** Inherits LLM settings from `review/config.py` (LLM_URL, LLM_API_KEY, LLM_MODEL).

### `review/` Module

Python package containing the review pipeline logic:
- `__main__.py` — Entry point for auto-review
- `config.py` — LLM configuration (URL, API key, model)
- `llm.py` — LLM client utilities (call_llm, extract_json)
- `scanner.py` — Repo scanning and file analysis
- `audit.py` — Audit trail management

### `requirements.txt`

Python dependencies for the review pipeline:
- Python 3.12+ required
- No external dependencies — uses only stdlib
- Optional: `python-dotenv` for `.env` auto-load

### `review_audit.jsonl`

Audit log of auto-review decisions. Each line is a JSON object with:
- `timestamp` — When the review was performed
- `repo_url` — GitHub URL of the candidate
- `decision` — approve / reject / flag
- `rationale` — LLM-generated explanation
- `reviewer` — Which reviewer handled it

### `scratch/`

Temporary working directory for scripts (gitignored).

---

## Future Command Contract

Expected future checks:

```text
validate:quick
  format, lint, typecheck, unit tests, architecture check

test:integration
  backend contract and integration checks

test:e2e
  user-visible end-to-end flows

test:platform
  platform shell smoke checks, if the project has a native shell

test:release
  full suite, log checks, and performance smoke
```
