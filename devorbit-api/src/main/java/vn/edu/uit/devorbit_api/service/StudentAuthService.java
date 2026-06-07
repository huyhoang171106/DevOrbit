package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.ForgotPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.OtpVerificationRequest;
import vn.edu.uit.devorbit_api.dto.student.ResetPasswordRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.entity.Otp;
import vn.edu.uit.devorbit_api.entity.StudentUser;
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

    @Value("${app.otp.expiration-minutes:10}")
    private int otpExpirationMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    public StudentAuthResponse login(StudentLoginRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(request.studentCode())
                .orElseThrow(() -> new UnauthorizedException("Tên đăng nhập không tồn tại"));

        if (!student.isActive()) {
            throw new UnauthorizedException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email.");
        }
        if (!passwordEncoder.matches(request.password(), student.getPasswordHash())) {
            throw new UnauthorizedException("Mật khẩu không đúng");
        }

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    @Transactional
    public StudentProfileResponse register(StudentRegisterRequest request) {
        var existingByCode = studentUserRepository.findByStudentCode(request.studentCode());
        if (existingByCode.isPresent()) {
            if (existingByCode.get().isActive()) {
                throw new BadRequestException("Student code already exists");
            }
            otpRepository.deleteByEmail(existingByCode.get().getEmail());
            studentUserRepository.delete(existingByCode.get());
            studentUserRepository.flush();
        }

        var existingByEmail = studentUserRepository.findByEmail(request.email());
        if (existingByEmail.isPresent()) {
            if (existingByEmail.get().isActive()) {
                throw new BadRequestException("Email already exists");
            }
            otpRepository.deleteByEmail(existingByEmail.get().getEmail());
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

        String otpCode = generateOtp();
        otpRepository.save(Otp.builder()
                .email(student.getEmail())
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build());
        emailService.sendOtp(student.getEmail(), otpCode, otpExpirationMinutes);

        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    @Transactional
    public StudentAuthResponse verifyOtp(OtpVerificationRequest request) {
        String email = request.email().trim().toLowerCase();
        Otp otp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("No OTP found for this email. Please register again."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP has expired. Please register again.");
        }
        if (!otp.getOtpCode().equals(request.otpCode().trim())) {
            throw new BadRequestException("Invalid OTP code.");
        }

        StudentUser student = studentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Student not found."));

        student.setActive(true);
        student.setEmailVerified(true);
        studentUserRepository.save(student);
        otpRepository.delete(otp);

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    public StudentProfileResponse me(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(request.studentCode().trim())
                .orElseThrow(() -> new BadRequestException("Tên đăng nhập không tồn tại"));

        String otpCode = generateOtp();
        otpRepository.save(Otp.builder()
                .email(student.getEmail())
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build());
        emailService.sendPasswordResetOtp(student.getEmail(), otpCode, otpExpirationMinutes);
        return student.getEmail();
    }

    @Transactional
    public StudentAuthResponse resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        Otp otp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy mã OTP. Vui lòng yêu cầu lại."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng yêu cầu lại.");
        }
        if (!otp.getOtpCode().equals(request.otpCode().trim())) {
            throw new BadRequestException("Mã OTP không đúng.");
        }

        StudentUser student = studentUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        student.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        studentUserRepository.save(student);
        otpRepository.delete(otp);

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    @Transactional
    public void resendOtp(String email) {
        StudentUser student = studentUserRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Email không tồn tại trong hệ thống."));

        otpRepository.deleteByEmail(student.getEmail());

        String otpCode = generateOtp();
        otpRepository.save(Otp.builder()
                .email(student.getEmail())
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build());
        emailService.sendOtp(student.getEmail(), otpCode, otpExpirationMinutes);
    }

    private String generateOtp() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
