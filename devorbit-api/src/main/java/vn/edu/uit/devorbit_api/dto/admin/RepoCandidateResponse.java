package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.RepoCandidate;

/**
 * Full response DTO for a GitHub repository candidate.
 *
 * <p>A "repo candidate" is a GitHub repository discovered by the scan
 * process that has not yet been approved or rejected. This record
 * carries extensive metadata about the repository (owner, name, URL,
 * description, language, topics, star/fork counts, file tree, README
 * excerpt, etc.) plus review-state information (status, assigned
 * reviewer, review note) and optional course association.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code GET /api/admin/repo-candidates} — list pending candidates
 *       (optionally filtered by reviewer).</li>
 *   <li>{@code POST /api/admin/repo-candidates/{candidateId}/approve}
 *       — approve a candidate (returns the updated candidate).</li>
 *   <li>{@code POST /api/admin/repo-candidates/{candidateId}/reject}
 *       — reject a candidate (returns the updated candidate).</li>
 *   <li>{@code POST /api/admin/github/scan} — scan GitHub for new
 *       candidates (returns the newly discovered candidates).</li>
 * </ul></p>
 *
 * <p><b>Factory method:</b> {@link #from(RepoCandidate)} converts a
 * {@link RepoCandidate} entity to this response DTO, resolving the
 * associated course data (id, code, name) if present.</p>
 *
 * @param id               Internal primary key of the candidate.
 * @param githubOwner      GitHub username/organization that owns the repo
 *                         (e.g. {@code "spring-projects"}).
 * @param githubName       Repository name (e.g. {@code "spring-boot"}).
 * @param githubUrl        Full GitHub URL (e.g. {@code "https://github.com/spring-projects/spring-boot"}).
 * @param status           Current review status as a string.
 *                         Values: {@code "PENDING"}, {@code "APPROVED"}, {@code "REJECTED"}.
 * @param description      Repository description from GitHub (may be {@code null}).
 * @param primaryLanguage  Primary programming language detected (e.g. {@code "Java"}).
 *                         May be {@code null}.
 * @param topics           Comma-separated topic tags (e.g. {@code "spring,microservices"}).
 *                         May be {@code null}.
 * @param stars            Number of GitHub stars.
 * @param forks            Number of GitHub forks.
 * @param lastPushedAt     ISO-8601 timestamp of the last push to the repository
 *                         (e.g. {@code "2025-12-01T10:30:00Z"}). May be {@code null}.
 * @param readmeExcerpt    First few lines of the repository's README (may be {@code null}).
 * @param hasReadme        Whether the repository has a README file ({@code true}/{@code false}).
 *                         May be {@code null} if not yet checked.
 * @param fileTree         A textual representation of the repository's file structure
 *                         (may be {@code null}). Useful for quickly assessing repo contents.
 * @param assignedReviewer Username of the reviewer assigned to evaluate this candidate
 *                         (may be {@code null} if unassigned).
 * @param courseId         ID of the associated course, if one has been linked
 *                         (may be {@code null}).
 * @param courseCode       University course code of the associated course
 *                         (e.g. {@code "IT007"}). May be {@code null}.
 * @param courseName       Vietnamese name of the associated course (may be {@code null}).
 * @param reviewNote       Free-text note left by the reviewer after evaluation
 *                         (may be {@code null}).
 */
public record RepoCandidateResponse(
    Long id,
    String githubOwner,
    String githubName,
    String githubUrl,
    String status,
    String description,
    String primaryLanguage,
    String topics,
    int stars,
    int forks,
    String lastPushedAt,
    String readmeExcerpt,
    Boolean hasReadme,
    String fileTree,
    String assignedReviewer,
    Long courseId,
    String courseCode,
    String courseName,
    String reviewNote,
    String approvedAt
) {
    /**
     * Converts a {@link RepoCandidate} entity into a {@code RepoCandidateResponse}.
     *
     * <p>This factory method maps every field from the JPA entity to the
     * record, including null-safe course resolution for the nested course
     * fields.</p>
     *
     * @param candidate The JPA entity to convert. Must not be {@code null}.
     * @return A new {@code RepoCandidateResponse} with all fields populated.
     */
    public static RepoCandidateResponse from(RepoCandidate candidate) {
        return new RepoCandidateResponse(
            candidate.getId(),
            candidate.getGithubOwner(),
            candidate.getGithubName(),
            candidate.getGithubUrl(),
            candidate.getStatus().name(),
            candidate.getDescription(),
            candidate.getPrimaryLanguage(),
            candidate.getTopics(),
            candidate.getStars(),
            candidate.getForks(),
            candidate.getLastPushedAt(),
            candidate.getReadmeExcerpt(),
            candidate.getHasReadme(),
            candidate.getFileTree(),
            candidate.getAssignedReviewer(),
            candidate.getCourse() != null ? candidate.getCourse().getId() : null,
            candidate.getCourse() != null ? candidate.getCourse().getMaMH() : null,
            candidate.getCourse() != null ? candidate.getCourse().getTenMH() : null,
            candidate.getReviewNote(),
            candidate.getApprovedAt() != null ? candidate.getApprovedAt().toString() : null
        );
    }
}
