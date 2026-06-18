package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.Note;
import vn.edu.uit.devorbit_api.entity.NoteTargetType;
import java.util.List;

/**
 * NOTE REPOSITORY = data access for student notes.
 *
 * Notes are Markdown documents written by students about courses or repos.
 * The NoteTargetType determines what the note is attached to:
 *   - COURSE → note about a course (targetId = Course.id)
 *   - REPO   → note about a repo (targetId = GithubRepo.id)
 *   - NONE   → general note, no specific attachment
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /** All notes for a student, most recently updated first. */
    List<Note> findByStudentIdOrderByUpdatedAtDesc(Long studentId);

    /** [ADMIN] All notes across all students, most recently updated first. */
    List<Note> findAllByOrderByUpdatedAtDesc();

    /** Delete notes by target (cascade cleanup when a course/repo is deleted). */
    void deleteByTargetTypeAndTargetId(NoteTargetType targetType, Long targetId);

    /** Batch delete notes for multiple targets. */
    void deleteByTargetTypeAndTargetIdIn(NoteTargetType targetType, List<Long> targetIds);
}
