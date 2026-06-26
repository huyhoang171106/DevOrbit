package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.StudentTechStackRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentTechStackResponse;
import vn.edu.uit.devorbit_api.service.StudentTechStackService;

import java.util.List;

@RestController
@RequestMapping("/api/student/tech-stacks")
@RequiredArgsConstructor
public class StudentTechStackController {

    private final StudentTechStackService studentTechStackService;

    @GetMapping
    public List<StudentTechStackResponse> getTechStacks(@AuthenticationPrincipal String studentCode) {
        return studentTechStackService.getTechStacks(studentCode);
    }

    @PostMapping
    public StudentTechStackResponse addTechStack(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid StudentTechStackRequest request) {
        return studentTechStackService.addTechStack(studentCode, request);
    }

    @DeleteMapping("/{id}")
    public void removeTechStack(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        studentTechStackService.removeTechStack(studentCode, id);
    }

    @DeleteMapping("/by-name/{name}")
    public void removeTechStackByName(
            @AuthenticationPrincipal String studentCode,
            @PathVariable String name) {
        studentTechStackService.removeTechStackByName(studentCode, name);
    }
}
