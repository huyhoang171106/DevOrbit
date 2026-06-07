package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.admin.LoginRequest;
import vn.edu.uit.devorbit_api.dto.admin.LoginResponse;
import vn.edu.uit.devorbit_api.service.AdminAuthService;

/**
 * ADMIN AUTH CONTROLLER = login endpoint for admin users.
 *
 * POST /api/admin/auth/login
 *   Body: { username, password }
 *   Returns: { token, username }
 *
 * The returned JWT token must be sent as:
 *   Authorization: Bearer <token>
 * in all subsequent /api/admin/** requests.
 *
 * Other /api/admin/** endpoints are protected by SecurityConfig.
 * Only this login endpoint is publicly accessible.
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return adminAuthService.login(request);
    }
}
