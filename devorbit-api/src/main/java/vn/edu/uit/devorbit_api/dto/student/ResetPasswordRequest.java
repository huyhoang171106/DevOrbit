package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RESET PASSWORD REQUEST = verify OTP and set a new password.
 *
 * Used by: POST /api/student/reset-password
 *
 * Flow:
 *   1. Student first calls POST /api/student/forgot-password to receive an OTP via email.
 *   2. Student submits studentCode + otpCode + newPassword to this endpoint.
 *   3. Server validates the OTP (correct code, not expired, PASSWORD_RESET purpose).
 *   4. On success, the password is updated and a JWT token is returned
 *      so the student is automatically logged in after reset.
 *
 * This is a two-step process:
 *   Step 1: forgot-password (sends OTP)
 *   Step 2: reset-password (verifies OTP + sets new password)
 *
 * Validation rules:
 *   - studentCode: must match the code used in the forgot-password step
 *   - otpCode:     exactly 6 characters, the OTP from email
 *   - newPassword: min 6 chars, max 100 chars (same rules as registration)
 *
 * @param studentCode The student's unique identifier/code.
 *                    Must match the code used in the forgot-password request.
 *                    Example: "SE123456"
 *                    @NotBlank: required, cannot be empty.
 * @param otpCode     The 6-digit OTP code received via email.
 *                    Must be a valid, non-expired OTP with PASSWORD_RESET purpose.
 *                    Examples: "123456", "000000"
 *                    @NotBlank: required, cannot be empty.
 * @param newPassword The new password to set (replaces the old password).
 *                    Will be hashed with bcrypt before storage.
 *                    Must be different from the current password (validated in service).
 *                    @NotBlank + @Size(min=6, max=100): required, 6-100 characters.
 */
public record ResetPasswordRequest(
    @NotBlank String studentCode,
    @NotBlank String otpCode,
    @NotBlank @Size(min = 6, max = 100) String newPassword
) {}
