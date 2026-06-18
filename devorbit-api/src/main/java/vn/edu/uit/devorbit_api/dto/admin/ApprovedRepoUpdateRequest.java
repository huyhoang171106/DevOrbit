package vn.edu.uit.devorbit_api.dto.admin;

import java.util.List;

/**
 * Request payload for updating an already-approved GitHub repository.
 *
 * <p>Once a repo candidate has been approved and becomes a visible
 * {@code GithubRepo}, the admin can update its metadata through this
 * DTO. Every field is optional — only the fields that the client
 * includes in the JSON body will be applied. This allows partial
 * updates without re-sending the entire entity.</p>
 *
 * <p><b>Used by:</b> {@code PUT /api/admin/repos/{repoId}}</p>
 *
 * <p><b>Example JSON:</b>
 * <pre>{@code
 * {
 *   "displayName": "Spring Boot Best Practices",
 *   "description": "Updated description with new examples",
 *   "githubUrl": "https://github.com/new-owner/spring-boot",
 *   "primaryLanguage": "Java",
 *   "stars": 450,
 *   "techStacks": ["Java", "Spring Boot", "JUnit"],
 *   "active": true,
 *   "courseId": 2
 * }
 * }</pre></p>
 *
 * @param displayName    Optional new display name for the repository
 *                       (e.g. {@code "Spring Boot Best Practices"}).
 *                       May be {@code null} to keep the existing value.
 * @param description    Optional new description. May be {@code null}.
 * @param githubUrl      Optional new GitHub URL. May be {@code null}.
 * @param primaryLanguage Optional new primary language. May be {@code null}.
 * @param stars          Optional new star count. May be {@code null}
 *                       (uses wrapper type to distinguish 0 from absent).
 * @param techStacks     Optional new list of associated tech stack names.
 *                       May be empty or {@code null}.
 * @param active         Optional new active status. May be {@code null}
 *                       (uses wrapper type). When {@code false}, the repo
 *                       is soft-deleted (hidden from students).
 * @param courseId       Optional new course association. May be {@code null}
 *                       to keep the existing course link.
 */
public record ApprovedRepoUpdateRequest(
    String displayName,
    String description,
    String githubUrl,
    String primaryLanguage,
    Integer stars,
    List<String> techStacks,
    Boolean active,
    Long courseId
) {}
