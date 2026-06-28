package vn.edu.uit.devorbit_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;
import vn.edu.uit.devorbit_api.service.StudentAuthService;
import vn.edu.uit.devorbit_api.service.SupabaseStorageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentAuthController.class)
class StudentAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentAuthService studentAuthService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RevokedTokenStore revokedTokenStore;

    @MockitoBean
    private SupabaseStorageService supabaseStorageService;

    @MockitoBean
    private StudentUserRepository studentUserRepository;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        StudentAuthResponse response = new StudentAuthResponse("student-jwt-token", 1L, "24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", null, "refresh-token");
        when(studentAuthService.login(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/student/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554",
                                "password": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("student-jwt-token"))
                .andExpect(jsonPath("$.studentCode").value("24520554"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        StudentProfileResponse response = new StudentProfileResponse(1L, "24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", null);
        when(studentAuthService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554",
                                "fullName": "Nguyen Van A",
                                "email": "24520554@gm.uit.edu.vn",
                                "password": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentCode").value("24520554"))
                .andExpect(jsonPath("$.email").value("24520554@gm.uit.edu.vn"));
    }

    @Test
    void shouldVerifyOtpSuccessfully() throws Exception {
        StudentAuthResponse response = new StudentAuthResponse("student-jwt-token", 1L, "24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", null, "refresh-token");
        when(studentAuthService.verifyOtp(any())).thenReturn(response);

        mockMvc.perform(post("/api/student/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "24520554@gm.uit.edu.vn",
                                "otpCode": "123456"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("student-jwt-token"))
                .andExpect(jsonPath("$.studentCode").value("24520554"));
    }

    @Test
    void shouldForgotPasswordSuccessfully() throws Exception {
        when(studentAuthService.forgotPassword(any())).thenReturn("");

        mockMvc.perform(post("/api/student/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nếu tài khoản tồn tại, mã OTP đã được gửi đến email của bạn"));
    }

    @Test
    void forgotPasswordShouldNeverLeakEmail() throws Exception {
        // Service returns the real email (student exists)
        when(studentAuthService.forgotPassword(any())).thenReturn("24520554@gm.uit.edu.vn");

        mockMvc.perform(post("/api/student/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nếu tài khoản tồn tại, mã OTP đã được gửi đến email của bạn"))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        StudentAuthResponse response = new StudentAuthResponse("student-jwt-token", 1L, "24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", null, "refresh-token");
        when(studentAuthService.resetPassword(any())).thenReturn(response);

        mockMvc.perform(post("/api/student/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554",
                                "otpCode": "123456",
                                "newPassword": "newPassword123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("student-jwt-token"));
    }

    @Test
    void shouldResendOtpSuccessfully() throws Exception {
        mockMvc.perform(post("/api/student/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "24520554@gm.uit.edu.vn"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP resent"));
    }

    @Test
    void shouldResendOtpWithPurpose() throws Exception {
        mockMvc.perform(post("/api/student/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "studentCode": "24520554",
                                "purpose": "PASSWORD_RESET"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP resent"));
    }

    @Test
    void shouldRejectInvalidPurpose() throws Exception {
        mockMvc.perform(post("/api/student/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "24520554@gm.uit.edu.vn",
                                "purpose": "INVALID"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }
}
