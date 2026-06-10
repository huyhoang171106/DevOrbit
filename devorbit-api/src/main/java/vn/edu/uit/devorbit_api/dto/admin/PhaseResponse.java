package vn.edu.uit.devorbit_api.dto.admin;

import java.time.LocalDateTime;

public record PhaseResponse(
    Long id,
    Long roadmapId,
    String title,
    String description,
    int sortOrder,
    LocalDateTime createdAt
) {}
