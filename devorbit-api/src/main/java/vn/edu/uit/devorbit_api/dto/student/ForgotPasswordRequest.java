package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

/**
 * FORGOT PASSWORD REQUEST = trigger a password reset OTP email.
 *
 * Used by: POST /api/student/forgot-password
 *
 * Flow:
 *   1. Student submits their studentCode (login username).
 *   2. Server looks up the student by studentCode.
 *   3. If found, a 6-digit OTP is sent to the student's registered email.
 *   4. The student then uses {@link OtpVerificationRequest} + {@link ResetPasswordRequest}
 *      to verify the OTP and set a new password.
 *
 * Security note: The endpoint always returns a generic success message
 * ("Nếu tài khoản tồn tại, mã OTP đã được gửi đến email của bạn") regardless
 * of whether the student code exists. This prevents information leakage
 * (attackers cannot determine which accounts exist).
 *
 * @param studentCode The student's unique identifier/code.
 *                    The system looks up the student's email from this code
 *                    and sends the OTP to that email.
 *                    Example: "SE123456"
 *                    @NotBlank: required, cannot be empty or null.
 */
public record ForgotPasswordRequest(
    @NotBlank String studentCode
) {}
