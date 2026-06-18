package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseReference;
import java.util.List;
import java.util.UUID;

/**
 * COURSE REFERENCE REPOSITORY = textbook/reference citations from syllabi.
 */
@Repository
public interface CourseReferenceRepository extends JpaRepository<CourseReference, UUID> {
    List<CourseReference> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);
}
