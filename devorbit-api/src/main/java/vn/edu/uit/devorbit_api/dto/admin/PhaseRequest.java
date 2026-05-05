package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record PhaseRequest(
    @NotBlank String title,
    String description,
    Integer sortOrder
) {}
