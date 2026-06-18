package vn.edu.uit.devorbit_api.dto.admin;

/**
 * Result DTO returned after deleting a course.
 *
 * <p>When a course is deleted, the system may also deactivate its
 * associated Discord/text channel if one exists. This record signals
 * whether such a channel was deactivated as part of the deletion
 * process — this is informational for the admin so they know if
 * additional channel cleanup was performed.</p>
 *
 * <p><b>Used by:</b> {@code DELETE /api/admin/courses/{id}}</p>
 *
 * <p><b>Example JSON response:</b>
 * <pre>{@code
 * {
 *   "channelDeactivated": true
 * }
 * }</pre></p>
 *
 * @param channelDeactivated {@code true} if the course's associated
 *                           communication channel was also deactivated;
 *                           {@code false} if no channel existed or it
 *                           was already inactive.
 */
public record CourseDeleteResult(boolean channelDeactivated) {
}
