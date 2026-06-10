package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * LOGIN REQUEST = credentials for admin login.
 *
 * POST /api/admin/auth/login
 * Both fields are required (@NotBlank = must not be null or empty).
 */
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
