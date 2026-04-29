package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class PublicCourseController {
    private final CourseService courseService;

    @GetMapping
    public List<CourseSummaryResponse> getCourses() {
        return courseService.getActiveCourseSummaries();
    }

    @GetMapping("/{id}")
    public CourseDetailResponse getCourseDetail(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }
}
