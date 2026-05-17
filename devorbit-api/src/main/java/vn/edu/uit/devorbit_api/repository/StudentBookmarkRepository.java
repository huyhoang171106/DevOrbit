package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentBookmark;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBookmarkRepository extends JpaRepository<StudentBookmark, Long> {
    List<StudentBookmark> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    Optional<StudentBookmark> findByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);
    boolean existsByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);
    void deleteByStudentIdAndTargetTypeAndTargetId(Long studentId, String targetType, Long targetId);
}
