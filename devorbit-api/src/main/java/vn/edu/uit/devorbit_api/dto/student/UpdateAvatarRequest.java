package vn.edu.uit.devorbit_api.dto.student;

/**
 * UPDATE AVATAR REQUEST = set or change the student's profile avatar URL.
 *
 * Used by:
 *   PATCH /api/student/me/avatar          — directly set an avatar URL
 *   POST  /api/student/me/avatar/upload   — upload a file, then this DTO is created internally
 *
 * Flow (direct URL):
 *   1. Frontend sends the new avatar URL in the request body.
 *   2. Server updates the student's avatar field in the database.
 *   3. Returns the updated {@link StudentProfileResponse}.
 *
 * Flow (file upload):
 *   1. Frontend uploads a file to POST /api/student/me/avatar/upload.
 *   2. Server uploads the file to Supabase Storage.
 *   3. Server creates an UpdateAvatarRequest internally with the returned URL.
 *   4. Server updates the database and returns the updated profile.
 *
 * The avatar is optional — a student may have no avatar (null/empty).
 * There is no validation annotation because the URL can be null
 * (to clear the avatar) or a valid URL string.
 *
 * @param avatar URL to the student's avatar image.
 *               If null or empty, the avatar is cleared.
 *               Example: "https://supabase.storage/avatars/student_1.jpg"
 *               Can be any valid URL pointing to an image resource.
 */
public record UpdateAvatarRequest(String avatar) {}
