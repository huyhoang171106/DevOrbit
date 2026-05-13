"""Weighted scoring & action decision engine."""

from review.config import WEIGHT_COURSE_RELEVANCE, WEIGHT_EDUCATIONAL_QUALITY, WEIGHT_UIT_CONFIDENCE
from review.models import ReviewResult, RepoCandidate, HeuristicSignals


def compute_weighted_score(is_uit: bool, is_course_rel: bool, has_edu_val: bool,
                           llm_score: int, confidence: float,
                           heuristics: HeuristicSignals | None = None) -> tuple[int, str]:
    """Compute 0-10 weighted score from all signals + decide action."""
    raw_weighted = 0.0

    # LLM raw score — 70% of total
    raw_weighted += (llm_score / 10.0) * 0.70

    # Boolean signals — 15%
    # Nếu heuristics phát hiện MSSV, ta tin tưởng hơn AI
    final_is_uit = is_uit or (heuristics.is_uit_by_id if heuristics else False)
    
    bool_score = 0.0
    if final_is_uit:
        bool_score += 0.4
    if is_course_rel:
        bool_score += 0.35
    if has_edu_val:
        bool_score += 0.25
    raw_weighted += bool_score * 0.15

    # Heuristic bonus — 15%
    if heuristics:
        h_score = 0.0
        if heuristics.has_readme:
            h_score += 0.35
        if heuristics.has_description:
            h_score += 0.15
        if heuristics.stars >= 3:
            h_score += 0.25
        elif heuristics.stars >= 1:
            h_score += 0.1
        if heuristics.forks >= 1:
            h_score += 0.1
        if heuristics.readme_len > 500:
            h_score += 0.05
        raw_weighted += h_score * 0.15

    final_score = max(0, min(10, round(raw_weighted * 10)))

    # Decision tree logic updated based on latest user feedback:
    # REJECT if: (Not UIT info) AND (No Edu Value OR No Course Relevance)
    
    # Nghĩa là nếu không phải UIT, thì BẮT BUỘC phải vừa có giá trị vừa đúng môn mới giữ.
    is_junk = (not final_is_uit) and ((not has_edu_val) or (not is_course_rel))
    
    if is_junk:
        action = "REJECT"
    elif final_score >= 7 and final_is_uit and is_course_rel:
        # Chắc chắn là sinh viên UIT và đúng môn -> Approve
        action = "APPROVE"
    elif final_score >= 9:
        # Điểm cực cao (thường là repo xịn) -> Approve
        action = "APPROVE"
    elif has_edu_val or is_course_rel:
        # Có giá trị hoặc đúng môn nhưng không chắc là UIT -> Để Manual cho người xem
        action = "MANUAL"
    else:
        action = "MANUAL"

    return final_score, action
