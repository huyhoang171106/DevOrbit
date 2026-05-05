package vn.edu.uit.devorbit_api.dto.admin;

import java.time.LocalDateTime;

public record RoadmapResponse(
    Long id,
    Long studentId,
    String studentCode,
    String studentName,
    String title,
    String description,
    String markdownContent,
    boolean isPublic,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
