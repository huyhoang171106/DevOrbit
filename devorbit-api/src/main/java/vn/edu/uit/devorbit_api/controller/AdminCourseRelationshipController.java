package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipRequest;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.List;

/**
 * ADMIN COURSE RELATIONSHIP CONTROLLER = manage prerequisites and relationships.
 *
 * Course relationships define how courses connect in the knowledge graph:
 * - PREREQUISITE: course A must be done before course B
 * - COMPLEMENTARY: courses are related but order doesn't matter
 * - COREQUISITE: courses should be taken together
 *
 * All endpoints require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/courses/relationships")
@RequiredArgsConstructor
public class AdminCourseRelationshipController {

    private final CourseRelationshipService relationshipService;

    /** Get ALL relationships */
    @GetMapping
    public List<CourseRelationshipResponse> getAll() {
        return relationshipService.getAll();
    }

    /** Get relationships for ONE course */
    @GetMapping("/course/{courseId}")
    public List<CourseRelationshipResponse> getByCourse(@PathVariable Long courseId) {
        return relationshipService.getByCourse(courseId);
    }

    /** Create a new relationship between two courses */
    @PostMapping
    public CourseRelationshipResponse create(@RequestBody @Valid CourseRelationshipRequest request) {
        return relationshipService.create(request);
    }

    /** Delete a relationship */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        relationshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
