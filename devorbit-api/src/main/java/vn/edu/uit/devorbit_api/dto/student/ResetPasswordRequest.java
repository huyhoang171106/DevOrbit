package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank String studentCode,
    @NotBlank String otpCode,
    @NotBlank @Size(min = 6, max = 100) String newPassword
) {}
