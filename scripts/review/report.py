"""Report & observability."""

import time
from review.models import ReviewResult


def _elapsed_str(s: float) -> str:
    if s < 60:
        return f"{s:.0f}s"
    return f"{s/60:.1f}m"


def print_report(results: list[ReviewResult], total_elapsed: float):
    approved = [r for r in results if r.suggested_action == "APPROVE"]
    rejected = [r for r in results if r.suggested_action == "REJECT"]
    manual = [r for r in results if r.suggested_action == "MANUAL"]

    avg_latency = sum(r.latency_ms for r in results) / len(results) if results else 0

    print(f"\n{'='*60}")
    print(f"KẾT QUẢ AUTO-REVIEW")
    print(f"{'='*60}")
    print(f"Tổng: {len(results)}")
    print(f"  ✅ Cần duyệt (APPROVE): {len(approved)}")
    print(f"  ❌ Tự động REJECT:      {len(rejected)}")
    print(f"  ⚠️  Cần duyệt (MANUAL):  {len(manual)}")
    print(f"  ⏱  Thời gian: {_elapsed_str(total_elapsed)} | Trung bình: {avg_latency:.0f}ms/candidate")
    print()

    if approved:
        print("── CẦN DUYỆT THỦ CÔNG (APPROVE) ──")
        for r in approved:
            print(f"  [{r.score}/10] {r.repo_name} (conf={r.confidence:.2f}): {r.reasoning[:120]}")
    if rejected:
        print("── TỰ ĐỘNG REJECT ──")
        for r in rejected:
            print(f"  [{r.score}/10] {r.repo_name}: {r.reasoning[:120]}")
    if manual:
        print("── CẦN DUYỆT THỦ CÔNG (MANUAL) ──")
        for r in manual:
            print(f"  [{r.score}/10] {r.repo_name} (conf={r.confidence:.2f}): {r.reasoning[:120]}")

    # Aggregate stats
    print(f"\n── THỐNG KÊ ──")
    print(f"  avg latency: {avg_latency:.0f}ms")
    print(f"  rejected repos: {[r.repo_name for r in rejected]}")
    print(f"  audit trail: review_audit.jsonl")


def _truncate(s: str, n: int = 80) -> str:
    return s[:n] + "..." if len(s) > n else s
