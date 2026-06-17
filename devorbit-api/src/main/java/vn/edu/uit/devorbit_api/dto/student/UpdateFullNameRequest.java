package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFullNameRequest(
    @NotBlank @Size(min = 2, max = 100) String fullName
) {}
