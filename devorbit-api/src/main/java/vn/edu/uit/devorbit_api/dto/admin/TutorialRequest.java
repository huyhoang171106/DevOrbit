package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record TutorialRequest(
    @NotBlank String title,
    @NotBlank String url,
    String type,
    String description
) {}
