package vn.edu.uit.devorbit_api.entity;

/**
 * Status of a repo candidate (a repo waiting for admin review).
 *
 * NEW      — Just discovered, waiting for admin review
 * APPROVED — Admin approved it; becomes a visible GithubRepo
 * REJECTED — Admin rejected it; won't be shown in the system
 */
public enum RepoCandidateStatus {
    NEW,
    APPROVED,
    REJECTED
}
