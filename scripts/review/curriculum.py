import os
import re
from dataclasses import dataclass

@dataclass
class CourseInfo:
    code: str
    name: str
    name_en: str | None = None
    description: str | None = None

_curriculum_cache: dict[str, CourseInfo] = {}

def load_curriculum(sql_path: str = None) -> dict[str, CourseInfo]:
    """Parses data_updated.sql to extract course info."""
    global _curriculum_cache
    if _curriculum_cache:
        return _curriculum_cache

    if not sql_path:
        # Default path relative to this script
        base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
        sql_path = os.path.join(base_dir, "data_updated.sql")

    if not os.path.exists(sql_path):
        print(f"  ⚠️  Curriculum SQL not found at {sql_path}")
        return {}

    courses = {}
    
    try:
        with open(sql_path, "r", encoding="utf-8") as f:
            content = f.read()
            
            # Find the INSERT INTO courses block
            # We match the whole VALUES (...) part
            matches = re.findall(r"\(\s*'([^']+)'\s*,\s*'([^']+)'", content)
            for code, name in matches:
                # Basic validation: code is usually short, name is longer
                if len(code) < 10 and len(name) > 2:
                    courses[code] = CourseInfo(code=code, name=name)
    except Exception as e:
        print(f"  ⚠️  Failed to parse curriculum: {e}")

    _curriculum_cache = courses
    return courses

COMMON_ABBREVIATIONS = {
    "csdl": "IT004",
    "oop": "IT002",
    "ctdl": "IT003",
    "gt": "IT003",
    "hdh": "IT007",
    "nmcnpm": "SE104",
    "cnpm": "SE104",
    "dm": "IT012",
    "mm": "IT005",
    "web": "SE347",
    "mobile": "SE346",
    "game": "SE102",
}

def find_best_course_match(text: str, candidates: dict[str, CourseInfo] = None) -> CourseInfo | None:
    """Simple keyword matcher to find the most relevant course from text."""
    if not candidates:
        candidates = load_curriculum()
    
    text = text.lower()
    # Normalize text: remove accents for better comparison if needed, 
    # but for now just basic mapping
    
    best_match = None
    max_score = 0

    # 1. Check abbreviations first
    for abbr, code in COMMON_ABBREVIATIONS.items():
        if abbr in text.split() or abbr in text.split("-") or abbr in text.split("_"):
            if code in candidates:
                return candidates[code]

    for code, info in candidates.items():
        score = 0
        code_lower = code.lower()
        name_lower = info.name.lower()
        
        # Exact code match (high weight)
        if code_lower in text:
            score += 10
            
        # Name match (contains)
        if name_lower in text:
            score += 5
        
        # Keyword match
        keywords = name_lower.split()
        match_count = sum(1 for kw in keywords if len(kw) > 2 and kw in text)
        if match_count >= 2:
            score += 3

        if score > max_score:
            max_score = score
            best_match = info

    return best_match if max_score >= 5 else None
