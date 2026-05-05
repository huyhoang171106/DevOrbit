package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.RoadmapItemTargetType;
import java.time.LocalDateTime;

public record ItemResponse(
    Long id,
    Long phaseId,
    RoadmapItemTargetType targetType,
    Long targetId,
    String title,
    String note,
    int sortOrder,
    LocalDateTime createdAt
) {}
