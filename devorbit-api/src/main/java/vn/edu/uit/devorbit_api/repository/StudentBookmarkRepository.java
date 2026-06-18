package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentBookmark;

import java.util.List;
import java.util.Optional;

/**
 * STUDENT BOOKMARK REPOSITORY = data access for student bookmarks.
 *
 * Students can bookmark courses, repos, tutorials, etc.
 * Each bookmark is unique per (student_id + target_type + target_id).
 *
 * The deleteByTargetTypeAndTargetId methods are used for cascade cleanup:
 * when a course is deleted, all its bookmarks are removed automatically.
 */
@Repository
public interface StudentBookmarkRepository extends JpaRepository<StudentBookmark, Long> {

    /** All bookmarks for a student, newest first. */
    List<StudentBookmark> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    /** Find a specific bookmark (e.g., check if already bookmarked). */
    Optional<StudentBookmark> findByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);

    /** Quick check: is this already bookmarked? Avoids loading the full entity. */
    boolean existsByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);

    /** Remove a bookmark (unbookmark action). */
    void deleteByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);

    /** Remove bookmarks for a target (cascade cleanup on target deletion). */
    void deleteByTargetTypeAndTargetId(String targetType, Long targetId);

    /** Remove bookmarks for multiple targets (batch cascade cleanup). */
    void deleteByTargetTypeAndTargetIdIn(String targetType, List<Long> targetIds);
}
