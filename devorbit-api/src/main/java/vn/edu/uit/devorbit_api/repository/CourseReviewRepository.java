package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    Optional<CourseReview> findByCourseIdAndStudentId(Long courseId, Long studentId);
    List<CourseReview> findByCourseIdOrderByUpdatedAtDesc(Long courseId);

    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.id = :courseId")
    Double averageRatingByCourseId(Long courseId);
}
