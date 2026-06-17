package vn.edu.uit.devorbit_api.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.uit.devorbit_api.entity.AdminUser;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.AdminUserRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private RevokedTokenStore revokedTokenStore;
    @Mock private StudentUserRepository studentUserRepository;
    @Mock private AdminUserRepository adminUserRepository;
    @Mock private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsTokenForInactiveAdmin() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, revokedTokenStore, studentUserRepository, adminUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer admin-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid("admin-token")).thenReturn(true);
        when(jwtService.extractJti("admin-token")).thenReturn("jti");
        when(revokedTokenStore.isRevoked("jti")).thenReturn(false);
        when(jwtService.extractUsername("admin-token")).thenReturn("admin");
        when(jwtService.extractTokenType("admin-token")).thenReturn("ADMIN");
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(
                AdminUser.builder().username("admin").passwordHash("hash").active(false).build()));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void authenticatesActiveAdminToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, revokedTokenStore, studentUserRepository, adminUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer admin-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid("admin-token")).thenReturn(true);
        when(jwtService.extractJti("admin-token")).thenReturn("jti");
        when(revokedTokenStore.isRevoked("jti")).thenReturn(false);
        when(jwtService.extractUsername("admin-token")).thenReturn("admin");
        when(jwtService.extractTokenType("admin-token")).thenReturn("ADMIN");
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(
                AdminUser.builder().username("admin").passwordHash("hash").active(true).build()));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void rejectsStaleStudentTokenVersion() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, revokedTokenStore, studentUserRepository, adminUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer student-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        StudentUser student = StudentUser.builder()
                .studentCode("24520554")
                .active(true)
                .tokenVersion(2)
                .build();

        when(jwtService.isTokenValid("student-token")).thenReturn(true);
        when(jwtService.extractJti("student-token")).thenReturn("jti");
        when(revokedTokenStore.isRevoked("jti")).thenReturn(false);
        when(jwtService.extractUsername("student-token")).thenReturn("24520554");
        when(jwtService.extractTokenType("student-token")).thenReturn("STUDENT");
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(jwtService.extractTokenVersion("student-token")).thenReturn(1);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void authenticatesStudentTokenWithMatchingVersion() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, revokedTokenStore, studentUserRepository, adminUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer student-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        StudentUser student = StudentUser.builder()
                .studentCode("24520554")
                .active(true)
                .tokenVersion(2)
                .build();

        when(jwtService.isTokenValid("student-token")).thenReturn(true);
        when(jwtService.extractJti("student-token")).thenReturn("jti");
        when(revokedTokenStore.isRevoked("jti")).thenReturn(false);
        when(jwtService.extractUsername("student-token")).thenReturn("24520554");
        when(jwtService.extractTokenType("student-token")).thenReturn("STUDENT");
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(jwtService.extractTokenVersion("student-token")).thenReturn(2);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("24520554");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void rejectsUnknownTokenType() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, revokedTokenStore, studentUserRepository, adminUserRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer legacy-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isTokenValid("legacy-token")).thenReturn(true);
        when(jwtService.extractJti("legacy-token")).thenReturn("jti");
        when(revokedTokenStore.isRevoked("jti")).thenReturn(false);
        when(jwtService.extractUsername("legacy-token")).thenReturn("24520554");
        when(jwtService.extractTokenType("legacy-token")).thenReturn("USER");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, never()).doFilter(request, response);
    }
}
