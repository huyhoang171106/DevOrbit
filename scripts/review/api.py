"""DevOrbit API client — thin wrappers."""

import json
import sys
import urllib.error
import urllib.request

from review.config import API_BASE
from review.models import RepoCandidate


def login() -> str:
    from review.config import ADMIN_USER, ADMIN_PASS
    url = f"{API_BASE}/api/admin/auth/login"
    payload = json.dumps({
        "username": ADMIN_USER,
        "password": ADMIN_PASS
    }).encode()
    headers = {"Content-Type": "application/json"}
    
    req = urllib.request.Request(url, data=payload, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode())
            return data["token"]
    except Exception as e:
        print(f"  ❌ Login failed: {e}", file=sys.stderr)
        raise


import urllib.parse

def fetch_candidates(admin_token: str | None, reviewer: str = "all") -> list[RepoCandidate]:
    quoted_reviewer = urllib.parse.quote(reviewer)
    url = f"{API_BASE}/api/admin/repo-candidates?reviewer={quoted_reviewer}"
    headers = {"Content-Type": "application/json"}
    if admin_token:
        headers["Authorization"] = f"Bearer {admin_token}"

    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode())
        return [RepoCandidate(**d) for d in data]
    except urllib.error.HTTPError as e:
        body = e.read().decode()[:200]
        if e.code == 403:
            print(f"  ❌ API 403 Forbidden — token hết hạn hoặc không hợp lệ. Lấy token mới từ cookie auth_token.", file=sys.stderr)
        elif e.code == 404:
            print(f"  ❌ API 404 — server chạy chưa? API_BASE={API_BASE}", file=sys.stderr)
        elif e.code == 502:
            print(f"  ❌ API 502 Bad Gateway — backend server không hoạt động ở {API_BASE}", file=sys.stderr)
        else:
            print(f"  ❌ API error {e.code}: {body}", file=sys.stderr)
        raise


def approve_candidate(candidate_id: int, admin_token: str, reasoning: str):
    url = f"{API_BASE}/api/admin/repo-candidates/{candidate_id}/approve"
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {admin_token}",
    }
    # Backend's approveCandidate takes CandidateReviewRequest
    body = json.dumps({
        "reviewNote": f"[AUTO-APPROVED]: {reasoning}"
    }).encode()
    req = urllib.request.Request(url, data=body, headers=headers, method="POST")
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode())


def reject_candidate(candidate_id: int, admin_token: str):
    url = f"{API_BASE}/api/admin/repo-candidates/{candidate_id}/reject"
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {admin_token}",
    }
    body = json.dumps({}).encode()
    req = urllib.request.Request(url, data=body, headers=headers, method="POST")
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode())


def update_review_note(candidate_id: int, note: str, admin_token: str):
    url = f"{API_BASE}/api/admin/repo-candidates/{candidate_id}/review-note"
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {admin_token}",
    }
    payload = json.dumps({"reviewNote": note}).encode('utf-8')
    req = urllib.request.Request(url, data=payload, headers=headers, method="POST")
    with urllib.request.urlopen(req, timeout=15) as resp:
        return resp.read()
