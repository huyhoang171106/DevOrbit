package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.entity.StudentNotification;

public record StudentNotificationResponse(
    Long id,
    String studentCode,
    String title,
    String body,
    String type,
    Long repoId,
    Long courseId,
    String techStackName,
    boolean isRead,
    String createdAt,
    String readAt
) {
    public static StudentNotificationResponse from(StudentNotification n) {
        return new StudentNotificationResponse(
            n.getId(),
            n.getStudentCode(),
            n.getTitle(),
            n.getBody(),
            n.getType(),
            n.getRepo() != null ? n.getRepo().getId() : null,
            n.getCourse() != null ? n.getCourse().getId() : null,
            n.getTechStackName(),
            n.isRead(),
            n.getCreatedAt() != null ? n.getCreatedAt().toString() : null,
            n.getReadAt() != null ? n.getReadAt().toString() : null
        );
    }
}
