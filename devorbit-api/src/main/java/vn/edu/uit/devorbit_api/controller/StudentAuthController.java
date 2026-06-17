package vn.edu.uit.devorbit_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.devorbit_api.dto.student.ChangePasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.ForgotPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.OtpVerificationRequest;
import vn.edu.uit.devorbit_api.dto.student.ResetPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.dto.student.UpdateAvatarRequest;
import vn.edu.uit.devorbit_api.dto.student.UpdateFullNameRequest;
import vn.edu.uit.devorbit_api.entity.OtpPurpose;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.service.StudentAuthService;
import vn.edu.uit.devorbit_api.service.SupabaseStorageService;

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
    private final SupabaseStorageService supabaseStorageService;

    /** Log in with student code + password. Returns JWT token. */
    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request,
                                      HttpServletRequest httpRequest) {
        return studentAuthService.login(request, httpRequest);
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
     * Resend OTP to the student's email (overwrites previous OTP for given purpose).
     * Body: { email, purpose? } — purpose defaults to EMAIL_VERIFICATION.
     */
    @PostMapping("/resend-otp")
    public Map<String, String> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        String purposeStr = body.getOrDefault("purpose", "EMAIL_VERIFICATION");
        OtpPurpose purpose;
        try {
            purpose = OtpPurpose.valueOf(purposeStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Purpose không hợp lệ: " + purposeStr);
        }
        studentAuthService.resendOtp(email, purpose);
        return Map.of("message", "OTP resent");
    }

    /**
     * Send password reset OTP to the student's email.
     * Always returns success — no information leak on whether student exists.
     */
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        studentAuthService.forgotPassword(request);
        return Map.of("message", "Nếu tài khoản tồn tại, mã OTP đã được gửi đến email của bạn");
    }

    /**
     * Verify OTP and reset password. Returns JWT token.
     */
    @PostMapping("/reset-password")
    public StudentAuthResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return studentAuthService.resetPassword(request);
    }

    /**
     * Logout: revoke the current JWT token.
     */
    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            studentAuthService.logout(authHeader.substring(7));
        }
        return Map.of("message", "Logged out");
    }

    /**
     * Get the currently logged-in student's profile.
     * Uses @AuthenticationPrincipal to extract the student code from the JWT.
     */
    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }

    @PatchMapping("/me/avatar")
    public StudentProfileResponse updateAvatar(@AuthenticationPrincipal String studentCode,
                                                @RequestBody @Valid UpdateAvatarRequest request) {
        return studentAuthService.updateAvatar(studentCode, request);
    }

    @PostMapping(value = "/me/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentProfileResponse uploadAvatar(@AuthenticationPrincipal String studentCode,
                                                @RequestParam("file") MultipartFile file) {
        Map<String, String> uploadResult = supabaseStorageService.upload(file);
        String avatarUrl = uploadResult.get("url");
        return studentAuthService.updateAvatar(studentCode, new UpdateAvatarRequest(avatarUrl));
    }

    @PatchMapping("/me/fullname")
    public StudentProfileResponse updateFullName(@AuthenticationPrincipal String studentCode,
                                                  @RequestBody @Valid UpdateFullNameRequest request) {
        return studentAuthService.updateFullName(studentCode, request);
    }

    @PatchMapping("/me/password")
    public Map<String, String> changePassword(@AuthenticationPrincipal String studentCode,
                                              @RequestBody @Valid ChangePasswordRequest request) {
        studentAuthService.changePassword(studentCode, request);
        return Map.of("message", "Đổi mật khẩu thành công");
    }
}
