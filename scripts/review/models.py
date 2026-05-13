from dataclasses import dataclass, field

# ─── Domain models ──────────────────

@dataclass
class RepoCandidate:
    id: int
    githubOwner: str
    githubName: str
    githubUrl: str
    status: str
    description: str | None
    primaryLanguage: str | None
    topics: str | None
    stars: int
    forks: int
    lastPushedAt: str | None
    readmeExcerpt: str | None
    assignedReviewer: str | None
    courseId: int | None
    courseCode: str | None
    courseName: str | None
    reviewNote: str | None = None


@dataclass
class GithubOwnerProfile:
    login: str
    name: str | None
    bio: str | None
    location: str | None
    company: str | None
    blog: str | None
    email: str | None
    twitter: str | None
    public_repos: int
    followers: int
    profile_readme: str | None
    organizations: list[str] | None
    recent_repos: list[str] | None


@dataclass
class HeuristicSignals:
    """Deterministic signals extracted from repo metadata."""
    readme_len: int = 0
    has_readme: bool = False
    has_description: bool = False
    stars: int = 0
    forks: int = 0
    has_topics: bool = False
    is_uit_by_id: bool = False
    primary_language: str | None = None


@dataclass
class ReviewResult:
    candidate_id: int
    repo_name: str
    is_uit_student: bool
    is_course_relevant: bool
    has_educational_value: bool
    score: int               # 0-10 weighted
    confidence: float        # 0-1
    reasoning: str
    suggested_action: str    # APPROVE / REJECT / MANUAL
    raw_llm_response: str = ""
    latency_ms: float = 0.0
    heuristic_signals: HeuristicSignals | None = None


# ─── Audit trail ────────────────────

@dataclass
class AuditEntry:
    candidate_id: int
    repo_name: str
    prompt: str
    raw_response: str
    result: ReviewResult
    human_override: str | None = None  # APPROVE / REJECT / None
    created_at: str = ""
