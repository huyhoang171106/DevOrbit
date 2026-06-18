package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseSyllabus;
import java.util.Optional;
import java.util.UUID;

/**
 * COURSE SYLLABUS REPOSITORY = data access for course syllabus details.
 *
 * The syllabus contains structured information about a course:
 * credits, hours, prerequisites, department, description.
 * Each syllabus belongs to one KnowledgeSource (the document it was extracted from).
 */
@Repository
public interface CourseSyllabusRepository extends JpaRepository<CourseSyllabus, UUID> {

    /** Find syllabus by course code (e.g., "SE101"). */
    Optional<CourseSyllabus> findByCourseCode(String courseCode);

    /** Delete syllabus data when a course is removed. */
    void deleteByCourseCode(String courseCode);

    /** Delete syllabus when its source document is removed. */
    void deleteBySourceId(UUID sourceId);
}
