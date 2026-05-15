package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoadmapRequest(
    @NotNull Long studentId,
    @NotBlank String title,
    String description,
    String markdownContent,
    Boolean isPublic
) {}
