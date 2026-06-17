package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @Email @Size(max = 255) String email,
    @Size(max = 50) String studentCode,
    @NotBlank @Size(min = 6, max = 6) String otpCode,
    @NotBlank @Size(min = 6, max = 100) String newPassword
) {}
