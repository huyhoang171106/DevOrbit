package vn.edu.uit.devorbit_api.dto.admin;

/**
 * Login response returned after a successful admin authentication.
 *
 * <p>This record contains the JWT access token and refresh token that the
 * admin client must use for subsequent API calls.</p>
 *
 * <p><b>Used by:</b> {@code POST /api/admin/auth/login}</p>
 *
 * <p><b>Flow:</b><ol>
 *   <li>The {@link vn.edu.uit.devorbit_api.controller.AdminAuthController}
 *       returns this DTO after successful credential validation.</li>
 *   <li>The client receives the tokens and stores them securely.</li>
 *   <li>All subsequent API calls must include the access token:
 *       <pre>Authorization: Bearer &lt;accessToken&gt;</pre></li>
 *   <li>When the access token expires, use the refresh token at
 *       {@code POST /api/auth/refresh} to obtain a new pair.</li>
 * </ol></p>
 *
 * <p><b>Example JSON response:</b>
 * <pre>{@code
 * {
 *   "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
 * }
 * }</pre></p>
 *
 * @param token        Signed JWT access token. Format: three Base64url-encoded
 *                     segments separated by dots (header.payload.signature).
 * @param refreshToken Signed JWT refresh token, long-lived, for obtaining new
 *                     access tokens when the current one expires.
 */
public record LoginResponse(String token, String refreshToken) {}
