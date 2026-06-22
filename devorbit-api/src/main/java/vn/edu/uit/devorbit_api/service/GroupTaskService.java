package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.constant.MemberStatus;
import vn.edu.uit.devorbit_api.dto.student.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTaskService {

    private static final Logger log = LoggerFactory.getLogger(GroupTaskService.class);
    private final GroupPlanRepository groupPlanRepository;
    private final GroupPlanMemberRepository memberRepository;
    private final GroupTaskRepository taskRepository;
    private final StudentUserRepository studentUserRepository;
    private final StudentNotificationService notificationService;

    @Transactional(readOnly = true)
    public List<GroupTaskResponse> getTasks(String studentCode, Long planId) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        validateAcceptedMember(studentCode, plan);
        return taskRepository.findByGroupPlanIdOrderByCreatedAtAsc(planId)
            .stream()
            .map(GroupTaskResponse::from)
            .toList();
    }

    @Transactional
    public GroupTaskResponse addTask(String studentCode, Long planId, AddGroupTaskRequest request) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        validateAcceptedMember(studentCode, plan);

        StudentUser creator = studentUserRepository.findByStudentCode(studentCode)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        if (request.assignedTo() != null && !request.assignedTo().isBlank()) {
            boolean isMember = memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(
                planId, request.assignedTo(), MemberStatus.ACCEPTED);
            if (!isMember && !plan.getCreatorStudentCode().equals(request.assignedTo())) {
                throw new BadRequestException("Assigned user is not a member of this group plan");
            }
        }

        LocalDate deadline = request.deadline() != null && !request.deadline().isBlank()
            ? LocalDate.parse(request.deadline()) : null;

        GroupTask task = GroupTask.builder()
            .groupPlan(plan)
            .title(request.title())
            .description(request.description())
            .assignedTo(request.assignedTo())
            .deadline(deadline)
            .createdBy(creator.getStudentCode())
            .build();

        task = taskRepository.save(task);

        notificationService.notifyGroupTaskAdded(plan, task, creator.getStudentCode());
        log.info("Task added to group plan id={}: taskId={}, title={}, by={}", planId, task.getId(), task.getTitle(), studentCode);
        return GroupTaskResponse.from(task);
    }

    @Transactional
    public GroupTaskResponse updateTask(String studentCode, Long taskId, UpdateGroupTaskRequest request) {
        GroupTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        GroupPlan plan = task.getGroupPlan();
        validateAcceptedMember(studentCode, plan);

        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.assignedTo() != null) task.setAssignedTo(request.assignedTo());
        if (request.deadline() != null && !request.deadline().isBlank()) {
            task.setDeadline(LocalDate.parse(request.deadline()));
        }
        if (request.completed() != null) task.setCompleted(request.completed());
        task.setUpdatedAt(LocalDateTime.now());

        task = taskRepository.save(task);
        return GroupTaskResponse.from(task);
    }

    @Transactional
    public void requestDeleteTask(String studentCode, Long taskId) {
        GroupTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        GroupPlan plan = task.getGroupPlan();
        validateAcceptedMember(studentCode, plan);

        if (task.isDeleteRequested()) {
            throw new BadRequestException("Delete already requested for this task");
        }

        task.setDeleteRequested(true);
        task.setDeleteRequestedBy(studentCode);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);

        notificationService.notifyGroupTaskDeleteRequest(plan, task, studentCode, plan.getCreatorStudentCode());
        log.info("Delete requested for task id={} by user={}", taskId, studentCode);
    }

    @Transactional
    public void approveDeleteTask(String studentCode, Long taskId, ApproveDeleteRequest request) {
        GroupTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        GroupPlan plan = task.getGroupPlan();

        if (!plan.getCreatorStudentCode().equals(studentCode)) {
            throw new BadRequestException("Only the plan creator can approve delete requests");
        }
        if (!task.isDeleteRequested()) {
            throw new BadRequestException("No delete request pending for this task");
        }

        String action = request.action().toUpperCase();
        if ("APPROVE".equals(action)) {
            String requester = task.getDeleteRequestedBy();
            taskRepository.delete(task);
            notificationService.notifyGroupTaskDeleteApproved(plan, task, requester, true);
            log.info("Delete approved for task id={} by creator={}", taskId, studentCode);
        } else if ("REJECT".equals(action)) {
            String requester = task.getDeleteRequestedBy();
            task.setDeleteRequested(false);
            task.setDeleteRequestedBy(null);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
            notificationService.notifyGroupTaskDeleteApproved(plan, task, requester, false);
            log.info("Delete rejected for task id={} by creator={}", taskId, studentCode);
        } else {
            throw new BadRequestException("Action must be APPROVE or REJECT");
        }
    }

    private void validateAcceptedMember(String studentCode, GroupPlan plan) {
        if (!plan.getCreatorStudentCode().equals(studentCode) &&
            !memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(plan.getId(), studentCode, MemberStatus.ACCEPTED)) {
            throw new BadRequestException("You are not a member of this group plan");
        }
    }
}
