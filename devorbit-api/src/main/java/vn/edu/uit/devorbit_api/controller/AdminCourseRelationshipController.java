package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipRequest;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses/relationships")
@RequiredArgsConstructor
public class AdminCourseRelationshipController {

    private final CourseRelationshipService relationshipService;

    @GetMapping
    public List<CourseRelationshipResponse> getAll() {
        return relationshipService.getAll();
    }

    @GetMapping("/course/{courseId}")
    public List<CourseRelationshipResponse> getByCourse(@PathVariable Long courseId) {
        return relationshipService.getByCourse(courseId);
    }

    @PostMapping
    public CourseRelationshipResponse create(@RequestBody @Valid CourseRelationshipRequest request) {
        return relationshipService.create(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        relationshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
