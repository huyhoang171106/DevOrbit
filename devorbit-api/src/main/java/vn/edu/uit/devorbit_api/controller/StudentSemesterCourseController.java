package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.SemesterCourseRequest;
import vn.edu.uit.devorbit_api.dto.student.SemesterCourseResponse;
import vn.edu.uit.devorbit_api.service.StudentSemesterCourseService;

import java.util.List;

@RestController
@RequestMapping("/api/student/semester-courses")
@RequiredArgsConstructor
public class StudentSemesterCourseController {

    private final StudentSemesterCourseService studentSemesterCourseService;

    @GetMapping
    public List<SemesterCourseResponse> getSelections(@AuthenticationPrincipal String studentCode) {
        return studentSemesterCourseService.getSelections(studentCode);
    }

    @PostMapping
    public SemesterCourseResponse addSelection(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid SemesterCourseRequest request) {
        return studentSemesterCourseService.addSelection(studentCode, request);
    }

    @DeleteMapping("/{courseId}")
    public void removeSelection(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long courseId) {
        studentSemesterCourseService.removeSelection(studentCode, courseId);
    }
}
