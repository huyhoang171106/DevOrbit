package vn.edu.uit.devorbit_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.uit.devorbit_api.entity.AdminUser;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.AdminUserRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RevokedTokenStore revokedTokenStore;
    private final StudentUserRepository studentUserRepository;
    private final AdminUserRepository adminUserRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   RevokedTokenStore revokedTokenStore,
                                   StudentUserRepository studentUserRepository,
                                   AdminUserRepository adminUserRepository) {
        this.jwtService = jwtService;
        this.revokedTokenStore = revokedTokenStore;
        this.studentUserRepository = studentUserRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                String jti = jwtService.extractJti(token);
                if (revokedTokenStore.isRevoked(jti)) {
                    writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Session expired");
                    return;
                }

                String username = jwtService.extractUsername(token);
                String tokenType = jwtService.extractTokenType(token);

                if ("STUDENT".equals(tokenType)) {
                    StudentUser student = studentUserRepository.findByStudentCode(username).orElse(null);
                    if (student == null || !student.isActive()) {
                        writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Account disabled");
                        return;
                    }
                    if (jwtService.extractTokenVersion(token) != student.getTokenVersion()) {
                        writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Session no longer valid");
                        return;
                    }
                } else if ("ADMIN".equals(tokenType)) {
                    boolean active = adminUserRepository.findByUsername(username)
                            .map(AdminUser::isActive)
                            .orElse(false);
                    if (!active) {
                        writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Account disabled");
                        return;
                    }
                } else {
                    writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }

                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + tokenType));
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
