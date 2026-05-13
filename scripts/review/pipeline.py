"""Review pipeline orchestrator."""

import time
import asyncio
import sys
import threading

from review.models import ReviewResult, RepoCandidate, HeuristicSignals, AuditEntry
from review.github import fetch_owner_profile, fetch_repo_tree
from review.llm import call_llm, extract_json, validate_review_json, SYSTEM_PROMPT, LLMError, JSONParseError
from review.heuristics import extract_heuristic_signals, is_empty_repo, is_likely_spam
from review.scoring import compute_weighted_score
from review.prompts import build_profile_block, build_user_prompt
from review.audit import append_audit
from review.config import CANDIDATE_TIMEOUT


class TimeoutError_(Exception):
    pass


def _run_with_timeout(fn, timeout_sec: float):
    """Cross-platform timeout using threading.Timer (works on Windows)."""
    result = [None]
    exception = [None]
    finished = threading.Event()

    def worker():
        try:
            result[0] = fn()
        except BaseException as e:
            exception[0] = e
        finally:
            finished.set()

    t = threading.Thread(target=worker, daemon=True)
    t.start()
    ok = finished.wait(timeout_sec)
    if not ok:
        raise TimeoutError_(f"Timed out after {timeout_sec}s")
    if exception[0]:
        raise exception[0]
    return result[0]


def review_one(c: RepoCandidate, dry_run: bool = False) -> ReviewResult:
    """Review a single candidate with all stages: heuristic → LLM → scoring."""
    start = time.time()
    heuristic_signals = extract_heuristic_signals(c)

    # ── GitHub profile ──
    profile = fetch_owner_profile(c.githubOwner)
    profile_block = build_profile_block(profile)

    # Cập nhật thêm MSSV check từ profile (name, bio)
    if profile:
        from review.heuristics import detect_uit_student_id, detect_uit_keywords
        profile_texts = [profile.name, profile.bio, profile.login]
        if detect_uit_student_id(profile_texts) or detect_uit_keywords(profile_texts):
            heuristic_signals.is_uit_by_id = True

    # ── Spare repo? fetch file tree for extra context ──
    is_sparse = is_empty_repo(heuristic_signals) or is_likely_spam(heuristic_signals)
    if is_sparse:
        tree = fetch_repo_tree(c.githubOwner, c.githubName)
    else:
        tree = None

    # ── LLM review ──
    user_prompt = build_user_prompt(c, profile_block, tree_block=tree)
    raw_response = call_llm(None, user_prompt)

    try:
        data = extract_json(raw_response)
        data = validate_review_json(data)
    except (JSONParseError, ValueError) as e:
        print(f"  ⚠️  LLM parse error: {e}")
        print(f"  📝 Raw LLM Response: [{raw_response}]")
        print(f"  ⏭️  Falling back to MANUAL...")
        elapsed = (time.time() - start) * 1000
        result = ReviewResult(
            candidate_id=c.id,
            repo_name=c.githubName,
            is_uit_student=False,
            is_course_relevant=False,
            has_educational_value=False,
            score=3,
            confidence=0.3,
            reasoning=f"LLM parse error — cần duyệt thủ công: {e}",
            suggested_action="MANUAL",
            raw_llm_response=raw_response,
            heuristic_signals=heuristic_signals,
            latency_ms=elapsed,
        )
        append_audit(AuditEntry(
            candidate_id=c.id, repo_name=c.githubName,
            prompt=user_prompt, raw_response=raw_response,
            result=result,
        ))
        return result

    # ── Weighted scoring ──
    final_score, final_action = compute_weighted_score(
        is_uit=data["is_uit_student"],
        is_course_rel=data["is_course_relevant"],
        has_edu_val=data["has_educational_value"],
        llm_score=data["score"],
        confidence=data["confidence"],
        heuristics=heuristic_signals,
    )

    elapsed = (time.time() - start) * 1000

    result = ReviewResult(
        candidate_id=c.id,
        repo_name=c.githubName,
        is_uit_student=data["is_uit_student"],
        is_course_relevant=data["is_course_relevant"],
        has_educational_value=data["has_educational_value"],
        score=final_score,
        confidence=data["confidence"],
        reasoning=data["reasoning"],
        suggested_action=final_action,
        raw_llm_response=raw_response,
        latency_ms=elapsed,
        heuristic_signals=heuristic_signals,
    )

    append_audit(AuditEntry(
        candidate_id=c.id, repo_name=c.githubName,
        prompt=user_prompt, raw_response=raw_response,
        result=result,
    ))

    return result


def review_with_timeout(c: RepoCandidate, timeout_sec: int = CANDIDATE_TIMEOUT,
                        dry_run: bool = False) -> ReviewResult:
    try:
        return _run_with_timeout(lambda: review_one(c, dry_run=dry_run), timeout_sec)
    except TimeoutError_:
        return ReviewResult(
            candidate_id=c.id, repo_name=c.githubName,
            is_uit_student=False, is_course_relevant=False,
            has_educational_value=False, score=0,
            confidence=0.0,
            reasoning=f"Timeout (>{timeout_sec}s) — cần duyệt thủ công",
            suggested_action="MANUAL",
        )
    finally:
        pass


# ─── Async batch ────────────────────

async def review_batch(candidates: list[RepoCandidate],
                       dry_run: bool = False,
                       max_concurrency: int = 5) -> list[ReviewResult]:
    sem = asyncio.Semaphore(max_concurrency)

    async def _worker(c: RepoCandidate, idx: int, total: int) -> ReviewResult:
        async with sem:
            print(f"[{idx}/{total}] Task started: {c.githubOwner}/{c.githubName}", flush=True)
            loop = asyncio.get_running_loop()
            res = await loop.run_in_executor(None, review_with_timeout, c, CANDIDATE_TIMEOUT, dry_run)
            print(f"[{idx}/{total}] Task finished: {c.githubName} -> {res.suggested_action}", flush=True)
            return res

    tasks = [_worker(c, i, len(candidates)) for i, c in enumerate(candidates, 1)]
    return await asyncio.gather(*tasks, return_exceptions=False)
