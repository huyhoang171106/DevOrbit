package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Admin-facing DTO for a course review submitted by a student.
 *
 * <p>This response is used in the admin review management panel.
 * It carries the review's identifier, the student's name, the course
 * name, the numeric rating, the comment text, and the creation timestamp.
 * Unlike the student-facing review DTOs, this includes the student's
 * display name (resolved from the relationship) so the admin can see
 * who wrote what without additional queries.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/reviews/courses}
 * — list all course reviews (newest first).</p>
 *
 * <p><b>Note:</b> The corresponding delete endpoint
 * ({@code DELETE /api/admin/reviews/courses/{id}}) does not return
 * this DTO; it returns {@code 204 No Content}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewAdminResponse {

    /** Internal primary key of the course review. */
    private Long id;

    /** Display name of the student who wrote the review (e.g. "Nguyen Van A"). */
    private String studentName;

    /** Name of the course being reviewed (e.g. "Cau truc du lieu & Giai thuat"). */
    private String courseName;

    /** Star rating given by the student (1 = worst, 5 = best). */
    private Integer rating;

    /** Free-text review comment (may be {@code null} or empty if the student
     * only submitted a rating without a written comment). */
    private String comment;

    /** Timestamp of when the review was submitted. */
    private LocalDateTime createdAt;
}
