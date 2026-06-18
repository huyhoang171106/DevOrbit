package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseObjective;
import java.util.List;
import java.util.UUID;

/**
 * COURSE OBJECTIVE REPOSITORY = learning goals extracted from course syllabi.
 */
@Repository
public interface CourseObjectiveRepository extends JpaRepository<CourseObjective, UUID> {
    List<CourseObjective> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
