package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseSyllabus;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSyllabusRepository extends JpaRepository<CourseSyllabus, UUID> {
    Optional<CourseSyllabus> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
