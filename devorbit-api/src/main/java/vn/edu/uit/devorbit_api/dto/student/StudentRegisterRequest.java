package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * STUDENT REGISTER REQUEST = create a new student account.
 *
 * Used by: POST /api/student/register
 *
 * Flow:
 *   1. Student fills in studentCode, fullName, email, and password.
 *   2. Server validates all fields (no duplicates, password strength).
 *   3. An OTP verification email is sent to the provided email address.
 *   4. Account is created in INACTIVE state until OTP is verified.
 *   5. Returns a {@link StudentProfileResponse} with basic profile info (no token yet).
 *
 * Validation rules:
 *   - studentCode: must be unique, max 50 chars (e.g., "SE123456")
 *   - fullName:    max 255 chars (supports Vietnamese names with diacritics)
 *   - email:       must be a valid email format, max 255 chars
 *   - password:    min 6 chars, max 100 chars — stored as bcrypt hash
 *
 * @param studentCode  The student's unique identifier (ma so sinh vien).
 *                     Examples: "SE123456", "IT98765".
 *                     This becomes the login username — cannot be changed later.
 *                     @NotBlank + @Size(max=50): required, max 50 characters.
 * @param fullName     The student's full name in Vietnamese.
 *                     Examples: "Nguyen Van A", "Trần Thị B".
 *                     Supports Unicode including Vietnamese diacritics.
 *                     @NotBlank + @Size(max=255): required, max 255 characters.
 * @param email        The student's email address.
 *                     Used for account verification (OTP) and password recovery.
 *                     Must be unique in the system — no duplicate emails allowed.
 *                     Examples: "student@example.com", "nguyen.van.a@uit.edu.vn".
 *                     @NotBlank + @Email + @Size(max=255): required, valid email format, max 255 chars.
 * @param password     The account password in plain text.
 *                     Will be hashed using bcrypt before storage (never stored in plain text).
 *                     Must be at least 6 characters for security.
 *                     @NotBlank + @Size(min=6, max=100): required, 6-100 characters.
 */
public record StudentRegisterRequest(
        @NotBlank @Size(max = 50) String studentCode,
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 6, max = 100) String password
) {}
