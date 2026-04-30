package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.entity.BookmarkTargetType;

import java.time.Instant;

public record BookmarkResponse(
        Long id,
        BookmarkTargetType targetType,
        Long targetId,
        String title,
        String subtitle,
        String url,
        Instant createdAt
) {}
