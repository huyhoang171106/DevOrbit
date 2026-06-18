package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotNull;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

/**
 * Request payload for creating a relationship between two courses.
 *
 * <p>Course relationships define how two courses are connected — for example,
 * a "prerequisite" relationship (Course A must be taken before Course B),
 * or a "related" relationship (Course A and Course B cover similar topics).
 * All three fields are required ({@link NotNull}).</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/courses/relationships}</p>
 *
 * <p><b>Example JSON:</b>
 * <pre>{@code
 * {
 *   "courseId": 1,
 *   "relatedCourseId": 2,
 *   "relationType": "PREREQUISITE"
 * }
 * }</pre></p>
 *
 * <p><b>Relation types (from {@link CourseRelationType}):</b><ul>
 *   <li>{@code PREREQUISITE} — {@code courseId} must be taken before {@code relatedCourseId}.</li>
 *   <li>{@code RELATED} — the two courses are topically related (mutual).</li>
 * </ul></p>
 *
 * @param courseId        ID of the first (source) course. Must not be null.
 * @param relatedCourseId ID of the second (target) course. Must not be null.
 * @param relationType    The type of relationship (enum value). Must not be null.
 *                        See {@link CourseRelationType} for allowed values.
 */
public record CourseRelationshipRequest(
    @NotNull Long courseId,
    @NotNull Long relatedCourseId,
    @NotNull CourseRelationType relationType
) {}
