package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseSession;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, UUID> {
    List<CourseSession> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
