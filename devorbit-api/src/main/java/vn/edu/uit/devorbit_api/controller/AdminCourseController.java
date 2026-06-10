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

/**
 * ADMIN COURSE CONTROLLER = CRUD endpoints for managing courses.
 * Only accessible to users with the ADMIN role.
 *
 * SECURITY:
 * - All endpoints under /api/admin/** require authentication + ROLE_ADMIN
 * - Configured in SecurityConfig.java
 * - Unauthorized requests get HTTP 401 (Unauthorized) or HTTP 403 (Forbidden)
 *
 * COMPARED TO PublicCourseController:
 *   Public  → anyone can READ courses
 *   Admin   → only admins can CREATE / UPDATE / DELETE courses
 *
 * REQUEST FLOW EXAMPLE (POST /api/admin/courses):
 *   1. Admin sends POST with JSON body
 *   2. SecurityConfig checks JWT token + ROLE_ADMIN
 *   3. This controller receives the request
 *   4. @Valid validates the request body
 *   5. Controller calls courseService.createCourse(request)
 *   6. Service creates the Course entity and saves it
 *   7. Response returns with the new course data + HTTP 200
 */
@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    /**
     * GET /api/admin/courses
     * Returns ALL courses (including inactive ones) for admin management.
     */
    @GetMapping
    public List<CourseSummaryResponse> list() {
        return courseService.getAllCourseSummaries();
    }

    /**
     * POST /api/admin/courses
     * Creates a new course.
     * Request body is validated (@Valid) — checks required fields.
     */
    @PostMapping
    public CourseDetailResponse create(@RequestBody @Valid AdminCourseUpsertRequest request) {
        return courseService.createCourse(request);
    }

    /**
     * PUT /api/admin/courses/{id}
     * Updates an existing course. All fields are replaced.
     */
    @PutMapping("/{id}")
    public CourseDetailResponse update(@PathVariable Long id, @RequestBody @Valid AdminCourseUpsertRequest request) {
        return courseService.updateCourse(id, request);
    }

    /**
     * DELETE /api/admin/courses/{id}
     * Deletes a course from the database.
     * Returns HTTP 204 (No Content) on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
