package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.*;
import vn.edu.uit.devorbit_api.service.GroupPlanService;
import vn.edu.uit.devorbit_api.service.GroupTaskService;

import java.util.List;

/**
 * STUDENT GROUP PLAN CONTROLLER = collaborative task management.
 *
 * Students can create group plans, invite others, and manage tasks together.
 * All endpoints require a valid student JWT token.
 */
@RestController
@RequestMapping("/api/student/group-plans")
@RequiredArgsConstructor
public class StudentGroupPlanController {

    private final GroupPlanService groupPlanService;
    private final GroupTaskService groupTaskService;

    // ─── Plan CRUD ───

    @PostMapping
    public GroupPlanResponse createPlan(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid CreateGroupPlanRequest request) {
        return groupPlanService.createPlan(studentCode, request);
    }

    @GetMapping
    public List<GroupPlanResponse> getMyPlans(@AuthenticationPrincipal String studentCode) {
        return groupPlanService.getMyPlans(studentCode);
    }

    @GetMapping("/{id}")
    public GroupPlanResponse getPlanDetail(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        return groupPlanService.getPlanDetail(studentCode, id);
    }

    @DeleteMapping("/{id}")
    public void deletePlan(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        groupPlanService.deletePlan(studentCode, id);
    }

    // ─── Members ───

    @PostMapping("/{id}/invite")
    public GroupPlanMemberResponse inviteMember(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id,
            @RequestBody @Valid InviteMemberRequest request) {
        return groupPlanService.inviteMember(studentCode, id, request);
    }

    @GetMapping("/{id}/members")
    public List<GroupPlanMemberResponse> getMembers(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        return groupPlanService.getMembers(studentCode, id);
    }

    @PostMapping("/{id}/respond")
    public void respondToInvite(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id,
            @RequestBody @Valid RespondInviteRequest request) {
        groupPlanService.respondToInvite(studentCode, id, request);
    }

    // ─── Tasks ───

    @GetMapping("/{id}/tasks")
    public List<GroupTaskResponse> getTasks(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        return groupTaskService.getTasks(studentCode, id);
    }

    @PostMapping("/{id}/tasks")
    public GroupTaskResponse addTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id,
            @RequestBody @Valid AddGroupTaskRequest request) {
        return groupTaskService.addTask(studentCode, id, request);
    }

    @PutMapping("/tasks/{taskId}")
    public GroupTaskResponse updateTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateGroupTaskRequest request) {
        return groupTaskService.updateTask(studentCode, taskId, request);
    }

    @PostMapping("/tasks/{taskId}/request-delete")
    public void requestDeleteTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId) {
        groupTaskService.requestDeleteTask(studentCode, taskId);
    }

    @GetMapping("/assigned-tasks")
    public List<GroupTaskResponse> getAssignedTasks(
            @AuthenticationPrincipal String studentCode) {
        return groupTaskService.getAssignedTasks(studentCode);
    }

    @PostMapping("/tasks/{taskId}/approve-delete")
    public void approveDeleteTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId,
            @RequestBody @Valid ApproveDeleteRequest request) {
        groupTaskService.approveDeleteTask(studentCode, taskId, request);
    }
}
