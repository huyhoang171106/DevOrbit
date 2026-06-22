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
public class GroupPlanService {

    private static final Logger log = LoggerFactory.getLogger(GroupPlanService.class);
    private final GroupPlanRepository groupPlanRepository;
    private final GroupPlanMemberRepository memberRepository;
    private final GroupTaskRepository taskRepository;
    private final StudentUserRepository studentUserRepository;
    private final StudentNotificationService notificationService;

    @Transactional
    public GroupPlanResponse createPlan(String creatorStudentCode, CreateGroupPlanRequest request) {
        StudentUser creator = studentUserRepository.findByStudentCode(creatorStudentCode)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        LocalDate deadline = request.deadline() != null && !request.deadline().isBlank()
            ? LocalDate.parse(request.deadline()) : null;

        GroupPlan plan = GroupPlan.builder()
            .title(request.title())
            .description(request.description())
            .creatorStudentCode(creator.getStudentCode())
            .deadline(deadline)
            .build();

        plan = groupPlanRepository.save(plan);
        log.info("Group plan created: id={}, title={}, creator={}", plan.getId(), plan.getTitle(), creatorStudentCode);
        return GroupPlanResponse.from(plan);
    }

    @Transactional(readOnly = true)
    public List<GroupPlanResponse> getMyPlans(String studentCode) {
        return groupPlanRepository.findActiveByStudentCode(studentCode)
            .stream()
            .map(GroupPlanResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public GroupPlanResponse getPlanDetail(String studentCode, Long planId) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        validateMember(studentCode, plan);
        return GroupPlanResponse.from(plan);
    }

    @Transactional
    public void deletePlan(String studentCode, Long planId) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        if (!plan.getCreatorStudentCode().equals(studentCode)) {
            throw new BadRequestException("Only the creator can delete this plan");
        }
        plan.setActive(false);
        groupPlanRepository.save(plan);
        log.info("Group plan deleted: id={}, creator={}", planId, studentCode);
    }

    @Transactional
    public GroupPlanMemberResponse inviteMember(String inviterStudentCode, Long planId, InviteMemberRequest request) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        if (!plan.getCreatorStudentCode().equals(inviterStudentCode)) {
            throw new BadRequestException("Only the creator can invite members");
        }

        StudentUser invitedUser = studentUserRepository.findByStudentCode(request.studentCode())
            .orElseThrow(() -> new NotFoundException("Student not found: " + request.studentCode()));

        if (invitedUser.getStudentCode().equals(inviterStudentCode)) {
            throw new BadRequestException("Cannot invite yourself");
        }

        if (memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(planId, request.studentCode(), MemberStatus.ACCEPTED)) {
            throw new BadRequestException("Student is already a member of this plan");
        }

        GroupPlanMember existing = memberRepository.findByGroupPlanIdAndStudentCode(planId, request.studentCode())
            .orElse(null);
        if (existing != null && existing.getStatus() == MemberStatus.PENDING) {
            throw new BadRequestException("Invitation already sent to this student");
        }
        if (existing != null) {
            existing.setStatus(MemberStatus.PENDING);
            existing.setRespondedAt(null);
            existing.setInvitedAt(LocalDateTime.now());
            memberRepository.save(existing);
        } else {
            GroupPlanMember member = GroupPlanMember.builder()
                .groupPlan(plan)
                .studentCode(invitedUser.getStudentCode())
                .status(MemberStatus.PENDING)
                .build();
            member = memberRepository.save(member);
            existing = member;
        }

        notificationService.notifyGroupPlanInvite(plan, inviterStudentCode, invitedUser.getStudentCode());
        log.info("Invited {} to group plan id={}", request.studentCode(), planId);
        return GroupPlanMemberResponse.from(existing);
    }

    @Transactional(readOnly = true)
    public List<GroupPlanMemberResponse> getMembers(String studentCode, Long planId) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));
        validateMember(studentCode, plan);
        return memberRepository.findByGroupPlanId(planId)
            .stream()
            .map(GroupPlanMemberResponse::from)
            .toList();
    }

    @Transactional
    public void respondToInvite(String studentCode, Long planId, RespondInviteRequest request) {
        GroupPlan plan = groupPlanRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Group plan not found: " + planId));

        GroupPlanMember member = memberRepository.findByGroupPlanIdAndStudentCode(planId, studentCode)
            .orElseThrow(() -> new NotFoundException("Invitation not found"));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new BadRequestException("Invitation already responded");
        }

        String action = request.action().toUpperCase();
        MemberStatus newStatus;
        switch (action) {
            case "ACCEPT" -> newStatus = MemberStatus.ACCEPTED;
            case "DECLINE" -> newStatus = MemberStatus.DECLINED;
            default -> throw new BadRequestException("Action must be ACCEPT or DECLINE");
        }

        member.setStatus(newStatus);
        member.setRespondedAt(LocalDateTime.now());
        memberRepository.save(member);

        notificationService.notifyGroupPlanResponse(plan, studentCode, plan.getCreatorStudentCode(), newStatus);
        log.info("Student {} {}d invitation to group plan id={}", studentCode, action.toLowerCase(), planId);
    }

    private void validateMember(String studentCode, GroupPlan plan) {
        if (!plan.getCreatorStudentCode().equals(studentCode) &&
            !memberRepository.existsByGroupPlanIdAndStudentCodeAndStatus(plan.getId(), studentCode, MemberStatus.ACCEPTED)) {
            throw new BadRequestException("You are not a member of this group plan");
        }
    }
}
