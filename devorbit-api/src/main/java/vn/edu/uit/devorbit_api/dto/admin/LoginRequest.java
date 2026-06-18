package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request payload for admin authentication.
 *
 * <p>This DTO carries the credentials submitted by an admin user when
 * calling the login endpoint. Both fields are required — the server
 * enforces this via {@link NotBlank} validation (the value must not be
 * null, and must contain at least one non-whitespace character).</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/auth/login}</p>
 *
 * <p><b>Flow:</b><ol>
 *   <li>Admin submits their {@code username} and {@code password} in the
 *       request body (JSON).</li>
 *   <li>The {@link vn.edu.uit.devorbit_api.controller.AdminAuthController}
 *       validates the DTO with {@code @Valid}.</li>
 *   <li>If valid, the credentials are checked against the stored
 *       admin credentials.</li>
 *   <li>On success, a {@link LoginResponse} containing a JWT token is
 *       returned.</li>
 * </ol></p>
 *
 * <p><b>Example JSON:</b>
 * <pre>{@code
 * {
 *   "username": "admin01",
 *   "password": "mySecretPassword123"
 * }
 * }</pre></p>
 *
 * @param username Admin's login username (e.g. {@code "admin01"}, {@code "superadmin"}).
 *                 Must not be blank.
 * @param password Admin's login password (e.g. {@code "P@ssw0rd!"}). The server
 *                 stores hashed passwords only — the raw password is never persisted.
 *                 Must not be blank.
 */
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
