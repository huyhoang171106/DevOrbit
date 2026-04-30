package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.BookmarkTargetType;
import vn.edu.uit.devorbit_api.entity.StudentBookmark;
import vn.edu.uit.devorbit_api.entity.StudentUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBookmarkRepository extends JpaRepository<StudentBookmark, Long> {
    List<StudentBookmark> findByStudentOrderByCreatedAtDesc(StudentUser student);
    Optional<StudentBookmark> findByStudentAndTargetTypeAndTargetId(StudentUser student, BookmarkTargetType targetType, Long targetId);
    void deleteByStudentAndTargetTypeAndTargetId(StudentUser student, BookmarkTargetType targetType, Long targetId);
}
