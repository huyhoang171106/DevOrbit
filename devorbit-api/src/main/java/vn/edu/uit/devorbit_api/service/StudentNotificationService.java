package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.StudentNotificationResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.StudentBookmarkRepository;
import vn.edu.uit.devorbit_api.repository.StudentNotificationRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentNotificationService {

    private static final Logger log = LoggerFactory.getLogger(StudentNotificationService.class);
    private final StudentNotificationRepository notificationRepository;
    private final StudentBookmarkRepository bookmarkRepository;
    private final StudentUserRepository studentUserRepository;

    public List<StudentNotificationResponse> getNotifications(String studentCode) {
        return notificationRepository.findByStudentCodeOrderByCreatedAtDesc(studentCode)
            .stream()
            .map(StudentNotificationResponse::from)
            .toList();
    }

    public long getUnreadCount(String studentCode) {
        return notificationRepository.countByStudentCodeAndIsReadFalse(studentCode);
    }

    @Transactional
    public void markAsRead(String studentCode, Long id) {
        int updated = notificationRepository.markAsRead(id, studentCode, LocalDateTime.now());
        if (updated == 0) {
            throw new NotFoundException("Notification not found: " + id);
        }
    }

    @Transactional
    public void markAllAsRead(String studentCode) {
        notificationRepository.markAllAsRead(studentCode, LocalDateTime.now());
    }

    @Transactional
    public void notifyCourseSubscribers(GithubRepo repo, Course course) {
        List<StudentBookmark> bookmarks = bookmarkRepository.findByTargetTypeAndTargetId("COURSE", course.getId());
        if (bookmarks.isEmpty()) return;

        String title = "Mới: " + repo.getDisplayName();
        String body = "Đã thêm repo mới vào môn " + course.getMaMH() + " - " + course.getTenMH();

        for (StudentBookmark bookmark : bookmarks) {
            StudentUser student = bookmark.getStudent();
            try {
                StudentNotification notification = StudentNotification.builder()
                    .studentCode(student.getStudentCode())
                    .title(title)
                    .body(body)
                    .type("NEW_REPO_COURSE")
                    .repo(repo)
                    .course(course)
                    .build();
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.warn("Failed to create notification for student {}: {}", student.getStudentCode(), e.getMessage());
            }
        }

        log.info("notifyCourseSubscribers: created {} notifications for course {} (repo id={})",
            bookmarks.size(), course.getId(), repo.getId());
    }
}
