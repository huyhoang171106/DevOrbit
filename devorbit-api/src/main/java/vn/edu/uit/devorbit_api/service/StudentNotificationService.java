package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.constant.MemberStatus;
import vn.edu.uit.devorbit_api.dto.student.StudentNotificationResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.GroupPlanMemberRepository;
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
    private final GroupPlanMemberRepository groupPlanMemberRepository;

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

    @Transactional
    public void notifyGroupPlanInvite(GroupPlan plan, String inviterCode, String inviteeCode) {
        StudentUser inviter = studentUserRepository.findByStudentCode(inviterCode).orElse(null);
        String inviterName = inviter != null ? inviter.getFullName() : inviterCode;

        StudentNotification notification = StudentNotification.builder()
            .studentCode(inviteeCode)
            .title("Lời mời tham gia kế hoạch nhóm")
            .body(inviterName + " đã mời bạn tham gia kế hoạch \"" + plan.getTitle() + "\"")
            .type("GROUP_PLAN_INVITE")
            .groupPlan(plan)
            .build();
        notificationRepository.save(notification);
        log.info("notifyGroupPlanInvite: sent to {} for plan id={}", inviteeCode, plan.getId());
    }

    @Transactional
    public void notifyGroupPlanResponse(GroupPlan plan, String responderCode, String creatorCode, MemberStatus status) {
        StudentUser responder = studentUserRepository.findByStudentCode(responderCode).orElse(null);
        String responderName = responder != null ? responder.getFullName() : responderCode;
        String action = status == MemberStatus.ACCEPTED ? "chấp nhận" : "từ chối";

        StudentNotification notification = StudentNotification.builder()
            .studentCode(creatorCode)
            .title("Phản hồi lời mời kế hoạch nhóm")
            .body(responderName + " đã " + action + " lời mời tham gia kế hoạch \"" + plan.getTitle() + "\"")
            .type("GROUP_PLAN_RESPONSE")
            .groupPlan(plan)
            .build();
        notificationRepository.save(notification);
        log.info("notifyGroupPlanResponse: sent to {} for plan id={}", creatorCode, plan.getId());
    }

    @Transactional
    public void notifyGroupTaskAdded(GroupPlan plan, GroupTask task, String creatorCode) {
        StudentUser creator = studentUserRepository.findByStudentCode(creatorCode).orElse(null);
        String creatorName = creator != null ? creator.getFullName() : creatorCode;

        List<GroupPlanMember> members = groupPlanMemberRepository.findByGroupPlanId(plan.getId());
        for (GroupPlanMember member : members) {
            if (member.getStatus() == MemberStatus.ACCEPTED && !member.getStudentCode().equals(creatorCode)) {
                StudentNotification notification = StudentNotification.builder()
                    .studentCode(member.getStudentCode())
                    .title("Nhiệm vụ mới trong kế hoạch nhóm")
                    .body(creatorName + " đã thêm nhiệm vụ \"" + task.getTitle() + "\" vào kế hoạch \"" + plan.getTitle() + "\"")
                    .type("GROUP_TASK_ADDED")
                    .groupPlan(plan)
                    .build();
                notificationRepository.save(notification);
            }
        }
        log.info("notifyGroupTaskAdded: sent to {} members for plan id={}", members.size(), plan.getId());
    }

    @Transactional
    public void notifyGroupTaskDeleteRequest(GroupPlan plan, GroupTask task, String requesterCode, String creatorCode) {
        StudentUser requester = studentUserRepository.findByStudentCode(requesterCode).orElse(null);
        String requesterName = requester != null ? requester.getFullName() : requesterCode;

        StudentNotification notification = StudentNotification.builder()
            .studentCode(creatorCode)
            .title("Yêu cầu xoá nhiệm vụ")
            .body(requesterName + " muốn xoá nhiệm vụ \"" + task.getTitle() + "\" trong kế hoạch \"" + plan.getTitle() + "\"")
            .type("GROUP_TASK_DELETE_REQUEST")
            .groupPlan(plan)
            .taskId(task.getId())
            .build();
        notificationRepository.save(notification);
        log.info("notifyGroupTaskDeleteRequest: sent to creator {} for task id={}", creatorCode, task.getId());
    }

    @Transactional
    public void notifyGroupTaskDeleteApproved(GroupPlan plan, GroupTask task, String requesterCode, boolean approved) {
        if (requesterCode == null) return;
        String action = approved ? "đã được duyệt" : "đã bị từ chối";

        StudentNotification notification = StudentNotification.builder()
            .studentCode(requesterCode)
            .title("Kết quả yêu cầu xoá nhiệm vụ")
            .body("Yêu cầu xoá nhiệm vụ \"" + task.getTitle() + "\" trong kế hoạch \"" + plan.getTitle() + "\" " + action)
            .type("GROUP_TASK_DELETE_APPROVED")
            .groupPlan(plan)
            .taskId(task.getId())
            .build();
        notificationRepository.save(notification);
        log.info("notifyGroupTaskDeleteApproved: sent to requester {} for task id={}, approved={}", requesterCode, task.getId(), approved);
    }
    @Transactional
    public void notifyGroupPlanDeleteRequest(GroupPlan plan, String requesterCode) {
        StudentUser requester = studentUserRepository.findByStudentCode(requesterCode).orElse(null);
        String requesterName = requester != null ? requester.getFullName() : requesterCode;

        StudentNotification notification = StudentNotification.builder()
            .studentCode(plan.getCreatorStudentCode())
            .title("Yêu cầu xoá kế hoạch nhóm")
            .body(requesterName + " muốn xoá kế hoạch \"" + plan.getTitle() + "\"")
            .type("GROUP_PLAN_DELETE_REQUEST")
            .groupPlan(plan)
            .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyGroupPlanDeleteApproved(GroupPlan plan, String requesterCode, boolean approved) {
        if (requesterCode == null) return;
        String action = approved ? "đã được duyệt" : "đã bị từ chối";

        StudentNotification notification = StudentNotification.builder()
            .studentCode(requesterCode)
            .title("Kết quả yêu cầu xoá kế hoạch")
            .body("Yêu cầu xoá kế hoạch \"" + plan.getTitle() + "\" " + action)
            .type("GROUP_PLAN_DELETE_APPROVED")
            .groupPlan(plan)
            .build();
        notificationRepository.save(notification);
    }

}
