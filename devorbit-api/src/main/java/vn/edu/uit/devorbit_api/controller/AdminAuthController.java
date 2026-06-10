package vn.edu.uit.devorbit_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
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
    public LoginResponse login(@RequestBody @Valid LoginRequest request,
                                HttpServletRequest httpRequest) {
        return adminAuthService.login(request, httpRequest);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            adminAuthService.logout(authHeader.substring(7));
        }
        return Map.of("message", "Logged out");
    }
}
