package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;

/**
 * Technology stack entry used in the admin tech-stack management panel.
 *
 * <p>A "tech stack" represents a programming language, framework, or tool
 * that a repository is associated with (e.g. {@code "Java"}, {@code "Spring Boot"},
 * {@code "React"}, {@code "PostgreSQL"}). This DTO is used both for listing
 * existing stacks and for returning the created/deleted result.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code GET /api/admin/techstack} — list all available tech stacks.</li>
 *   <li>{@code POST /api/admin/techstack} — create a new tech stack
 *       (request body: {@code {"name": "..."}}).</li>
 *   <li>{@code DELETE /api/admin/techstack/{id}} — delete a tech stack
 *       (returns success/error map, not this DTO directly).</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTechStackResponse {

    /** Internal primary key of the tech stack entry. */
    private Long id;

    /**
     * Display name of the technology.
     * Examples: {@code "Java"}, {@code "Spring Boot"}, {@code "React"},
     * {@code "Docker"}, {@code "PostgreSQL"}.
     */
    private String name;
}
