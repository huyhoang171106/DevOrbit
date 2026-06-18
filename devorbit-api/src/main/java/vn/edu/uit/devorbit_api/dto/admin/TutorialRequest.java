package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating or updating a course tutorial resource.
 *
 * <p>A "tutorial" is a guided learning resource (e.g. an interactive
 * tutorial, step-by-step guide, or coding playground) that is linked
 * to a specific course. The {@code title} and {@code url} are required;
 * the optional {@code type} field categorises the tutorial format
 * (e.g. {@code "video"}, {@code "interactive"}, {@code "text"}),
 * and {@code description} provides a short summary.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code POST /api/admin/courses/{courseId}/resources/tutorials}
 *       — create a new tutorial for the course.</li>
 *   <li>{@code PUT /api/admin/courses/{courseId}/resources/tutorials/{id}}
 *       — update an existing tutorial.</li>
 * </ul></p>
 *
 * <p><b>Example JSON (create):</b>
 * <pre>{@code
 * {
 *   "title": "Spring Boot Tutorial for Beginners",
 *   "url": "https://example.com/spring-boot-intro",
 *   "type": "video",
 *   "description": "Step-by-step video guide to building your first Spring Boot app"
 * }
 * }</pre></p>
 *
 * @param title       Tutorial title (required). Must not be blank.
 *                    Example: {@code "Spring Boot Tutorial for Beginners"}.
 * @param url         Full URL to the tutorial (required). Must not be blank.
 *                    Example: {@code "https://example.com/spring-boot-intro"}.
 * @param type        Tutorial format/category (optional).
 *                    Examples: {@code "video"}, {@code "interactive"}, {@code "text"}.
 *                    May be {@code null}.
 * @param description Short summary of what the tutorial covers (optional).
 *                    May be {@code null}.
 */
public record TutorialRequest(
    @NotBlank String title,
    @NotBlank String url,
    String type,
    String description
) {}
