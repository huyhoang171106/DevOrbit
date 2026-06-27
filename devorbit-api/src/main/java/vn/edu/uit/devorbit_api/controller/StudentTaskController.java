package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.CreateTaskRequest;
import vn.edu.uit.devorbit_api.dto.student.TaskResponse;
import vn.edu.uit.devorbit_api.dto.student.UpdateTaskRequest;
import vn.edu.uit.devorbit_api.service.PersonalTaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/tasks")
@RequiredArgsConstructor
public class StudentTaskController {

    private final PersonalTaskService personalTaskService;

    @GetMapping
    public List<TaskResponse> getTasks(
            @AuthenticationPrincipal String studentCode,
            @RequestParam(required = false, defaultValue = "all") String filter) {
        return personalTaskService.getTasks(studentCode, filter);
    }

    @PostMapping
    public TaskResponse createTask(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid CreateTaskRequest request) {
        return personalTaskService.createTask(studentCode, request);
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateTaskRequest request) {
        return personalTaskService.updateTask(studentCode, taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId) {
        personalTaskService.deleteTask(studentCode, taskId);
    }

    @PatchMapping("/{taskId}/toggle")
    public TaskResponse toggleTask(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long taskId,
            @RequestBody Map<String, Boolean> body) {
        boolean completed = body.getOrDefault("completed", false);
        return personalTaskService.toggleTask(studentCode, taskId, completed);
    }
}
