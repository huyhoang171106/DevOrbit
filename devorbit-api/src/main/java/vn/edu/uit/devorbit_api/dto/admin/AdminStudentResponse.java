package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;

/**
 * Student profile data as seen by the admin.
 *
 * <p>This DTO exposes student information for the admin management panel.
 * It includes the core identity fields plus account status flags
 * ({@code active}, {@code emailVerified}) so admins can see at a glance
 * whether a student's account is usable.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code GET /api/admin/students} — list all students (optional
 *       {@code ?search=} query param for filtering).</li>
 *   <li>{@code PUT /api/admin/students/{id}/toggle-active} — toggle a
 *       student's active status; the updated record is returned.</li>
 * </ul></p>
 *
 * <p><b>Note:</b> The response intentionally omits sensitive fields such as
 * the password hash or identity tokens.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStudentResponse {

    /** Student's internal primary key (auto-generated). */
    private Long id;

    /**
     * University-issued student code.
     * Example: {@code "21520101"}, {@code "20521456"}.
     */
    private String studentCode;

    /** Student's full display name. Example: {@code "Nguyen Van A"}. */
    private String fullName;

    /** Student's email address. Example: {@code "student@example.com"}. */
    private String email;

    /**
     * Whether the student account is currently active.
     * Inactive students cannot log in or use platform features.
     */
    private boolean active;

    /**
     * Whether the student's email address has been verified.
     * Unverified accounts may have restricted access.
     */
    private boolean emailVerified;
}
