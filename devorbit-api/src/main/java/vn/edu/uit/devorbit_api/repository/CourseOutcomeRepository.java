package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseOutcome;
import java.util.List;
import java.util.UUID;

/**
 * COURSE OUTCOME REPOSITORY = measurable skills/outcomes from course syllabi.
 */
@Repository
public interface CourseOutcomeRepository extends JpaRepository<CourseOutcome, UUID> {
    List<CourseOutcome> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
