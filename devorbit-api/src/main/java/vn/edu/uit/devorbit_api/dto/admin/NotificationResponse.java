package vn.edu.uit.devorbit_api.dto.admin;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * Response DTO for an admin notification.
 *
 * <p>Notifications are system-generated alerts for the admin user
 * (e.g. "New repo candidate awaiting review", "Course scan complete").
 * This DTO carries the notification's metadata: type, message text,
 * an optional target URL for deep-linking, read status, and timestamps.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/notifications}
 * — list all notifications for the admin.</p>
 *
 * <p><b>Related endpoints (via {@link vn.edu.uit.devorbit_api.controller.AdminNotificationController}):</b><ul>
 *   <li>{@code GET /api/admin/notifications/unread-count} — get unread count.</li>
 *   <li>{@code PUT /api/admin/notifications/{id}/read} — mark one as read.</li>
 *   <li>{@code PUT /api/admin/notifications/read-all} — mark all as read.</li>
 * </ul></p>
 *
 * @param id        Internal primary key of the notification.
 * @param type      Notification type/category (e.g. {@code "REVIEW"}, {@code "SCAN"},
 *                  {@code "SYSTEM"}). Helps the frontend apply different styling.
 * @param message   Human-readable notification text.
 *                  Example: {@code "3 new repo candidates found for course IT007"}.
 * @param targetUrl Optional deep-link URL the admin can navigate to when
 *                  clicking the notification (may be {@code null}).
 *                  Example: {@code "/admin/repo-candidates"}.
 * @param isRead    Whether the notification has been read ({@code true}/{@code false}).
 * @param createdAt Timestamp of when the notification was generated.
 */
@Builder
public record NotificationResponse(
    Long id,
    String type,
    String message,
    String targetUrl,
    Boolean isRead,
    LocalDateTime createdAt
) {}
