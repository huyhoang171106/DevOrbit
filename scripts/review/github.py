import time
import urllib.request
import urllib.error
import json
import base64

from review.config import GITHUB_TOKEN, GITHUB_MAX_RETRY, GITHUB_BASE_DELAY
from review.models import GithubOwnerProfile

# ─── In-memory cache ────────────────
_profile_cache: dict[str, GithubOwnerProfile | None] = {}


def _gh_headers() -> dict[str, str]:
    h = {
        "Accept": "application/vnd.github.v3+json",
        "User-Agent": "DevOrbit-AutoReview/1.0",
    }
    if GITHUB_TOKEN:
        h["Authorization"] = f"Bearer {GITHUB_TOKEN}"
    return h


def _gh_get(url: str) -> dict | list | None:
    last_err = None
    for attempt in range(GITHUB_MAX_RETRY):
        try:
            req = urllib.request.Request(url, headers=_gh_headers())
            with urllib.request.urlopen(req, timeout=15) as resp:
                return json.loads(resp.read().decode())
        except urllib.error.HTTPError as e:
            code = e.code
            if code == 403:
                body = e.read().decode()[:200]
                # Respect Retry-After
                retry_after = resp.headers.get("Retry-After") if attempt > 0 else None
                wait = int(retry_after) if retry_after else min(GITHUB_BASE_DELAY * (2 ** attempt), 60)
                print(f"  ⚠️  GitHub 403 rate limited, retry in {wait}s: {body}")
                time.sleep(wait)
                last_err = e
                continue
            elif code == 404:
                return None
            elif code == 409:
                # 409 Conflict usually means repo is empty (no commits)
                return {"tree": []}
            else:
                print(f"  ⚠️  GitHub API error {code}")
                return None
        except (urllib.error.URLError, TimeoutError) as e:
            wait = GITHUB_BASE_DELAY * (2 ** attempt)
            print(f"  ⚠️  GitHub timeout, retry in {wait}s: {e}")
            time.sleep(wait)
            last_err = e
            continue
    if last_err:
        print(f"  ❌ GitHub API failed after {GITHUB_MAX_RETRY} retries")
    return None


def fetch_owner_profile(owner: str) -> GithubOwnerProfile | None:
    if owner in _profile_cache:
        return _profile_cache[owner]

    data = _gh_get(f"https://api.github.com/users/{owner}")
    if not data or "login" not in data:
        _profile_cache[owner] = None
        return None

    profile = GithubOwnerProfile(
        login=data.get("login", owner),
        name=data.get("name"),
        bio=data.get("bio"),
        location=data.get("location"),
        company=data.get("company"),
        blog=data.get("blog"),
        email=data.get("email"),
        twitter=data.get("twitter_username"),
        public_repos=data.get("public_repos", 0),
        followers=data.get("followers", 0),
        profile_readme=None,
        organizations=None,
        recent_repos=None,
    )

    # Profile README
    rd = _gh_get(f"https://api.github.com/repos/{owner}/{owner}/readme")
    if rd and "content" in rd:
        profile.profile_readme = base64.b64decode(rd["content"]).decode("utf-8", errors="replace")

    # Orgs
    orgs = _gh_get(f"https://api.github.com/users/{owner}/orgs")
    if orgs and isinstance(orgs, list) and orgs:
        profile.organizations = [o.get("login", "") for o in orgs if o.get("login")]

    # Recent public repos
    repos = _gh_get(f"https://api.github.com/users/{owner}/repos?sort=pushed&per_page=5&type=public")
    if repos and isinstance(repos, list) and repos:
        profile.recent_repos = [
            f"{r.get('name', '?')} ({r.get('description', 'no desc') or 'no desc'})"
            for r in repos
        ]

    _profile_cache[owner] = profile
    return profile


def fetch_repo_tree(owner: str, repo: str, max_entries: int = 50) -> str:
    """Fetch repo file tree (recursive) via GitHub Contents API. Returns formatted string."""
    data = _gh_get(f"https://api.github.com/repos/{owner}/{repo}/git/trees/HEAD?recursive=1")
    if not data or "tree" not in data:
        return "N/A"
    entries = data["tree"]
    # Filter to code-related files, limit
    lines = []
    for e in entries[:max_entries]:
        path = e.get("path", "?")
        etype = e.get("type", "blob")
        lines.append(f"{'📁' if etype == 'tree' else '📄'} {path}")
    return "\n".join(lines) if lines else "N/A"


def clear_profile_cache():
    _profile_cache.clear()
