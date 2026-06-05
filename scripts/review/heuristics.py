"""Deterministic quality heuristics — no LLM needed."""

import re
from review.models import RepoCandidate, HeuristicSignals


def detect_uit_student_id(texts: list[str]) -> bool:
    """Quét MSSV UIT dạng 2x52xxxx (8 chữ số)."""
    # Pattern: 2, theo sau là 1 chữ số (khóa), rồi 52, rồi 4 chữ số (stt)
    # VD: 21521234, 22520001
    pattern = r"2[0-9]52[0-9]{4}"
    for t in texts:
        if t and re.search(pattern, str(t)):
            return True
    return False


def detect_uit_keywords(texts: list[str]) -> bool:
    """Quét các từ khóa khẳng định UIT trong tên/mô tả."""
    keywords = [r"\bUIT\b", r"\bDHCNTT\b", r"\bVNU-UIT\b", r"\bVNUUIT\b"]
    for t in texts:
        if not t: continue
        for kw in keywords:
            if re.search(kw, str(t), re.IGNORECASE):
                return True
    return False


def extract_heuristic_signals(c: RepoCandidate) -> HeuristicSignals:
    signals = HeuristicSignals(
        readme_len=len(c.readmeExcerpt or ""),
        has_readme=bool(c.readmeExcerpt and c.readmeExcerpt.strip()),
        has_description=bool(c.description and c.description.strip()),
        stars=c.stars,
        forks=c.forks,
        has_topics=bool(c.topics and c.topics.strip()),
        primary_language=c.primaryLanguage,
        is_uit_by_id=False,
    )
    
    # Tìm MSSV hoặc từ khóa UIT trong repo name, description
    texts = [c.githubName, c.description, c.githubOwner]
    signals.is_uit_by_id = detect_uit_student_id(texts) or detect_uit_keywords(texts)
    
    return signals


def is_empty_repo(signals: HeuristicSignals) -> bool:
    """Repo trống: không README, không description, không topics."""
    if not signals.has_readme and not signals.has_description and not signals.has_topics:
        return True
    return False


def is_likely_spam(signals: HeuristicSignals) -> bool:
    """Spam indicators: 0 stars, 0 forks, no README, no description, hoặc repo rác."""
    # 1. Repo trống hoàn toàn
    if signals.stars == 0 and signals.forks == 0:
        if not signals.has_readme and not signals.has_description:
            return True
            
    # 2. Kiểm tra thư mục rác (nếu signals được mở rộng sau này)
    # Hiện tại dựa vào readme_len cực thấp cũng là một dấu hiệu
    if signals.readme_len < 20 and not signals.has_description and signals.stars == 0:
        return True
        
    return False


def repo_quality_score(signals: HeuristicSignals) -> float:
    """0-1 heuristic quality score (not used in final, only for debugging)."""
    score = 0.0
    if signals.has_readme:
        score += 0.3
    if signals.has_description:
        score += 0.15
    if signals.has_topics:
        score += 0.1
    if signals.stars >= 3:
        score += 0.2
    elif signals.stars >= 1:
        score += 0.1
    if signals.forks >= 1:
        score += 0.1
    if signals.readme_len > 100:
        score += 0.1
    if signals.readme_len > 500:
        score += 0.05
    return min(score, 1.0)
