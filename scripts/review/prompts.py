"""Prompt building — sanitizes untrusted input, manages token budget."""

from review.models import RepoCandidate, GithubOwnerProfile
from review.config import README_MAX_CHARS, PROFILE_README_MAX_CHARS
from review.curriculum import load_curriculum, find_best_course_match


def _sanitize(text: str, max_chars: int) -> str:
    """Sanitize untrusted text: strip nulls, limit length, cap lines."""
    text = text.replace("\x00", "")
    if len(text) > max_chars:
        text = text[:max_chars] + "\n...(truncated)"
    # Cap each line to 500 chars to prevent injection via super-long lines
    lines = text.split("\n")
    lines = [line[:500] for line in lines]
    return "\n".join(lines)


def build_profile_block(profile: GithubOwnerProfile | None) -> str:
    if not profile:
        return "(Không fetch được profile)"

    lines = [
        f"Owner GitHub Profile ({profile.login}):",
        f"- Name: {profile.name or 'N/A'}",
        f"- Bio: {profile.bio or 'N/A'}",
        f"- Location: {profile.location or 'N/A'}",
        f"- Company: {profile.company or 'N/A'}",
        f"- Email: {profile.email or 'N/A'}",
        f"- Twitter: {profile.twitter or 'N/A'}",
        f"- Website: {profile.blog or 'N/A'}",
        f"- Public repos: {profile.public_repos} | Followers: {profile.followers}",
    ]
    if profile.organizations:
        lines.append(f"- Organizations: {', '.join(profile.organizations)}")
    if profile.profile_readme:
        truncated = _sanitize(profile.profile_readme, PROFILE_README_MAX_CHARS)
        lines.append(f"- Profile README (UNTRUSTED):\n<PROFILE_README>\n{truncated}\n</PROFILE_README>")
    if profile.recent_repos:
        r = "\n  - ".join(profile.recent_repos[:10])
        lines.append(f"- Recent public repos:\n  - {r}")
    return "\n".join(lines)


def build_user_prompt(c: RepoCandidate, profile_block: str, tree_block: str | None = None) -> str:
    desc_str = _sanitize(c.description or "N/A", 500)
    topics_str = _sanitize(c.topics or "N/A", 500)
    readme_content = c.readmeExcerpt or "N/A"
    if readme_content != "N/A":
        readme_block = f"<README>\n{_sanitize(readme_content, README_MAX_CHARS)}\n</README>"
    else:
        readme_block = "N/A"

    # --- Curriculum Matching Logic ---
    curriculum = load_curriculum()
    assigned_course = f"{c.courseCode or ''} - {c.courseName or ''}".strip(" -")
    if not assigned_course:
        assigned_course = "Chưa có"

    # Try to find a better match from metadata
    suggestion = find_best_course_match(f"{c.githubName} {c.description} {c.topics}", curriculum)
    suggestion_str = "Không tìm thấy suggestion tự động"
    if suggestion:
        suggestion_str = f"{suggestion.code} - {suggestion.name}"

    return f"""Đánh giá repo candidate sau:

THÔNG TIN OWNER:
{profile_block}

THÔNG TIN REPO:
- GitHub URL: {c.githubUrl}
- Owner: {c.githubOwner}
- Repo name: {c.githubName}
- Description: {desc_str}
- Topics: {topics_str}
- Language: {c.primaryLanguage or 'N/A'}
- Stars: {c.stars} | Forks: {c.forks}
- Last pushed: {c.lastPushedAt or 'N/A'}
- README content: {readme_block}
- File tree:
{tree_block or 'N/A'}

MÔN HỌC ĐƯỢC GÁN (TỪ HỆ THỐNG): {assigned_course}
GỢI Ý MÔN HỌC (TỪ HEURISTICS): {suggestion_str}

Nhiệm vụ của bạn là xác định repo này có thuộc về sinh viên UIT và có liên quan đến môn học nào trong chương trình đào tạo hay không.
Nếu "Môn học được gán" sai, hãy dùng "Gợi ý môn học" hoặc tự tìm môn học phù hợp dựa trên nội dung.

Trả lời JSON:"""
