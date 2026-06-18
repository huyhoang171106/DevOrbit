package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * STUDENT BOOKMARK REQUEST = save a new bookmark for the logged-in student.
 *
 * Used by: POST /api/student/bookmarks
 *
 * Students can bookmark various resources in the system (courses, repos, articles, etc.).
 * Each bookmark stores the resource type, ID, display title, optional subtitle, and URL.
 *
 * Flow:
 *   1. Logged-in student sends bookmark details (targetType, targetId, title, subtitle?, url).
 *   2. Server creates a new bookmark record linked to the student's account.
 *   3. Returns the created {@link StudentBookmarkResponse} with an auto-generated ID and timestamp.
 *
 * targetType identifies what kind of resource is bookmarked (e.g., "COURSE", "REPO", "ARTICLE").
 * targetId is the database ID of that resource within its type.
 * The combination of (studentId, targetType, targetId) is unique — duplicates are prevented.
 *
 * @param targetType The type of resource being bookmarked.
 *                   Examples: "COURSE", "REPO", "ARTICLE", "LESSON"
 *                   @NotBlank: required, must specify what kind of resource.
 * @param targetId   The database ID of the resource within its type.
 *                   Example: 42 (for the course with ID 42)
 *                   @NotNull: required, must point to an existing resource.
 * @param title      The display title for the bookmark.
 *                   This is typically the name/title of the resource at the time of bookmarking.
 *                   Examples: "Nhập môn lập trình", "Spring Boot Tutorial"
 *                   @NotBlank: required, each bookmark needs a visible label.
 * @param subtitle   An optional subtitle or short description for the bookmark.
 *                   Can be null if not needed.
 *                   Examples: "SE101 - UIT", "A beginner-friendly guide"
 * @param url        The URL to access the bookmarked resource.
 *                   Example: "/courses/42" or "https://example.com/tutorial"
 *                   @NotBlank: required, must provide a navigation target.
 */
public record StudentBookmarkRequest(
    @NotBlank String targetType,
    @NotNull Long targetId,
    @NotBlank String title,
    String subtitle,
    @NotBlank String url
) {}
