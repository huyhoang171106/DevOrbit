package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UPDATE FULL NAME REQUEST = change the student's displayed full name.
 *
 * Used by: POST /api/student/me/name
 *
 * Flow:
 *   1. Logged-in student submits a new fullName.
 *   2. Server validates the name (2-100 characters, not blank).
 *   3. Updates the student's fullName in the database.
 *   4. Returns the updated {@link StudentProfileResponse}.
 *
 * The full name is the display name shown in the UI, not the login credential.
 * Students can change their name freely (unlike studentCode which is immutable).
 * Vietnamese names with diacritics are fully supported (e.g., "Nguyễn Văn A").
 *
 * @param fullName The new full name to set.
 *                 Minimum 2 characters (e.g., "Lê A"), maximum 100 characters.
 *                 Supports Unicode, including Vietnamese diacritics.
 *                 Examples: "Nguyễn Văn A", "Trần Thị Bích Ngọc"
 *                 @NotBlank(message in Vietnamese): required, cannot be empty.
 *                 @Size(min=2, max=100): must be between 2 and 100 characters.
 *                 Error messages in Vietnamese for student users.
 */
public record UpdateFullNameRequest(
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    String fullName
) {}
