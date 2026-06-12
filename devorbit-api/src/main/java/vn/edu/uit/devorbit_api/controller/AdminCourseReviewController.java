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

@RestController
@RequestMapping("/api/admin/reviews/courses")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCourseReviewController {

    private final CourseReviewRepository courseReviewRepo;

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
