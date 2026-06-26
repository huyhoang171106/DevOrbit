package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

public record StudentTechStackRequest(
    @NotBlank String name
) {}
