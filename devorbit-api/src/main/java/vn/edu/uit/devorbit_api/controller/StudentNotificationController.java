package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.StudentNotificationResponse;
import vn.edu.uit.devorbit_api.service.StudentNotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/notifications")
@RequiredArgsConstructor
public class StudentNotificationController {

    private final StudentNotificationService notificationService;

    @GetMapping
    public List<StudentNotificationResponse> getNotifications(@AuthenticationPrincipal String studentCode) {
        return notificationService.getNotifications(studentCode);
    }

    @GetMapping("/unread-count")
    public Map<String, Integer> getUnreadCount(@AuthenticationPrincipal String studentCode) {
        return Map.of("count", (int) notificationService.getUnreadCount(studentCode));
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@AuthenticationPrincipal String studentCode, @PathVariable Long id) {
        notificationService.markAsRead(studentCode, id);
    }

    @PutMapping("/read-all")
    public void markAllAsRead(@AuthenticationPrincipal String studentCode) {
        notificationService.markAllAsRead(studentCode);
    }
}
