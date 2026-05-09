package vn.edu.uit.devorbit_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("devorbit_project@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Mã xác nhận đăng ký tài khoản - DevOrbit");
        message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otpCode +
                "\n\nVui lòng không chia sẻ mã này với bất kỳ ai để bảo mật tài khoản.");

        mailSender.send(message);
    }
}