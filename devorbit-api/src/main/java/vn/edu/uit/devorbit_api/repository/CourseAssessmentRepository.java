package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseAssessment;
import java.util.List;
import java.util.UUID;

/**
 * COURSE ASSESSMENT REPOSITORY = grading components from course syllabi.
 * Each assessment has a code (e.g., "BT" for homework), description, and weight percentage.
 */
@Repository
public interface CourseAssessmentRepository extends JpaRepository<CourseAssessment, UUID> {
    List<CourseAssessment> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
