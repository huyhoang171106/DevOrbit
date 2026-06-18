package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseTool;
import java.util.List;
import java.util.UUID;

/**
 * COURSE TOOL REPOSITORY = software tools/technologies mentioned in course syllabi.
 * Distinct from TechStack — these are tools students USE in the course,
 * not technology tags on GitHub repos.
 */
@Repository
public interface CourseToolRepository extends JpaRepository<CourseTool, UUID> {
    List<CourseTool> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
