package vn.edu.uit.devorbit_api.dto.admin;

/**
 * LOGIN RESPONSE = JWT token returned after successful admin login.
 *
 * The client must send this token as:
 *   Authorization: Bearer <token>
 * in all subsequent /api/admin/** requests.
 *
 * The token has an expiration time (default: 120 minutes).
 */
public record LoginResponse(String token) {}
