package vn.edu.uit.devorbit_api.dto.student;

/**
 * STUDENT BOOKMARK RESPONSE = a single bookmark entry returned by the API.
 *
 * Used by:
 *   GET  /api/student/bookmarks      — returns list of all bookmarks for the student
 *   POST /api/student/bookmarks      — returns the newly created bookmark
 *
 * Contains all fields of a bookmark including the auto-generated database ID
 * and the creation timestamp. The subtitle is optional and may be null.
 *
 * The id field is used for deletion (DELETE /api/student/bookmarks/{id}).
 * The createdAt field is a String (ISO 8601 format) rather than a date object
 * for easy JSON serialization and frontend display.
 *
 * @param id          The auto-generated database ID of the bookmark record.
 *                    Used to identify the bookmark for deletion.
 *                    Example: 1, 42, 12345
 * @param targetType  The type of resource that was bookmarked.
 *                    Examples: "COURSE", "REPO", "ARTICLE", "LESSON"
 * @param targetId    The database ID of the bookmarked resource within its type.
 *                    Example: 42 (for the course with ID 42)
 * @param title       The display title of the bookmark.
 *                    Example: "Nhập môn lập trình"
 * @param subtitle    An optional subtitle or short description.
 *                    May be null if not provided during creation.
 *                    Example: "SE101 - UIT"
 * @param url         The URL to access the bookmarked resource.
 *                    Example: "/courses/42"
 * @param createdAt   The timestamp when the bookmark was created.
 *                    Format: ISO 8601 string (e.g., "2025-06-18T10:30:00Z").
 *                    Useful for displaying bookmarks sorted by recency.
 */
public record StudentBookmarkResponse(
    Long id,
    String targetType,
    Long targetId,
    String title,
    String subtitle,
    String url,
    String createdAt
) {}
