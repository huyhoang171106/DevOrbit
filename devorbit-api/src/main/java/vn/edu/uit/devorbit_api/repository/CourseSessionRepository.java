package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseSession;
import java.util.List;
import java.util.UUID;

/**
 * COURSE SESSION REPOSITORY = data access for individual lecture/class sessions.
 *
 * Each session represents one class meeting with a topic, activities,
 * and optional assessment component. Extracted from the course syllabus.
 */
@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, UUID> {

    /** All sessions for a course, ordered by session number (index). */
    List<CourseSession> findByCourseCode(String courseCode);

    /** Delete sessions when a course is removed. */
    void deleteByCourseCode(String courseCode);

    /** Delete sessions when the source syllabus document is removed. */
    void deleteBySourceId(UUID sourceId);
}
