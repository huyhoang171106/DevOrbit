package vn.edu.uit.devorbit_api.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.admin.LoginRequest;
import vn.edu.uit.devorbit_api.dto.admin.LoginResponse;
import vn.edu.uit.devorbit_api.entity.AdminUser;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.AdminUserRepository;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final LoginRateLimitService loginRateLimitService;
    private final RevokedTokenStore revokedTokenStore;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String ip = extractClientIp(httpRequest);
        loginRateLimitService.check(request.username(), ip);

        AdminUser adminUser = adminUserRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    loginRateLimitService.recordFailure(request.username(), ip);
                    return new UnauthorizedException("Invalid username or password");
                });

        if (!adminUser.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.password(), adminUser.getPasswordHash())) {
            loginRateLimitService.recordFailure(request.username(), ip);
            throw new UnauthorizedException("Invalid username or password");
        }

        loginRateLimitService.onSuccess(request.username(), ip);
        String token = jwtService.generateToken(adminUser.getUsername());
        return new LoginResponse(token);
    }

    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String jti = jwtService.extractJti(token);
                revokedTokenStore.revoke(jti);
            } catch (Exception ignored) {}
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty() && !"unknown".equalsIgnoreCase(xf)) {
            return xf.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isEmpty() && !"unknown".equalsIgnoreCase(xri)) {
            return xri.trim();
        }
        return request.getRemoteAddr();
    }
}
