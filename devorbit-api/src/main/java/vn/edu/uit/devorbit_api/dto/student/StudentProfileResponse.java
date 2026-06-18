package vn.edu.uit.devorbit_api.dto.student;

/**
 * STUDENT PROFILE RESPONSE = public profile data for a student.
 *
 * Used by:
 *   POST /api/student/register  — returned after successful registration (no token yet)
 *   GET  /api/student/me        — returned when fetching the logged-in student's profile
 *   PATCH /api/student/me/avatar — returned after avatar update
 *   POST  /api/student/me/name  — returned after full name update
 *
 * Unlike {@link StudentAuthResponse}, this DTO does NOT include a JWT token.
 * It is used for read-only profile operations and registration confirmation.
 * The frontend uses these fields to display the student's personal information.
 *
 * @param id          The student's database ID (primary key, auto-generated).
 *                    Example: 1, 42, 12345
 * @param studentCode The student's unique identifier/code (ma so sinh vien).
 *                    Immutable after registration.
 *                    Example: "SE123456"
 * @param fullName    The student's full name in Vietnamese.
 *                    Can be updated via POST /api/student/me/name.
 *                    Example: "Nguyen Van A"
 * @param email       The student's email address.
 *                    Set during registration, used for OTP verification and password recovery.
 *                    Example: "student@example.com"
 * @param avatar      URL to the student's avatar image.
 *                    May be null or empty if not set.
 *                    Updated via PATCH /api/student/me/avatar or upload.
 *                    Example: "https://supabase.storage/avatars/student_1.jpg"
 */
public record StudentProfileResponse(Long id, String studentCode, String fullName, String email, String avatar) {}
