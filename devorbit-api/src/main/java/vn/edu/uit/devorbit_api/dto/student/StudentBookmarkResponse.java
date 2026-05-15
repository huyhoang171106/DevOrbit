package vn.edu.uit.devorbit_api.dto.student;

public record StudentBookmarkResponse(
    Long id,
    String targetType,
    Long targetId,
    String title,
    String subtitle,
    String url,
    String createdAt
) {}
