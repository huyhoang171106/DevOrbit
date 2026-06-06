package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpVerificationRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 6) String otpCode
) {}
