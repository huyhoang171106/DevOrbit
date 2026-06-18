package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * OTP VERIFICATION REQUEST = verify a one-time password code sent to the student's email.
 *
 * Used by: POST /api/student/verify-otp
 *
 * Flow:
 *   1. After registration, an OTP is sent to the student's email.
 *   2. Student submits their email + the 6-digit OTP code received.
 *   3. Server validates the OTP (correct code, not expired, matches purpose).
 *   4. On success, the student account is activated and a JWT token is returned.
 *
 * The same endpoint handles resend scenarios — if OTP expires, student can request
 * a new one via POST /api/student/resend-otp and then use this endpoint again.
 *
 * Validation rules:
 *   - email:   must be a valid email format, cannot be blank
 *              This identifies which student's OTP to verify.
 *   - otpCode: exactly 6 characters, cannot be blank
 *              The one-time password sent to the email.
 *              @Size(min=6, max=6) enforces exactly 6 characters.
 *
 * @param email   The email address used during registration.
 *                Must match exactly what was registered.
 *                Example: "student@example.com"
 *                @NotBlank + @Email: required, valid email format.
 * @param otpCode The 6-digit OTP code received via email.
 *                Typically numeric but stored as String to preserve leading zeros.
 *                Examples: "123456", "000000", "482931"
 *                @NotBlank + @Size(min=6, max=6): required, exactly 6 characters.
 */
public record OtpVerificationRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 6) String otpCode
) {}
