import os
from pathlib import Path

# ─── Load .env ──────────────────────
_dotenv = Path(__file__).parent.parent / ".env"
if _dotenv.exists():
    with open(_dotenv, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, _, val = line.partition("=")
            key = key.strip()
            val = val.strip().strip("\"'")
            if not os.environ.get(key):  # only set if not already exported
                os.environ[key] = val

# ─── Admin ──────────────────────────
ADMIN_USER = os.environ.get("ADMIN_USER", "admin")
ADMIN_PASS = os.environ.get("ADMIN_PASS", "admin123")

# ─── LLM ────────────────────────────
LLM_URL = "https://opencode.ai/zen/go/v1/chat/completions"
LLM_MODEL = "deepseek-v4-flash"
LLM_API_KEY = os.environ.get("LLM_API_KEY", "")

# ─── API ────────────────────────────
API_BASE = os.environ.get("API_BASE", "http://localhost:8080")

# ─── GitHub ─────────────────────────
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN", "")
GITHUB_MAX_RETRY = 3
GITHUB_BASE_DELAY = 1  # seconds

# ─── Pipeline ───────────────────────
CANDIDATE_TIMEOUT = 120       # seconds per candidate
MAX_CONCURRENCY = 5           # async semaphore
DEFAULT_DELAY = 0.5           # delay between LLM calls
README_MAX_CHARS = 1500
PROFILE_README_MAX_CHARS = 1000

# ─── Scoring weights ────────────────
WEIGHT_COURSE_RELEVANCE = 0.40
WEIGHT_EDUCATIONAL_QUALITY = 0.35
WEIGHT_UIT_CONFIDENCE = 0.25
