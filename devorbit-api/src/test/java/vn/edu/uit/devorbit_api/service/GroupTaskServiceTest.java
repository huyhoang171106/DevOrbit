package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.constant.MemberStatus;
import vn.edu.uit.devorbit_api.dto.student.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GroupTaskServiceTest {

    private final GroupPlanRepository groupPlanRepository = mock(GroupPlanRepository.class);
    private final GroupPlanMemberRepository memberRepository = mock(GroupPlanMemberRepository.class);
    private final GroupTaskRepository taskRepository = mock(GroupTaskRepository.class);
    private final StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);
    private final StudentNotificationService notificationService = mock(StudentNotificationService.class);
    private final GroupTaskService service = new GroupTaskService(
        groupPlanRepository, memberRepository, taskRepository,
        studentUserRepository, notificationService);

    private GroupPlan plan;
    private GroupTask task;

    @BeforeEach
    void setUp() {
        plan = GroupPlan.builder()
            .id(10L).title("Team Project").creatorStudentCode("creator1").build();

        task = GroupTask.builder()
            .id(100L).groupPlan(plan).title("Write report")
            .assignedTo("member1").createdBy("creator1")
            .completed(false).deleteRequested(false)
            .build();
    }

    @Test
    void getTasks_success() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);
        when(taskRepository.findByGroupPlanIdOrderByCreatedAtAsc(10L)).thenReturn(List.of(task));

        List<GroupTaskResponse> tasks = service.getTasks("member1", 10L);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).title()).isEqualTo("Write report");
    }

    @Test
    void addTask_success() {
        AddGroupTaskRequest req = new AddGroupTaskRequest("Fix bug", null, "member1", "2026-07-20");
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);
        when(studentUserRepository.findByStudentCode("creator1")).thenReturn(
            Optional.of(StudentUser.builder().studentCode("creator1").build()));
        when(taskRepository.save(any())).thenAnswer(inv -> {
            GroupTask t = inv.getArgument(0);
            return GroupTask.builder().id(200L).groupPlan(plan).title(t.getTitle())
                .assignedTo(t.getAssignedTo()).createdBy(t.getCreatedBy())
                .deadline(t.getDeadline()).build();
        });

        GroupTaskResponse res = service.addTask("creator1", 10L, req);

        assertThat(res.title()).isEqualTo("Fix bug");
        assertThat(res.assignedTo()).isEqualTo("member1");
        verify(notificationService).notifyGroupTaskAdded(any(), any(), eq("creator1"));
    }

    @Test
    void addTask_nonMemberAssignee_throws() {
        AddGroupTaskRequest req = new AddGroupTaskRequest("Fix bug", null, "outsider", null);
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "outsider", MemberStatus.ACCEPTED))
            .thenReturn(false);
        when(studentUserRepository.findByStudentCode("creator1")).thenReturn(
            Optional.of(StudentUser.builder().studentCode("creator1").build()));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "outsider", MemberStatus.ACCEPTED))
            .thenReturn(false);

        assertThatThrownBy(() -> service.addTask("creator1", 10L, req))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void updateTask_toggleCompleted() {
        UpdateGroupTaskRequest req = new UpdateGroupTaskRequest(null, null, null, null, true);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GroupTaskResponse res = service.updateTask("member1", 100L, req);

        assertThat(res.completed()).isTrue();
    }

    @Test
    void requestDeleteTask_success() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);

        service.requestDeleteTask("member1", 100L);

        assertThat(task.isDeleteRequested()).isTrue();
        verify(notificationService).notifyGroupTaskDeleteRequest(plan, task, "member1", "creator1");
    }

    @Test
    void requestDeleteTask_alreadyRequested_throws() {
        task.setDeleteRequested(true);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);

        assertThatThrownBy(() -> service.requestDeleteTask("member1", 100L))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void approveDeleteTask_approve_deletesTask() {
        task.setDeleteRequested(true);
        task.setDeleteRequestedBy("member1");
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

        service.approveDeleteTask("creator1", 100L, new ApproveDeleteRequest("approve"));

        verify(taskRepository).delete(task);
        verify(notificationService).notifyGroupTaskDeleteApproved(plan, task, "member1", true);
    }

    @Test
    void approveDeleteTask_reject_clearsRequest() {
        task.setDeleteRequested(true);
        task.setDeleteRequestedBy("member1");
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.approveDeleteTask("creator1", 100L, new ApproveDeleteRequest("reject"));

        assertThat(task.isDeleteRequested()).isFalse();
        verify(notificationService).notifyGroupTaskDeleteApproved(plan, task, "member1", false);
    }

    @Test
    void approveDeleteTask_nonCreator_throws() {
        task.setDeleteRequested(true);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.approveDeleteTask("member1", 100L, new ApproveDeleteRequest("approve")))
            .isInstanceOf(BadRequestException.class);
    }
}
