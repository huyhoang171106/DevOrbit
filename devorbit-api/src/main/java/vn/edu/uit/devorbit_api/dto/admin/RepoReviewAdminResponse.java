package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Admin-facing DTO for a repository review submitted by a student.
 *
 * <p>This response is used in the admin repo-review management panel.
 * It mirrors {@link CourseReviewAdminResponse} but is scoped to
 * GitHub repository reviews. It includes the student's display name,
 * the repository name, a numeric rating, a comment, and the creation
 * timestamp.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/reviews/repos}
 * — list all repository reviews (newest first).</p>
 *
 * <p><b>Note:</b> The corresponding delete endpoint
 * ({@code DELETE /api/admin/reviews/repos/{id}}) does not return
 * this DTO; it returns {@code 204 No Content}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoReviewAdminResponse {

    /** Internal primary key of the repository review. */
    private Long id;

    /** Display name of the student who wrote the review (e.g. "Nguyen Van A"). */
    private String studentName;

    /** Display name of the repository being reviewed (e.g. "spring-boot-api"). */
    private String repoName;

    /** Star rating given by the student (1 = worst, 5 = best). */
    private Integer rating;

    /** Free-text review comment (may be {@code null} or empty). */
    private String comment;

    /** Timestamp of when the review was submitted. */
    private LocalDateTime createdAt;
}
