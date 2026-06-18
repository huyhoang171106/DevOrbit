package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.time.LocalDateTime;

/**
 * Response DTO for a course-relationship entry.
 *
 * <p>This record represents a directed relationship between two courses.
 * It includes metadata about both the source course and the target course
 * (IDs, codes, and names in both Vietnamese and English) so the admin UI
 * can display readable course names without additional lookups.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code GET /api/admin/courses/relationships}
 *       — list all course relationships.</li>
 *   <li>{@code GET /api/admin/courses/relationships/course/{courseId}}
 *       — list relationships for a specific course.</li>
 *   <li>{@code POST /api/admin/courses/relationships}
 *       — create a new relationship (returns the persisted entity).</li>
 * </ul></p>
 *
 * @param id                  Internal primary key (auto-generated).
 * @param courseId            ID of the source course.
 * @param courseCode          University course code of the source course (e.g. {@code "IT007"}).
 * @param courseName          Vietnamese name of the source course.
 * @param courseNameEn        English name of the source course (may be {@code null}).
 * @param relatedCourseId     ID of the target/related course.
 * @param relatedCourseCode   University course code of the target course (e.g. {@code "IT008"}).
 * @param relatedCourseName   Vietnamese name of the target course.
 * @param relatedCourseNameEn English name of the target course (may be {@code null}).
 * @param relationType        Type of relationship (e.g. {@code PREREQUISITE}, {@code RELATED}).
 * @param createdAt           Timestamp when the relationship was created.
 */
public record CourseRelationshipResponse(
    Long id,
    Long courseId,
    String courseCode,
    String courseName,
    String courseNameEn,
    Long relatedCourseId,
    String relatedCourseCode,
    String relatedCourseName,
    String relatedCourseNameEn,
    CourseRelationType relationType,
    LocalDateTime createdAt
) {}
