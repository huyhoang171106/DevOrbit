package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.NotificationResponse;
import vn.edu.uit.devorbit_api.service.NotificationService;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for managing admin notifications.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/notifications - List all notifications</li>
 *   <li>GET /api/admin/notifications/unread-count - Get count of unread notifications</li>
 *   <li>PUT /api/admin/notifications/{id}/read - Mark one notification as read</li>
 *   <li>PUT /api/admin/notifications/read-all - Mark all notifications as read</li>
 * </ul>
 * <p>
 * Security: ADMIN role required. Delegates business logic to NotificationService.
 * Read-only by default; mark-read operations are explicitly transactional.
 */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNotificationController {

    private final NotificationService notificationService;

    /**
     * List all notifications for the admin.
     *
     * @return 200 OK with list of NotificationResponse
     * @apiNote GET /api/admin/notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> listNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * Get the count of unread notifications.
     *
     * @return 200 OK with JSON map {"count": N}
     * @apiNote GET /api/admin/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount()));
    }

    /**
     * Mark a single notification as read by its ID.
     *
     * @param id Long ID of the notification to mark as read
     * @return 200 OK if successful
     * @apiNote PUT /api/admin/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all admin notifications as read.
     *
     * @return 200 OK if successful
     * @apiNote PUT /api/admin/notifications/read-all
     */
    @PutMapping("/read-all")
    @Transactional
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }
}
