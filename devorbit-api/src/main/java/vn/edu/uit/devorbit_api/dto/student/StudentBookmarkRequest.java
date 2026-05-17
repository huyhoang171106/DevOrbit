package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentBookmarkRequest(
    @NotBlank String targetType,
    @NotNull Long targetId,
    @NotBlank String title,
    String subtitle,
    @NotBlank String url
) {}
