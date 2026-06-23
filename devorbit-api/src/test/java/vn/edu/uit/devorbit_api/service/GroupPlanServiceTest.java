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

class GroupPlanServiceTest {

    private final GroupPlanRepository groupPlanRepository = mock(GroupPlanRepository.class);
    private final GroupPlanMemberRepository memberRepository = mock(GroupPlanMemberRepository.class);
    private final GroupTaskRepository taskRepository = mock(GroupTaskRepository.class);
    private final StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);
    private final StudentNotificationService notificationService = mock(StudentNotificationService.class);
    private final GroupPlanService service = new GroupPlanService(
        groupPlanRepository, memberRepository, taskRepository,
        studentUserRepository, notificationService);

    private GroupPlan plan;
    private StudentUser creator;

    @BeforeEach
    void setUp() {
        creator = StudentUser.builder()
            .id(1L).studentCode("creator1").fullName("Creator").build();

        plan = GroupPlan.builder()
            .id(10L)
            .title("Team Project")
            .description("Group work")
            .creatorStudentCode("creator1")
            .deadline(LocalDate.of(2026, 7, 15))
            .build();
    }

    @Test
    void createPlan_success() {
        CreateGroupPlanRequest req = new CreateGroupPlanRequest("Team Project", "Group work", "2026-07-15");
        when(studentUserRepository.findByStudentCode("creator1")).thenReturn(Optional.of(creator));
        when(groupPlanRepository.save(any())).thenAnswer(inv -> {
            GroupPlan saved = inv.getArgument(0);
            return GroupPlan.builder()
                .id(10L).title(saved.getTitle()).description(saved.getDescription())
                .creatorStudentCode(saved.getCreatorStudentCode()).deadline(saved.getDeadline())
                .build();
        });

        GroupPlanResponse res = service.createPlan("creator1", req);

        assertThat(res.title()).isEqualTo("Team Project");
        assertThat(res.deadline()).isEqualTo(LocalDate.of(2026, 7, 15));
        verify(groupPlanRepository).save(any());
    }

    @Test
    void createPlan_studentNotFound_throws() {
        when(studentUserRepository.findByStudentCode("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPlan("unknown", new CreateGroupPlanRequest("T", null, null)))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getMyPlans_returnsPlans() {
        when(groupPlanRepository.findActiveByStudentCode("creator1")).thenReturn(List.of(plan));

        List<GroupPlanResponse> plans = service.getMyPlans("creator1");

        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).title()).isEqualTo("Team Project");
    }

    @Test
    void getPlanDetail_success() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "creator1", MemberStatus.ACCEPTED))
            .thenReturn(true);

        GroupPlanResponse res = service.getPlanDetail("creator1", 10L);

        assertThat(res.title()).isEqualTo("Team Project");
    }

    @Test
    void getPlanDetail_notFound_throws() {
        when(groupPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPlanDetail("creator1", 99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deletePlan_creator_success() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        service.deletePlan("creator1", 10L);

        verify(groupPlanRepository).save(plan);
        assertThat(plan.isActive()).isFalse();
    }

    @Test
    void deletePlan_nonCreator_throws() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.deletePlan("other", 10L))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void inviteMember_success() {
        InviteMemberRequest req = new InviteMemberRequest("member1");
        StudentUser invited = StudentUser.builder().id(2L).studentCode("member1").build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(studentUserRepository.findByStudentCode("member1")).thenReturn(Optional.of(invited));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(false);
        when(memberRepository.findByGroupPlanIdAndStudentCode(10L, "member1")).thenReturn(Optional.empty());
        when(memberRepository.save(any())).thenAnswer(inv -> {
            GroupPlanMember m = inv.getArgument(0);
            return GroupPlanMember.builder().id(1L).groupPlan(plan)
                .studentCode(m.getStudentCode()).status(MemberStatus.PENDING).build();
        });

        GroupPlanMemberResponse res = service.inviteMember("creator1", 10L, req);

        assertThat(res.studentCode()).isEqualTo("member1");
        assertThat(res.status()).isEqualTo("PENDING");
        verify(notificationService).notifyGroupPlanInvite(plan, "creator1", "member1");
    }

    @Test
    void inviteMember_selfInvite_throws() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(studentUserRepository.findByStudentCode("creator1")).thenReturn(Optional.of(creator));

        assertThatThrownBy(() -> service.inviteMember("creator1", 10L, new InviteMemberRequest("creator1")))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void inviteMember_alreadyMember_throws() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(studentUserRepository.findByStudentCode("member1")).thenReturn(
            Optional.of(StudentUser.builder().id(2L).studentCode("member1").build()));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);

        assertThatThrownBy(() -> service.inviteMember("creator1", 10L, new InviteMemberRequest("member1")))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getMembers_success() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.ACCEPTED).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "creator1", MemberStatus.ACCEPTED))
            .thenReturn(true);
        when(memberRepository.findByGroupPlanId(10L)).thenReturn(List.of(member));

        List<GroupPlanMemberResponse> members = service.getMembers("creator1", 10L);

        assertThat(members).hasSize(1);
        assertThat(members.get(0).studentCode()).isEqualTo("member1");
    }

    @Test
    void respondToInvite_accept() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.PENDING).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.findByGroupPlanIdAndStudentCode(10L, "member1")).thenReturn(Optional.of(member));

        service.respondToInvite("member1", 10L, new RespondInviteRequest("accept"));

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACCEPTED);
        verify(memberRepository).save(member);
    }

    @Test
    void respondToInvite_decline() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.PENDING).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.findByGroupPlanIdAndStudentCode(10L, "member1")).thenReturn(Optional.of(member));

        service.respondToInvite("member1", 10L, new RespondInviteRequest("decline"));

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DECLINED);
    }

    @Test
    void respondToInvite_alreadyResponded_throws() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.ACCEPTED).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.findByGroupPlanIdAndStudentCode(10L, "member1")).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.respondToInvite("member1", 10L, new RespondInviteRequest("accept")))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void requestDeletePlan_success() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.ACCEPTED).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);
        when(groupPlanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.requestDeletePlan("member1", 10L);

        assertThat(plan.isDeleteRequested()).isTrue();
        assertThat(plan.getDeleteRequestedBy()).isEqualTo("member1");
        verify(notificationService).notifyGroupPlanDeleteRequest(eq(plan), eq("member1"));
    }

    @Test
    void requestDeletePlan_creatorThrows() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.requestDeletePlan("creator1", 10L))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void requestDeletePlan_alreadyRequestedThrows() {
        plan.setDeleteRequested(true);
        plan.setDeleteRequestedBy("other");
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.ACCEPTED).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(10L, "member1", MemberStatus.ACCEPTED))
            .thenReturn(true);

        assertThatThrownBy(() -> service.requestDeletePlan("member1", 10L))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void approveDeletePlan_approve() {
        plan.setDeleteRequested(true);
        plan.setDeleteRequestedBy("member1");
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(groupPlanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.approveDeletePlan("creator1", 10L, new ApproveDeleteRequest("approve"));

        assertThat(plan.isActive()).isFalse();
        verify(notificationService).notifyGroupPlanDeleteApproved(eq(plan), eq("member1"), eq(true));
    }

    @Test
    void approveDeletePlan_reject() {
        plan.setDeleteRequested(true);
        plan.setDeleteRequestedBy("member1");
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(groupPlanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.approveDeletePlan("creator1", 10L, new ApproveDeleteRequest("reject"));

        assertThat(plan.isDeleteRequested()).isFalse();
        assertThat(plan.getDeleteRequestedBy()).isNull();
        verify(notificationService).notifyGroupPlanDeleteApproved(eq(plan), eq("member1"), eq(false));
    }

    @Test
    void approveDeletePlan_notCreatorThrows() {
        plan.setDeleteRequested(true);
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.approveDeletePlan("member1", 10L, new ApproveDeleteRequest("approve")))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void approveDeletePlan_noRequestPendingThrows() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.approveDeletePlan("creator1", 10L, new ApproveDeleteRequest("approve")))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void leavePlan_success() {
        GroupPlanMember member = GroupPlanMember.builder()
            .id(1L).groupPlan(plan).studentCode("member1").status(MemberStatus.ACCEPTED).build();
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberRepository.findByGroupPlanIdAndStudentCode(10L, "member1")).thenReturn(Optional.of(member));

        service.leavePlan("member1", 10L);

        verify(memberRepository).delete(member);
    }

    @Test
    void leavePlan_creatorCannotLeave() {
        when(groupPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.leavePlan("creator1", 10L))
            .isInstanceOf(BadRequestException.class);
    }
}
