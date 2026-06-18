package vn.edu.uit.devorbit_api.dto.student;

/**
 * STUDENT AUTH RESPONSE = returned after successful login, OTP verification, or password reset.
 *
 * Used by:
 *   POST /api/student/login          — returns JWT token + profile
 *   POST /api/student/verify-otp     — returns JWT token + profile
 *   POST /api/student/reset-password — returns JWT token + profile
 *
 * Contains the JWT access token plus basic student profile fields.
 * The token must be sent as "Authorization: Bearer <token>" in subsequent requests.
 * Profile fields (id, studentCode, fullName, email, avatar) allow the frontend
 * to display user info immediately without an extra GET /me call.
 *
 * @param token       JWT access token (Bearer token).
 *                    A signed JSON Web Token containing the student's identity.
 *                    Must be included in the Authorization header for authenticated requests.
 *                    Format: eyJhbGciOiJIUzI1NiIs... (base64url-encoded JWT)
 * @param id          The student's database ID (primary key, auto-generated).
 *                    Used for internal identification — not exposed to other users.
 *                    Example: 1, 42, 12345
 * @param studentCode The student's unique identifier/code.
 *                    This is the same value used for login.
 *                    Example: "SE123456"
 * @param fullName    The student's full name in Vietnamese.
 *                    Example: "Nguyen Van A"
 * @param email       The student's email address.
 *                    Example: "student@example.com"
 * @param avatar      URL to the student's avatar image.
 *                    May be null or empty if no avatar has been set.
 *                    Example: "https://supabase.storage/avatars/student_1.jpg"
 */
public record StudentAuthResponse(String token, Long id, String studentCode, String fullName, String email, String avatar) {}
