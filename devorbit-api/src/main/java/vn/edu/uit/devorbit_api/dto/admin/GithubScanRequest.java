package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload for initiating a GitHub scan.
 *
 * <p>The GitHub scan searches for open-source repositories relevant to
 * a specific course. The {@code courseId} identifies which course the
 * scan is for, and the {@code query} is the search term sent to the
 * GitHub API (e.g. a course name, topic, or keyword combination).</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/github/scan}</p>
 *
 * <p><b>Flow:</b><ol>
 *   <li>Admin sends a scan request with a course ID and a search query.</li>
 *   <li>Server queries the GitHub API for repositories matching the query.</li>
 *   <li>Newly discovered repos are saved as {@code RepoCandidate} entries
 *       with status {@code PENDING}.</li>
 *   <li>The response contains a list of {@link RepoCandidateResponse} for
 *       the newly found candidates.</li>
 * </ol></p>
 *
 * <p><b>Example JSON:</b>
 * <pre>{@code
 * {
 *   "courseId": 1,
 *   "query": "spring boot tutorial beginner"
 * }
 * }</pre></p>
 *
 * @param courseId ID of the course to associate the scanned repos with.
 *                 Must not be null. Example: {@code 1}.
 * @param query    GitHub search query string. Must not be blank (validated
 *                 at the controller level). Examples: {@code "spring boot tutorial"},
 *                 {@code "data structures java"}.
 */
public record GithubScanRequest(@NotNull Long courseId, @NotNull String query) {}
