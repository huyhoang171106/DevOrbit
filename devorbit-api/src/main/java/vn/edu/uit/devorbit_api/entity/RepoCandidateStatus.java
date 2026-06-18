package vn.edu.uit.devorbit_api.entity;

/**
 * LIFECYCLE STATUS of a repo candidate.
 *
 * After scanning GitHub, discovered repos are saved as candidates
 * with status NEW. An admin must review each candidate:
 *
 *   NEW ──────> APPROVED  → A GithubRepo entity is created, repo becomes visible
 *     │                    → Students can see it on the course page
 *     └──────> REJECTED   → The candidate stays in DB (audit trail)
 *                          → Nothing is shown to students
 *
 * The flow: GitHub Scan → RepoCandidate(NEW) → Admin Review → APPROVED/REJECTED
 */
public enum RepoCandidateStatus {

    /** Freshly discovered, waiting for admin to review. */
    NEW,

    /** Admin approved. A GithubRepo was created from this candidate. */
    APPROVED,

    /** Admin rejected. Will never become a visible repo. */
    REJECTED
}
