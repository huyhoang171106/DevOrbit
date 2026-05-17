package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseRelationship;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRelationshipRepository extends JpaRepository<CourseRelationship, Long> {
    List<CourseRelationship> findByCourseIdOrRelatedCourseIdOrderByCreatedAtAsc(Long courseId, Long relatedCourseId);

    Optional<CourseRelationship> findByCourseIdAndRelatedCourseIdAndRelationType(
            Long courseId, Long relatedCourseId, CourseRelationType relationType);

    List<CourseRelationship> findByRelationType(CourseRelationType relationType);

    void deleteByCourseIdOrRelatedCourseId(Long courseId1, Long courseId2);
}
