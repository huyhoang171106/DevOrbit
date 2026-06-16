package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.Note;
import vn.edu.uit.devorbit_api.entity.NoteTargetType;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByStudentIdOrderByUpdatedAtDesc(Long studentId);

    List<Note> findAllByOrderByUpdatedAtDesc();

    void deleteByTargetTypeAndTargetId(NoteTargetType targetType, Long targetId);

    void deleteByTargetTypeAndTargetIdIn(NoteTargetType targetType, List<Long> targetIds);
}
