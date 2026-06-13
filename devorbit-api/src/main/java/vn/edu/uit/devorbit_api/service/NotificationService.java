package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.NotificationResponse;
import vn.edu.uit.devorbit_api.entity.Notification;
import vn.edu.uit.devorbit_api.event.NotificationEvent;
import vn.edu.uit.devorbit_api.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    @EventListener
    @Transactional
    public void handleNotification(NotificationEvent event) {
        Notification notification = Notification.builder()
            .type(event.type())
            .message(event.message())
            .targetUrl(event.targetUrl())
            .build();
        notificationRepo.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepo.findAllByOrderByCreatedAtDesc().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepo.countByIsReadFalse();
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepo.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepo.save(n);
        });
    }

    @Transactional
    public void markAllAsRead() {
        List<Notification> unread = notificationRepo.findByIsReadFalseOrderByCreatedAtDesc();
        unread.forEach(n -> n.setIsRead(true));
        notificationRepo.saveAll(unread);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
            .id(n.getId())
            .type(n.getType())
            .message(n.getMessage())
            .targetUrl(n.getTargetUrl())
            .isRead(n.getIsRead())
            .createdAt(n.getCreatedAt())
            .build();
    }
}
