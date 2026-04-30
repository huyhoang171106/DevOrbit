package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotNull;
import vn.edu.uit.devorbit_api.entity.BookmarkTargetType;

public record BookmarkRequest(
        @NotNull BookmarkTargetType targetType,
        @NotNull Long targetId
) {}
