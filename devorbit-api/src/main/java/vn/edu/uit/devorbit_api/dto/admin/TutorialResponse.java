package vn.edu.uit.devorbit_api.dto.admin;

import java.time.LocalDateTime;

public record TutorialResponse(
    Long id,
    Long courseId,
    String title,
    String url,
    String type,
    String description,
    LocalDateTime createdAt
) {}
