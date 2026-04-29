package vn.edu.uit.devorbit_api.service;

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

    public LoginResponse login(LoginRequest request) {
        AdminUser adminUser = adminUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), adminUser.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(adminUser.getUsername());
        return new LoginResponse(token);
    }
}
