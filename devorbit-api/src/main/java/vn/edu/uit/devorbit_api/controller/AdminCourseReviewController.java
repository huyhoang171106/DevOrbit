package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CourseReviewAdminResponse;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for managing course reviews.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/reviews/courses - List all course reviews</li>
 *   <li>DELETE /api/admin/reviews/courses/{id} - Delete a course review</li>
 * </ul>
 * <p>
 * Security: ADMIN role required. Read-only by default; delete is explicitly transactional.
 */
@RestController
@RequestMapping("/api/admin/reviews/courses")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCourseReviewController {

    private final CourseReviewRepository courseReviewRepo;

    /**
     * List all course reviews ordered by creation date descending.
     * Student/course names fall back to "Deleted" if the related entity is missing.
     *
     * @return 200 OK with list of CourseReviewAdminResponse
     * @apiNote GET /api/admin/reviews/courses
     */
    @GetMapping
    public ResponseEntity<List<CourseReviewAdminResponse>> listCourseReviews() {
        return ResponseEntity.ok(courseReviewRepo.findAllByOrderByCreatedAtDesc()
            .stream().map(r -> CourseReviewAdminResponse.builder()
                .id(r.getId())
                .studentName(r.getStudent() != null ? r.getStudent().getFullName() : "Deleted")
                .courseName(r.getCourse() != null ? r.getCourse().getTenMH() : "Deleted")
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build())
            .collect(Collectors.toList()));
    }

    /**
     * Permanently delete a course review by ID.
     *
     * @param id Long ID of the course review to delete
     * @return 204 No Content if successful
     * @throws NotFoundException if review not found
     * @apiNote DELETE /api/admin/reviews/courses/{id}
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCourseReview(@PathVariable Long id) {
        if (!courseReviewRepo.existsById(id)) {
            throw new NotFoundException("Course review not found");
        }
        courseReviewRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
