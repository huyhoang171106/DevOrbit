package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

/**
 * STUDENT LOGIN REQUEST = credentials for logging into the student system.
 *
 * Used by: POST /api/student/login
 *
 * Flow:
 *   1. Student submits studentCode + password.
 *   2. Server validates credentials against the database.
 *   3. On success, returns a {@link StudentAuthResponse} with JWT token + profile info.
 *
 * All fields are required — {@code @NotBlank} ensures neither is empty or null.
 * If validation fails, the API returns a 400 Bad Request with error details.
 *
 * @param studentCode  The student's unique identifier/code (e.g., "SE123456").
 *                     This is the login username — must match exactly what was registered.
 * @param password     The student's password in plain text.
 *                     Must match the hashed password stored in the database.
 *                     Minimum 6 characters (enforced at registration).
 */
public record StudentLoginRequest(
    @NotBlank String studentCode,
    @NotBlank String password
) {}
