package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

public record ApproveDeleteRequest(
    @NotBlank String action
) {}
