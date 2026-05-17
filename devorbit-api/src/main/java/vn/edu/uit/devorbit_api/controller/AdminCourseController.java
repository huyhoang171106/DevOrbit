package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminCourseUpsertRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseSummaryResponse> list() {
        return courseService.getAllCourseSummaries();
    }

    @GetMapping("/{id}")
    public CourseDetailResponse detail(@PathVariable Long id) {
        return courseService.getAdminCourseDetail(id);
    }

    @PostMapping
    public CourseDetailResponse create(@RequestBody @Valid AdminCourseUpsertRequest request) {
        return courseService.createCourse(request);
    }

    @PutMapping("/{id}")
    public CourseDetailResponse update(@PathVariable Long id, @RequestBody @Valid AdminCourseUpsertRequest request) {
        return courseService.updateCourse(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
