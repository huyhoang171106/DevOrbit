package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseReview;

import java.util.List;
import java.util.Optional;

/**
 * COURSE REVIEW REPOSITORY = data access for student course reviews.
 *
 * Each student can leave ONE review per course (rating 1-5 + optional comment).
 * The unique constraint (course_id + student_id) in CourseReview entity enforces this.
 * Updating a review reuses the same row.
 *
 * Naming convention reminder:
 *   findByCourseIdAndStudentId → SELECT ... WHERE course_id = ? AND student_id = ?
 *   OrderByUpdatedAtDesc       → ORDER BY updated_at DESC
 *   Top10                      → LIMIT 10
 *   Spring Data JPA parses the method name and generates SQL automatically.
 */
@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    /**
     * Find a specific student's review for a specific course.
     * Used to check if student already reviewed, and to retrieve existing review for editing.
     *
     * @param courseId  the course's database ID
     * @param studentId the student's database ID
     * @return Optional containing the review, or empty if student hasn't reviewed yet
     */
    Optional<CourseReview> findByCourseIdAndStudentId(Long courseId, Long studentId);

    /**
     * Get all reviews for a course, newest first.
     * Displayed on the course detail page.
     */
    List<CourseReview> findByCourseIdOrderByUpdatedAtDesc(Long courseId);

    /** [ADMIN] All reviews across all courses, newest first. */
    List<CourseReview> findAllByOrderByCreatedAtDesc();

    /** [ADMIN] Last 10 reviews for the admin dashboard. */
    List<CourseReview> findTop10ByOrderByCreatedAtDesc();

    /**
     * Delete all reviews for a course (used when deleting the course itself).
     * Cascade cleanup to avoid foreign key violations.
     */
    void deleteByCourseId(Long courseId);

    /**
     * Calculate the AVERAGE rating for a course.
     * Uses JPQL (Java Persistence Query Language) — similar to SQL but works with
     * entity field names instead of column names.
     *
     * Example: If course has ratings [5, 4, 3], returns 4.0
     * If no reviews exist, returns NULL.
     *
     * @param courseId the course's database ID
     * @return average rating as Double (1.0-5.0), or null if no reviews
     */
    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.id = :courseId")
    Double averageRatingByCourseId(Long courseId);
}
