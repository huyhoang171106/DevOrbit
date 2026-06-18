package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseRelationship;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.util.List;
import java.util.Optional;

/**
 * COURSE RELATIONSHIP REPOSITORY = data access for links between courses.
 *
 * Used by the KnowledgeGraphService to build the interactive course graph.
 * @EntityGraph eagerly loads both course and relatedCourse to avoid N+1 queries
 * when rendering the graph.
 *
 * Each relationship connects TWO courses (course → relatedCourse) with a type:
 *   PREREQUISITE  → course depends on relatedCourse
 *   COMPLEMENTARY → courses are related but no dependency
 *   COREQUISITE   → courses should be taken together
 */
@Repository
public interface CourseRelationshipRepository extends JpaRepository<CourseRelationship, Long> {

    /** All relationships with both courses eagerly loaded (for knowledge graph rendering). */
    @EntityGraph(attributePaths = {"course", "relatedCourse"})
    List<CourseRelationship> findAll();

    /** All relationships involving a specific course (as either source or target). */
    @EntityGraph(attributePaths = {"course", "relatedCourse"})
    List<CourseRelationship> findByCourseIdOrRelatedCourseIdOrderByCreatedAtAsc(Long courseId, Long relatedCourseId);

    /** Find a specific relationship (used to check for duplicates before creating). */
    Optional<CourseRelationship> findByCourseIdAndRelatedCourseIdAndRelationType(
            Long courseId, Long relatedCourseId, CourseRelationType relationType);

    /** All relationships of a specific type (e.g., all PREREQUISITE relations). */
    List<CourseRelationship> findByRelationType(CourseRelationType relationType);

    /** Delete all relationships involving a course (cascade cleanup on course deletion). */
    void deleteByCourseIdOrRelatedCourseId(Long courseId1, Long courseId2);
}
