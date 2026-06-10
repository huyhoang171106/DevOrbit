package vn.edu.uit.devorbit_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String username;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username:}") String username) {
        this.mailSender = mailSender;
        this.username = username;
    }

    public void sendOtp(String to, String otpCode, int expirationMinutes) {
        if (username == null || username.isBlank()) {
            log.info("Mail not configured — OTP {} for {} would have been sent", otpCode, to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(username);
            message.setSubject("DevOrbit — Mã xác thực email");
            message.setText("""
                Chào bạn,

                Mã xác thực email của bạn là: %s

                Mã có hiệu lực trong %d phút.

                Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.

                — DevOrbit Team
                """.formatted(otpCode, expirationMinutes));
            mailSender.send(message);
            log.info("OTP sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", to, e.getMessage());
        }
    }

    public void sendPasswordResetOtp(String to, String otpCode, int expirationMinutes) {
        if (username == null || username.isBlank()) {
            log.debug("Mail not configured — password reset OTP {} for {} would have been sent", otpCode, to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(username);
            message.setSubject("DevOrbit — Đặt lại mật khẩu");
            message.setText("""
                Chào bạn,

                Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.

                Mã xác thực của bạn là: %s

                Mã có hiệu lực trong %d phút.

                Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.

                — DevOrbit Team
                """.formatted(otpCode, expirationMinutes));
            mailSender.send(message);
            log.info("Password reset OTP sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset OTP to {}: {}", to, e.getMessage());
        }
    }
}
