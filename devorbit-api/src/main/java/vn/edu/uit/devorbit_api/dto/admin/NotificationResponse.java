package vn.edu.uit.devorbit_api.dto.admin;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
    Long id,
    String type,
    String message,
    String targetUrl,
    Boolean isRead,
    LocalDateTime createdAt
) {}
