package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.service.StudentAuthService;
import vn.edu.uit.devorbit_api.service.EmailService;
import vn.edu.uit.devorbit_api.entity.Otp;
import vn.edu.uit.devorbit_api.repository.OtpRepository;

import java.time.LocalDateTime;
import java.security.SecureRandom;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAuthController {

    private static final SecureRandom OTP_RANDOM = new SecureRandom();

    private final StudentAuthService studentAuthService;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    @PostMapping("/send-otp")
    @Transactional
    public String sendOtp(@RequestParam String email) {
        String otp = String.format("%06d", OTP_RANDOM.nextInt(1_000_000));
        otpRepository.deleteByEmail(email);

        Otp otpEntity = Otp.builder()
                .email(email)
                .otpCode(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(1))
                .build();
        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(email, otp);
        return "Mã OTP đã được gửi và sẽ hết hạn sau 1 phút.";
    }

    @PostMapping("/verify-otp")
    @Transactional
    public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return otpRepository.findByEmail(email)
                .map(dbOtp -> {
                    if (dbOtp.getOtpCode().equals(otp) &&
                            dbOtp.getExpiryTime().isAfter(LocalDateTime.now())) {
                        otpRepository.delete(dbOtp);
                        return "Xác thực thành công!";
                    }
                    return "Mã sai hoặc đã hết hạn (quá 1 phút)!";
                })
                .orElse("Lỗi: Bạn chưa yêu cầu gửi mã!");
    }

    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request) {
        return studentAuthService.login(request);
    }

    @PostMapping("/register")
    public StudentAuthResponse register(@RequestBody @Valid StudentRegisterRequest request) {
        return studentAuthService.register(request);
    }

    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }
}
