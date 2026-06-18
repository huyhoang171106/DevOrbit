package vn.edu.uit.devorbit_api.dto.admin;

import java.util.List;

/**
 * Request payload for approving a repo candidate with review metadata.
 *
 * <p>When an admin (or reviewer) approves a GitHub repository candidate,
 * they may optionally enrich it with a custom description, a list of
 * associated technology stacks, and an internal review note. These fields
 * are stored on the resulting {@code GithubRepo} entity after approval.</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/repo-candidates/{candidateId}/approve}</p>
 *
 * <p><b>Example JSON:</b>
 * <pre>{@code
 * {
 *   "description": "A comprehensive Spring Boot starter template",
 *   "techStacks": ["Java", "Spring Boot", "Maven"],
 *   "reviewNote": "Good code quality, well-documented, suitable for IT007"
 * }
 * }</pre></p>
 *
 * @param description  Optional override description for the approved repo.
 *                     If {@code null} or empty, the candidate's original
 *                     GitHub description may be used instead.
 * @param techStacks   Optional list of technology stack names to associate
 *                     with the approved repo (e.g. {@code ["Java", "Spring Boot"]}).
 *                     May be empty or {@code null}.
 * @param reviewNote   Optional internal note left by the reviewer (e.g. summary
 *                     of findings, quality assessment). May be {@code null}.
 */
public record CandidateReviewRequest(
    String description,
    List<String> techStacks,
    String reviewNote
) {}
