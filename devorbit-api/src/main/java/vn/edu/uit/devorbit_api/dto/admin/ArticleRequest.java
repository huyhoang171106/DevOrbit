package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating or updating a course article resource.
 *
 * <p>An "article" is a curated learning resource (e.g. a blog post,
 * documentation page, or tutorial article) that is linked to a specific
 * course. The {@code title} and {@code url} are required; {@code author}
 * and {@code description} are optional metadata.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code POST /api/admin/courses/{courseId}/resources/articles}
 *       — create a new article for the course.</li>
 *   <li>{@code PUT /api/admin/courses/{courseId}/resources/articles/{id}}
 *       — update an existing article.</li>
 * </ul></p>
 *
 * <p><b>Example JSON (create):</b>
 * <pre>{@code
 * {
 *   "title": "Understanding Java Streams",
 *   "url": "https://example.com/java-streams-guide",
 *   "author": "John Doe",
 *   "description": "A comprehensive guide to Java 8+ Stream API"
 * }
 * }</pre></p>
 *
 * @param title       Article headline (required). Must not be blank.
 *                    Example: {@code "Understanding Java Streams"}.
 * @param url         Full URL to the article (required). Must not be blank.
 *                    Example: {@code "https://example.com/java-streams-guide"}.
 * @param author      Name of the article's author (optional).
 *                    Example: {@code "John Doe"}. May be {@code null}.
 * @param description Short summary or excerpt of the article (optional).
 *                    Example: {@code "A comprehensive guide to Java 8+ Stream API"}.
 *                    May be {@code null}.
 */
public record ArticleRequest(
    @NotBlank String title,
    @NotBlank String url,
    String author,
    String description
) {}
