package vn.edu.uit.devorbit_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RevokedTokenStore revokedTokenStore;
    private final StudentUserRepository studentUserRepository;

    public JwtAuthenticationFilter(JwtService jwtService, RevokedTokenStore revokedTokenStore,
                                    StudentUserRepository studentUserRepository) {
        this.jwtService = jwtService;
        this.revokedTokenStore = revokedTokenStore;
        this.studentUserRepository = studentUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                // Reject REFRESH tokens — only ACCESS tokens are accepted for API calls
                String tokenKind = jwtService.extractTokenKind(token);
                if ("REFRESH".equals(tokenKind)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Refresh tokens are not accepted for API access\"}");
                    return;
                }

                String jti = jwtService.extractJti(token);
                if (revokedTokenStore.isRevoked(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Phiên đăng nhập đã hết hạn\"}");
                    return;
                }
                String username = jwtService.extractUsername(token);
                String tokenType = jwtService.extractTokenType(token);

                if ("STUDENT".equals(tokenType)) {
                    boolean active = studentUserRepository.findByStudentCode(username)
                            .map(StudentUser::isActive)
                            .orElse(false);
                    if (!active) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Tài khoản đã bị vô hiệu hoá\"}");
                        return;
                    }
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
}
