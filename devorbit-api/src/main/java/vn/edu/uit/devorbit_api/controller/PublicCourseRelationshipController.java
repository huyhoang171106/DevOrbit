package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.List;

/**
 * PUBLIC COURSE RELATIONSHIP CONTROLLER = view course relationships.
 *
 * Shows how courses connect: prerequisites, complementary courses, etc.
 * Read-only — only admins can create/delete relationships.
 *
 * No authentication required.
 */
@RestController
@RequestMapping("/api/courses/relationships")
@RequiredArgsConstructor
public class PublicCourseRelationshipController {

    private final CourseRelationshipService relationshipService;

    /** Get ALL course relationships (prerequisites, etc.) */
    @GetMapping
    public List<CourseRelationshipResponse> getAll() {
        return relationshipService.getAll();
    }

    /** Get relationships for ONE course */
    @GetMapping("/course/{courseId}")
    public List<CourseRelationshipResponse> getByCourse(@PathVariable Long courseId) {
        return relationshipService.getByCourse(courseId);
    }
}
