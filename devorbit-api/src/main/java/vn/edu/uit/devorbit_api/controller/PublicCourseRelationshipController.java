package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.List;

@RestController
@RequestMapping("/api/courses/relationships")
@RequiredArgsConstructor
public class PublicCourseRelationshipController {

    private final CourseRelationshipService relationshipService;

    @GetMapping
    public List<CourseRelationshipResponse> getAll() {
        return relationshipService.getAll();
    }

    @GetMapping("/course/{courseId}")
    public List<CourseRelationshipResponse> getByCourse(@PathVariable Long courseId) {
        return relationshipService.getByCourse(courseId);
    }
}
