package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record RoadmapRequest(
    Long studentId,
    @NotBlank String title,
    String description,
    String markdownContent,
    Boolean isPublic
) {}
