package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotNull;
import vn.edu.uit.devorbit_api.entity.RoadmapItemTargetType;

public record ItemRequest(
    @NotNull RoadmapItemTargetType targetType,
    @NotNull Long targetId,
    String title,
    String note,
    Integer sortOrder
) {}
