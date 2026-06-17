package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.uit.devorbit_api.dto.student.*;
import vn.edu.uit.devorbit_api.entity.Otp;
import vn.edu.uit.devorbit_api.entity.OtpPurpose;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.OtpRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentAuthServiceTest {

    @Mock private StudentUserRepository studentUserRepository;
    @Mock private OtpRepository otpRepository;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;
    @Mock private OtpRateLimitService otpRateLimitService;
    @Mock(lenient = true) private LoginRateLimitService loginRateLimitService;
    @Mock(lenient = true) private RevokedTokenStore revokedTokenStore;
    @Mock(lenient = true) private HttpServletRequest httpRequest;
    @Mock(lenient = true) private ApplicationEventPublisher eventPublisher;

    private PasswordEncoder passwordEncoder;
    private StudentAuthService service;

    @Captor private ArgumentCaptor<Otp> otpCaptor;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        service = new StudentAuthService(
                studentUserRepository, otpRepository, jwtService,
                passwordEncoder, emailService, otpRateLimitService,
                loginRateLimitService, revokedTokenStore, eventPublisher);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    // ─── REGISTER ─────────────────────────────────────

    @Test
    void register_createsEmailVerificationOtp() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.empty());
        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.empty());
        when(studentUserRepository.save(any())).thenAnswer(invocation -> {
            StudentUser s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(otpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        StudentRegisterRequest req = new StudentRegisterRequest("24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", "password123");
        service.register(req);

        verify(otpRepository).save(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getPurpose()).isEqualTo(OtpPurpose.EMAIL_VERIFICATION);
        verify(emailService).sendOtp(eq("24520554@gm.uit.edu.vn"), any(), anyInt());
    }

    // ─── VERIFY OTP ────────────────────────────────────

    @Test
    void register_checksDuplicatesWithNormalizedStudentCodeAndEmail() {
        StudentUser existing = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(existing));

        StudentRegisterRequest req = new StudentRegisterRequest(
                " 24520554 ", "Nguyen Van A", " 24520554@GM.UIT.EDU.VN ", "password123");

        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Student code already exists");
        verify(studentUserRepository).findByStudentCode("24520554");
        verify(studentUserRepository, never()).save(any());
    }

    @Test
    void register_translatesConcurrentDuplicateBeforeOtpEmail() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.empty());
        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.empty());
        when(studentUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new DataIntegrityViolationException("unique student"))
                .when(studentUserRepository).flush();

        StudentRegisterRequest req = new StudentRegisterRequest(
                "24520554", "Nguyen Van A", "24520554@gm.uit.edu.vn", "password123");

        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
        verifyNoInteractions(otpRepository);
        verifyNoInteractions(emailService);
    }

    @Test
    void verifyOtp_rejectsWrongPurpose() {
        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.empty());

        OtpVerificationRequest req = new OtpVerificationRequest("24520554@gm.uit.edu.vn", "123456");
        assertThatThrownBy(() -> service.verifyOtp(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("OTP");
    }

    @Test
    void verifyOtp_onlyAcceptsEmailVerification() {
        // Simulate PASSWORD_RESET OTP existing but EMAIL_VERIFICATION not found
        Otp passwordResetOtp = Otp.builder()
                .email("24520554@gm.uit.edu.vn")
                .purpose(OtpPurpose.PASSWORD_RESET)
                .otpCode("654321")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.empty());

        OtpVerificationRequest req = new OtpVerificationRequest("24520554@gm.uit.edu.vn", "654321");
        assertThatThrownBy(() -> service.verifyOtp(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("OTP");
    }

    // ─── FORGOT PASSWORD ────────────────────────────────

    @Test
    void forgotPassword_deletesOldPasswordResetOtpBeforeCreatingNew() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(otpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.forgotPassword(new ForgotPasswordRequest("24520554"));

        verify(otpRateLimitService).check("FORGOT_PASSWORD:24520554");
        verify(otpRateLimitService).check("PASSWORD_RESET:24520554@gm.uit.edu.vn");
        verify(otpRepository).deleteByEmailAndPurpose("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET);
        verify(otpRepository).save(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getPurpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
        verify(emailService).sendPasswordResetOtp(eq("24520554@gm.uit.edu.vn"), any(), anyInt());
    }

    @Test
    void forgotPassword_returnsEmptyForInactiveAccount() {
        StudentUser inactive = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(false).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(inactive));

        String result = service.forgotPassword(new ForgotPasswordRequest("24520554"));

        assertThat(result).isEmpty();
        verify(otpRateLimitService).check("FORGOT_PASSWORD:24520554");
        verifyNoInteractions(otpRepository);
        verifyNoInteractions(emailService);
    }

    // ─── RESET PASSWORD ────────────────────────────────

    @Test
    void resetPassword_rejectsWrongPurpose() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();
        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.of(student));
        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.empty());

        ResetPasswordRequest req = new ResetPasswordRequest("24520554@gm.uit.edu.vn", null, "123456", "newPassword123");
        assertThatThrownBy(() -> service.resetPassword(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("OTP");
    }

    @Test
    void resetPassword_ignoresEmailVerificationOtp() {
        // Only EMAIL_VERIFICATION OTP exists
        Otp emailOtp = Otp.builder()
                .email("24520554@gm.uit.edu.vn")
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email(emailOtp.getEmail()).passwordHash("hash").active(true).build();
        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.of(student));

        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.empty());

        ResetPasswordRequest req = new ResetPasswordRequest("24520554@gm.uit.edu.vn", null, "123456", "newPassword123");
        assertThatThrownBy(() -> service.resetPassword(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("OTP");
    }

    @Test
    void resetPassword_rejectsInactiveAccount() {
        Otp validOtp = Otp.builder()
                .email("24520554@gm.uit.edu.vn")
                .purpose(OtpPurpose.PASSWORD_RESET)
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        StudentUser inactive = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(false).build();

        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.of(validOtp));
        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.of(inactive));

        ResetPasswordRequest req = new ResetPasswordRequest("24520554@gm.uit.edu.vn", null, "123456", "newPassword123");
        assertThatThrownBy(() -> service.resetPassword(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("vô hiệu hóa");
    }

    // ─── RESEND OTP ────────────────────────────────────

    @Test
    void resetPassword_acceptsStudentCodeWithoutLeakingEmailToClient() {
        Otp validOtp = Otp.builder()
                .email("24520554@gm.uit.edu.vn")
                .purpose(OtpPurpose.PASSWORD_RESET)
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.of(validOtp));
        when(jwtService.generateToken("24520554", "STUDENT", 1)).thenReturn("token");

        StudentAuthResponse response = service.resetPassword(
                new ResetPasswordRequest(null, "24520554", "123456", "newPassword123"));

        assertThat(response.token()).isEqualTo("token");
        assertThat(student.getTokenVersion()).isEqualTo(1);
        verify(studentUserRepository).save(student);
        verify(otpRepository).delete(validOtp);
    }

    @Test
    void resetPassword_rejectsMissingEmailAndStudentCode() {
        ResetPasswordRequest req = new ResetPasswordRequest(null, null, "123456", "newPassword123");

        assertThatThrownBy(() -> service.resetPassword(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("bắt buộc");
        verifyNoInteractions(otpRepository);
    }

    @Test
    void resendOtp_sendsCorrectEmailBasedOnPurpose() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();

        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.of(student));
        when(otpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.resendOtp("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET);

        verify(otpRepository).deleteByEmailAndPurpose("24520554@gm.uit.edu.vn", OtpPurpose.PASSWORD_RESET);
        verify(otpRepository).save(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getPurpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
        verify(emailService).sendPasswordResetOtp(eq("24520554@gm.uit.edu.vn"), any(), anyInt());
    }

    @Test
    void resendOtp_sendsEmailVerificationByDefault() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash("hash").active(true).build();

        when(studentUserRepository.findByEmail("24520554@gm.uit.edu.vn")).thenReturn(Optional.of(student));
        when(otpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.resendOtp("24520554@gm.uit.edu.vn", OtpPurpose.EMAIL_VERIFICATION);

        verify(otpRepository).deleteByEmailAndPurpose("24520554@gm.uit.edu.vn", OtpPurpose.EMAIL_VERIFICATION);
        verify(otpRepository).save(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getPurpose()).isEqualTo(OtpPurpose.EMAIL_VERIFICATION);
        verify(emailService).sendOtp(eq("24520554@gm.uit.edu.vn"), any(), anyInt());
    }

    // ─── LOGIN ───────────────────────────────────────────

    @Test
    void login_rejectsInactiveAccount() {
        StudentUser inactive = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash(passwordEncoder.encode("password123"))
                .active(false).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(inactive));

        StudentLoginRequest req = new StudentLoginRequest("24520554", "password123");
        assertThatThrownBy(() -> service.login(req, httpRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("chưa được kích hoạt");
        verify(loginRateLimitService).check("24520554", "127.0.0.1");
    }

    @Test
    void login_recordsFailureForInactiveAccount() {
        StudentUser inactive = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash(passwordEncoder.encode("password123"))
                .active(false).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(inactive));

        StudentLoginRequest req = new StudentLoginRequest("24520554", "password123");
        assertThatThrownBy(() -> service.login(req, httpRequest))
                .isInstanceOf(UnauthorizedException.class);
        verify(loginRateLimitService).recordFailure("24520554", "127.0.0.1");
    }

    @Test
    void login_recordsFailureForWrongPassword() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash(passwordEncoder.encode("correct"))
                .active(true).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));

        StudentLoginRequest req = new StudentLoginRequest("24520554", "wrong");
        assertThatThrownBy(() -> service.login(req, httpRequest))
                .isInstanceOf(UnauthorizedException.class);
        verify(loginRateLimitService).recordFailure("24520554", "127.0.0.1");
    }

    @Test
    void login_successClearsRateLimit() {
        StudentUser student = StudentUser.builder()
                .id(1L).studentCode("24520554").fullName("Nguyen Van A")
                .email("24520554@gm.uit.edu.vn").passwordHash(passwordEncoder.encode("password123"))
                .active(true).build();

        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(jwtService.generateToken("24520554", "STUDENT", 0)).thenReturn("token");

        StudentLoginRequest req = new StudentLoginRequest("24520554", "password123");
        service.login(req, httpRequest);
        verify(loginRateLimitService).onSuccess("24520554", "127.0.0.1");
    }

    // ─── FORGOT PASSWORD RATE LIMITING ─────────────────

    @Test
    void forgotPassword_rateLimitsNonExistentStudent() {
        when(studentUserRepository.findByStudentCode("nonexistent")).thenReturn(Optional.empty());

        service.forgotPassword(new ForgotPasswordRequest("nonexistent"));

        verify(otpRateLimitService).check("FORGOT_PASSWORD:nonexistent");
        verifyNoInteractions(otpRepository);
        verifyNoInteractions(emailService);
    }

    // ─── RESEND OTP RATE LIMITING ─────────────────────

    @Test
    void resendOtp_rateLimitsNonExistentEmail() {
        when(studentUserRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resendOtp("nobody@example.com", OtpPurpose.EMAIL_VERIFICATION))
                .isInstanceOf(BadRequestException.class);
        verify(otpRateLimitService).check("RESEND_OTP:nobody@example.com");
    }

    @Test
    void resendOtp_rejectsBlankEmailAsBadRequest() {
        assertThatThrownBy(() -> service.resendOtp(" ", OtpPurpose.EMAIL_VERIFICATION))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email");
        verifyNoInteractions(studentUserRepository);
        verifyNoInteractions(otpRepository);
    }
}
