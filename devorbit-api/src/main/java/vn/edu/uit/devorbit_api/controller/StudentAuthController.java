package vn.edu.uit.devorbit_api.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import vn.edu.uit.devorbit_api.entity.OtpPurpose;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.service.StudentAuthService;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAuthController {

    private final StudentAuthService studentAuthService;

    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request,
                                      HttpServletRequest httpRequest) {
        return studentAuthService.login(request, httpRequest);
    }

    @PostMapping("/register")
    public StudentProfileResponse register(@RequestBody @Valid StudentRegisterRequest request) {
        return studentAuthService.register(request);
    }

    @PostMapping("/verify-otp")
    public StudentAuthResponse verifyOtp(@RequestBody @Valid OtpVerificationRequest request) {
        return studentAuthService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public Map<String, String> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
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

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        String email = studentAuthService.forgotPassword(request);
        return Map.of("email", email != null ? email : "",
                      "message", "Nếu tài khoản tồn tại, mã OTP đã được gửi đến email của bạn");
    }

    @PostMapping("/reset-password")
    public StudentAuthResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return studentAuthService.resetPassword(request);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            studentAuthService.logout(authHeader.substring(7));
        }
        return Map.of("message", "Logged out");
    }

    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }
}
