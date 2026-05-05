package vn.edu.uit.devorbit_api.dto.admin;

import java.time.LocalDateTime;

public record ArticleResponse(
    Long id,
    Long courseId,
    String title,
    String url,
    String author,
    String description,
    LocalDateTime createdAt
) {}
