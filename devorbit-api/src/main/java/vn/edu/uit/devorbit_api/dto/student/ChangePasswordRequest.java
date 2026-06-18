package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CHANGE PASSWORD REQUEST = update password for an already-logged-in student.
 *
 * Used by: POST /api/student/me/password
 *
 * This is different from {@link ResetPasswordRequest}:
 *   - Change password: the student KNOWS their current password and wants to change it.
 *     Requires the old password as proof of identity. Used while logged in.
 *   - Reset password: the student FORGOT their password. Uses OTP verification instead.
 *     Does NOT require the old password. Used while logged out.
 *
 * Flow:
 *   1. Logged-in student submits currentPassword + newPassword.
 *   2. Server verifies currentPassword matches the stored bcrypt hash.
 *   3. If correct, updates to newPassword (also bcrypt-hashed).
 *   4. Returns success message "Đổi mật khẩu thành công".
 *
 * Both fields required. Password rules: 6-100 characters.
 * Error messages are in Vietnamese for the target user base.
 *
 * @param currentPassword The student's CURRENT password.
 *                        Must match the stored bcrypt hash to prove identity.
 *                        @NotBlank(message in Vietnamese): required, cannot be empty.
 * @param newPassword     The NEW password to set.
 *                        Will be bcrypt-hashed before storage.
 *                        Must be different from currentPassword (validated in service).
 *                        @NotBlank + @Size(min=6, max=100): required, 6-100 characters.
 *                        Error messages in Vietnamese for student users.
 */
public record ChangePasswordRequest(
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    String currentPassword,

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu mới phải từ 6-100 ký tự")
    String newPassword
) {}
