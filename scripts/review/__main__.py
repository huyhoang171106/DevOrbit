#!/usr/bin/env python3
"""
Auto-review entry point.
Usage:
  export LLM_API_KEY="your-key"
  export GITHUB_TOKEN="ghp_..."  # optional
  python -m review [--dry-run] [--admin-token TOKEN] [--reviewer Ba��o]
"""

import argparse
import asyncio
import time
import sys

if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

from review.config import LLM_API_KEY, CANDIDATE_TIMEOUT, DEFAULT_DELAY, MAX_CONCURRENCY
from review.api import fetch_candidates, reject_candidate, update_review_note
from review.pipeline import review_one, review_batch
from review.report import print_report


def process_result(c, result, admin_token, dry_run=False):
    """Helper to save side effects (reject/note) to backend."""
    if dry_run or not admin_token:
        return

    try:
        if result.suggested_action == "APPROVE":
            from review.api import approve_candidate
            approve_candidate(c.id, admin_token, result.reasoning)
            print(f"    ✅ ID {c.id}: ĐÃ AUTO-APPROVE repo {c.githubName}")
        elif result.suggested_action == "REJECT":
            reject_candidate(c.id, admin_token)
            print(f"    ❌ ID {c.id}: Đã auto-reject repo {c.githubName}")
        else:
            # Lưu reasoning vào database để không duyệt lại lần sau
            note = f"[AI {result.score}/10]: {result.reasoning}"
            update_review_note(c.id, note, admin_token)
            print(f"    ⚠️  ID {c.id}: Đã lưu AI note cho {c.githubName} (Chờ duyệt tay)")
    except Exception as e:
        print(f"    ⚠️  Lỗi khi lưu kết quả ID {c.id}: {e}")


def main():
    parser = argparse.ArgumentParser(description="Auto-review repo candidates bằng LLM")
    parser.add_argument("--dry-run", action="store_true", help="Chỉ phân loại, không gọi API")
    parser.add_argument("--admin-token", help="Admin JWT token (cần để auto-reject)")
    parser.add_argument("--reviewer", default="all", help="Filter: all, Bảo, Bắc, An")
    parser.add_argument("--max", type=int, default=0, help="Số candidate tối đa (0 = tất cả)")
    parser.add_argument("--delay", type=float, default=DEFAULT_DELAY, help="Delay giữa các candidate (giây)")
    parser.add_argument("--timeout", type=int, default=CANDIDATE_TIMEOUT, help="Timeout mỗi candidate")
    parser.add_argument("--concurrency", type=int, default=1, help="Số luồng song song (dùng async)")
    parser.add_argument("--list-reviewers", action="store_true", help="Chỉ list candidate không review")
    parser.add_argument("--api-base", default=None, help="Override API_BASE (VD: https://your-domain.com)")

    parser.add_argument("--re-review", action="store_true", help="Duyệt lại cả các candidate đã có reviewNote")
    
    args = parser.parse_args()

    # Fix #1: API key update
    global LLM_API_KEY  # noqa — rebind module-level var
    # ... (rest of key logic) ...
    from review.config import LLM_API_KEY as _key
    if not _key:
        k = input("Nhập LLM API key: ").strip()
        if not k:
            print("Cần API key để chạy.", file=sys.stderr)
            sys.exit(1)
        import review.config
        review.config.LLM_API_KEY = k
        from review.llm import LLM_API_KEY as _lk  # trigger reimport
        import review.llm
        review.llm.LLM_API_KEY = k

    # Override API_BASE nếu có --api-base
    if args.api_base:
        import review.config
        review.config.API_BASE = args.api_base

    t0 = time.time()

    # Tự động lấy token nếu chưa có
    if not args.admin_token:
        from review.api import login
        import review.config
        print(f"🔑 Đang đăng nhập admin ({review.config.ADMIN_USER})...")
        try:
            args.admin_token = login()
            print("✅ Đăng nhập thành công!")
        except Exception:
            print("❌ Không thể tự động đăng nhập.")

    print(f"📡 Đang fetch candidates từ {args.api_base or __import__('os').environ.get('API_BASE', 'http://localhost:8080')}...", flush=True)
    try:
        candidates = fetch_candidates(args.admin_token, args.reviewer)
    except Exception as e:
        # Nếu fail 403, thử login lại phát nữa cho chắc
        if "403" in str(e):
            from review.api import login
            print("🔄 Token hết hạn, đang thử login lại...")
            args.admin_token = login()
            candidates = fetch_candidates(args.admin_token, args.reviewer)
        else:
            raise
            
    if args.re_review:
        pending = [c for c in candidates if c.status == "NEW"]
        skipped = []
    else:
        pending = [c for c in candidates if c.status == "NEW" and not c.reviewNote]
        skipped = [c for c in candidates if c.status == "NEW" and c.reviewNote]
        
    print(f"  Tổng: {len(candidates)} | Pending: {len(pending)} | Đã có note (skip): {len(skipped)}")

    if args.list_reviewers:
        for c in candidates:
            print(f"  id={c.id:>5}  {c.githubOwner}/{c.githubName:<40} status={c.status:<10} reviewer={c.assignedReviewer}")
        return

    if args.max > 0:
        pending = pending[:args.max]

    if not pending:
        print("Không có candidate nào cần review.")
        return

    print(f"Sẽ review {len(pending)} candidates (concurrency={args.concurrency})...\n", flush=True)

    results = []
    if args.concurrency > 1:
        # Async batch
        sem = asyncio.Semaphore(args.concurrency)

        async def _worker(c, idx, total):
            async with sem:
                try:
                    print(f"[{idx}/{total}] Reviewing {c.githubOwner}/{c.githubName}...", flush=True)
                    loop = asyncio.get_running_loop()
                    from review.pipeline import review_with_timeout
                    res = await loop.run_in_executor(None, review_with_timeout, c, args.timeout, args.dry_run)
                    print(f"[{idx}/{total}] → {res.suggested_action} ({res.score}/10)", flush=True)

                    # Side effects
                    await loop.run_in_executor(None, process_result, c, res, args.admin_token, args.dry_run)
                    return res
                except Exception as e:
                    print(f"[{idx}/{total}] ❌ Critical error: {e}", flush=True)
                    return None

        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        tasks = [_worker(c, i, len(pending)) for i, c in enumerate(pending, 1)]
        results = loop.run_until_complete(asyncio.gather(*tasks))
        loop.close()
    else:
        # Sequential with delay
        for i, c in enumerate(pending, 1):
            print(f"[{i}/{len(pending)}] Reviewing {c.githubOwner}/{c.githubName}...", flush=True)
            try:
                from review.pipeline import review_with_timeout
                result = review_with_timeout(c, timeout_sec=args.timeout, dry_run=args.dry_run)
                results.append(result)
                print(f"  → {result.suggested_action} ({result.score}/10, conf={result.confidence:.2f}) [{result.latency_ms:.0f}ms]: {result.reasoning[:120]}", flush=True)

                process_result(c, result, args.admin_token, args.dry_run)

            except Exception as e:
                print(f"  ❌ Lỗi: {e}", file=sys.stderr)
                continue

            if i < len(pending):
                time.sleep(args.delay)

    total_elapsed = time.time() - t0
    print_report(results, total_elapsed)

    # Hit rate metrics
    if results:
        approved = sum(1 for r in results if r.suggested_action == "APPROVE")
        rejected = sum(1 for r in results if r.suggested_action == "REJECT")
        manual = sum(1 for r in results if r.suggested_action == "MANUAL")
        auto_processed = approved + rejected
        print(f"  auto-rate: {auto_processed}/{len(results)} ({auto_processed/len(results)*100:.0f}%)")


if __name__ == "__main__":
    main()
