package vn.edu.uit.devorbit_api.dto.admin;

/**
 * Login response returned after a successful admin authentication.
 *
 * <p>This single-field record contains the JWT (JSON Web Token) that the
 * admin client must include in the {@code Authorization} header for all
 * subsequent {@code /api/admin/**} requests.</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/auth/login}</p>
 *
 * <p><b>Flow:</b><ol>
 *   <li>The {@link vn.edu.uit.devorbit_api.controller.AdminAuthController}
 *       returns this DTO after successful credential validation.</li>
 *   <li>The client receives the token and stores it (typically in memory
 *       or an httpOnly cookie).</li>
 *   <li>All subsequent API calls must include:
 *       <pre>Authorization: Bearer &lt;token&gt;</pre></li>
 *   <li>The token has a configurable expiration (default: 120 minutes).
 *       After expiry, the admin must re-authenticate.</li>
 * </ol></p>
 *
 * <p><b>Example JSON response:</b>
 * <pre>{@code
 * {
 *   "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
 * }
 * }</pre></p>
 *
 * @param token Signed JWT string. Format: three Base64url-encoded segments
 *              separated by dots (header.payload.signature).
 *              Example: {@code "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."}
 */
public record LoginResponse(String token) {}
