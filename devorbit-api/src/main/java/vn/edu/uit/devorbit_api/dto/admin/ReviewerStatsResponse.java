package vn.edu.uit.devorbit_api.dto.admin;

/**
 * Per-reviewer statistics for repo-candidate reviews.
 *
 * <p>This record provides a summary of how many pending (remaining)
 * and completed reviews each reviewer has. It is used by the admin
 * dashboard to monitor reviewer workload and ensure fair distribution
 * of review tasks.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/repo-candidates/stats}</p>
 *
 * <p><b>Example JSON response:</b>
 * <pre>{@code
 * [
 *   { "reviewer": "john.doe", "remaining": 3, "completed": 12 },
 *   { "reviewer": "jane.smith", "remaining": 0, "completed": 8 }
 * ]
 * }</pre></p>
 *
 * @param reviewer  Username or display name of the reviewer (e.g. {@code "john.doe"}).
 * @param remaining Number of repo candidates still awaiting this reviewer's decision.
 * @param completed Number of repo candidates this reviewer has already decided on
 *                  (approved or rejected).
 */
public record ReviewerStatsResponse(
    String reviewer,
    long remaining,
    long completed
) {}
