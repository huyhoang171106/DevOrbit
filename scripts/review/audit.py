"""Audit trail — persist decisions for debug, improvement, hallucination detection."""

import json
import os
import datetime
from pathlib import Path

from review.models import AuditEntry, ReviewResult

_AUDIT_FILE = os.environ.get("REVIEW_AUDIT_FILE", str(Path(__file__).parent.parent / "review_audit.jsonl"))


def _now() -> str:
    return datetime.datetime.now(datetime.timezone.utc).isoformat()


def append_audit(entry: AuditEntry) -> None:
    entry.created_at = _now()
    try:
        with open(_AUDIT_FILE, "a", encoding="utf-8") as f:
            f.write(json.dumps({
                "candidate_id": entry.candidate_id,
                "repo_name": entry.repo_name,
                "prompt": entry.prompt,
                "raw_response": entry.raw_response,
                "result": {
                    "score": entry.result.score,
                    "confidence": entry.result.confidence,
                    "suggested_action": entry.result.suggested_action,
                    "reasoning": entry.result.reasoning,
                    "is_uit_student": entry.result.is_uit_student,
                    "is_course_relevant": entry.result.is_course_relevant,
                    "has_educational_value": entry.result.has_educational_value,
                },
                "human_override": entry.human_override,
                "created_at": entry.created_at,
            }, ensure_ascii=False) + "\n")
    except OSError as e:
        print(f"  ⚠️  Audit write failed: {e}", file=__import__('sys').stderr)
