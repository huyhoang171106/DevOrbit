package vn.edu.uit.devorbit_api.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import vn.edu.uit.devorbit_api.entity.Otp;
import vn.edu.uit.devorbit_api.entity.OtpPurpose;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.event.NotificationEvent;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.OtpRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentAuthService {
    private final StudentUserRepository studentUserRepository;
    private final OtpRepository otpRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpRateLimitService otpRateLimitService;
    private final LoginRateLimitService loginRateLimitService;
    private final RevokedTokenStore revokedTokenStore;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.otp.expiration-minutes:10}")
    private int otpExpirationMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    // ───────── AUTH ─────────

    public StudentAuthResponse login(StudentLoginRequest request, HttpServletRequest httpRequest) {
        String ip = extractClientIp(httpRequest);
        loginRateLimitService.check(request.studentCode(), ip);

        StudentUser student = studentUserRepository.findByStudentCode(request.studentCode())
                .orElseThrow(() -> {
                    loginRateLimitService.recordFailure(request.studentCode(), ip);
                    return new UnauthorizedException("Tên đăng nhập hoặc mật khẩu không đúng");
                });

        if (!student.isActive()) {
            loginRateLimitService.recordFailure(request.studentCode(), ip);
            throw new UnauthorizedException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email.");
        }
        if (!passwordEncoder.matches(request.password(), student.getPasswordHash())) {
            loginRateLimitService.recordFailure(request.studentCode(), ip);
            throw new UnauthorizedException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        loginRateLimitService.onSuccess(request.studentCode(), ip);
        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    public StudentProfileResponse me(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    // ───────── REGISTER & VERIFY ─────────

    @Transactional
    public StudentProfileResponse register(StudentRegisterRequest request) {
        var existingByCode = studentUserRepository.findByStudentCode(request.studentCode());
        if (existingByCode.isPresent()) {
            if (existingByCode.get().isActive()) {
                throw new BadRequestException("Student code already exists");
            }
            otpRepository.deleteByEmailAndPurpose(existingByCode.get().getEmail(), OtpPurpose.EMAIL_VERIFICATION);
            studentUserRepository.delete(existingByCode.get());
            studentUserRepository.flush();
        }

        var existingByEmail = studentUserRepository.findByEmail(request.email());
        if (existingByEmail.isPresent()) {
            if (existingByEmail.get().isActive()) {
                throw new BadRequestException("Email already exists");
            }
            otpRepository.deleteByEmailAndPurpose(existingByEmail.get().getEmail(), OtpPurpose.EMAIL_VERIFICATION);
            studentUserRepository.delete(existingByEmail.get());
            studentUserRepository.flush();
        }

        StudentUser student = studentUserRepository.save(StudentUser.builder()
                .studentCode(request.studentCode().trim())
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .active(false)
                .emailVerified(false)
                .build());

        otpRateLimitService.check("EMAIL_VERIFICATION:" + student.getEmail());

        String otpCode = generateOtp();
        otpRepository.save(Otp.builder()
                .email(student.getEmail())
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build());
        emailService.sendOtp(student.getEmail(), otpCode, otpExpirationMinutes);

        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    @Transactional
    public StudentAuthResponse verifyOtp(OtpVerificationRequest request) {
        String email = request.email().trim().toLowerCase();
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.EMAIL_VERIFICATION)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy mã OTP xác thực email. Vui lòng đăng ký lại."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
        }
        if (!otp.getOtpCode().equals(request.otpCode().trim())) {
            throw new BadRequestException("Mã OTP không đúng.");
        }

        StudentUser student = studentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        student.setActive(true);
        student.setEmailVerified(true);
        studentUserRepository.save(student);
        otpRepository.delete(otp);

        eventPublisher.publishEvent(new NotificationEvent(
            "STUDENT_REGISTER",
            "Sinh viên mới đăng ký: " + student.getFullName() + " (" + student.getStudentCode() + ")",
            "/admin/students"
        ));

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    // ───────── RESEND OTP ─────────

    @Transactional
    public void resendOtp(String email, OtpPurpose purpose) {
        otpRateLimitService.check("RESEND_OTP:" + email.trim().toLowerCase());

        StudentUser student = studentUserRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Không thể gửi lại mã OTP. Vui lòng thử lại."));

        otpRateLimitService.check(purpose + ":" + student.getEmail());

        otpRepository.deleteByEmailAndPurpose(student.getEmail(), purpose);

        String otpCode = generateOtp();
        otpRepository.save(Otp.builder()
                .email(student.getEmail())
                .purpose(purpose)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build());

        if (purpose == OtpPurpose.PASSWORD_RESET) {
            emailService.sendPasswordResetOtp(student.getEmail(), otpCode, otpExpirationMinutes);
        } else {
            emailService.sendOtp(student.getEmail(), otpCode, otpExpirationMinutes);
        }
    }

    // ───────── FORGOT / RESET PASSWORD ─────────

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        otpRateLimitService.check("FORGOT_PASSWORD:" + request.studentCode().trim());

        var studentOpt = studentUserRepository.findByStudentCode(request.studentCode().trim());
        if (studentOpt.isPresent()) {
            StudentUser student = studentOpt.get();
            if (!student.isActive()) {
                return "";
            }
            otpRateLimitService.check("PASSWORD_RESET:" + student.getEmail());

            otpRepository.deleteByEmailAndPurpose(student.getEmail(), OtpPurpose.PASSWORD_RESET);

            String otpCode = generateOtp();
            otpRepository.save(Otp.builder()
                    .email(student.getEmail())
                    .purpose(OtpPurpose.PASSWORD_RESET)
                    .otpCode(otpCode)
                    .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                    .build());
            emailService.sendPasswordResetOtp(student.getEmail(), otpCode, otpExpirationMinutes);
            return student.getEmail();
        }
        return "";
    }

    @Transactional
    public StudentAuthResponse resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy mã OTP đặt lại mật khẩu. Vui lòng yêu cầu lại."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng yêu cầu lại.");
        }
        if (!otp.getOtpCode().equals(request.otpCode().trim())) {
            throw new BadRequestException("Mã OTP không đúng.");
        }

        StudentUser student = studentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        if (!student.isActive()) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
        }

        student.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        studentUserRepository.save(student);
        otpRepository.delete(otp);

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    // ───────── UPDATE AVATAR ─────────

    @Transactional
    public StudentProfileResponse updateAvatar(String studentCode, UpdateAvatarRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
        student.setAvatar(request.avatar());
        studentUserRepository.save(student);
        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    // ───────── UPDATE FULL NAME ─────────

    @Transactional
    public StudentProfileResponse updateFullName(String studentCode, UpdateFullNameRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
        student.setFullName(request.fullName().trim());
        studentUserRepository.save(student);
        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail(), student.getAvatar());
    }

    // ───────── CHANGE PASSWORD ─────────

    @Transactional
    public void changePassword(String studentCode, ChangePasswordRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));

        if (!passwordEncoder.matches(request.currentPassword(), student.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }

        student.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        studentUserRepository.save(student);
    }

    // ───────── LOGOUT ─────────

    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String jti = jwtService.extractJti(token);
                var expiresAt = jwtService.extractExpiration(token);
                revokedTokenStore.revoke(jti, expiresAt);
            } catch (Exception ignored) {}
        }
    }

    // ───────── UTILITY ─────────

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

    private String generateOtp() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
