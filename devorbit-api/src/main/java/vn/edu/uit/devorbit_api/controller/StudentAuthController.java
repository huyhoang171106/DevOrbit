package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.ForgotPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.OtpVerificationRequest;
import vn.edu.uit.devorbit_api.dto.student.ResetPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.service.StudentAuthService;

/**
 * STUDENT AUTH CONTROLLER = register, login, and manage student accounts.
 *
 * Endpoints:
 *   POST /api/student/register        — creates a new student account (sends OTP)
 *   POST /api/student/verify-otp      — verifies OTP code and activates account
 *   POST /api/student/login           — logs in with student code + password
 *   POST /api/student/forgot-password — sends password reset OTP to email
 *   POST /api/student/reset-password  — verifies OTP and resets password
 *   GET  /api/student/me              — returns the logged-in student's profile
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAuthController {

    private final StudentAuthService studentAuthService;

    /** Log in with student code + password. Returns JWT token. */
    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request) {
        return studentAuthService.login(request);
    }

    /**
     * Register a new student account. Sends OTP to email.
     * Account is inactive until OTP is verified.
     */
    @PostMapping("/register")
    public StudentProfileResponse register(@RequestBody @Valid StudentRegisterRequest request) {
        return studentAuthService.register(request);
    }

    /**
     * Verify OTP code and activate the account. Returns JWT token.
     */
    @PostMapping("/verify-otp")
    public StudentAuthResponse verifyOtp(@RequestBody @Valid OtpVerificationRequest request) {
        return studentAuthService.verifyOtp(request);
    }

    /**
     * Resend OTP to the student's email (overwrites previous OTP).
     */
    @PostMapping("/resend-otp")
    public Map<String, String> resendOtp(@RequestBody Map<String, String> body) {
        studentAuthService.resendOtp(body.get("email"));
        return Map.of("message", "OTP resent");
    }

    /**
     * Send password reset OTP to the student's email.
     */
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        String email = studentAuthService.forgotPassword(request);
        return Map.of("email", email);
    }

    /**
     * Verify OTP and reset password. Returns JWT token.
     */
    @PostMapping("/reset-password")
    public StudentAuthResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return studentAuthService.resetPassword(request);
    }

    /**
     * Get the currently logged-in student's profile.
     * Uses @AuthenticationPrincipal to extract the student code from the JWT.
     */
    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }
}
