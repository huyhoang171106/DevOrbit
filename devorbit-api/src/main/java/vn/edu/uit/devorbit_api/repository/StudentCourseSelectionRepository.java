package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentCourseSelection;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseSelectionRepository extends JpaRepository<StudentCourseSelection, Long> {

    List<StudentCourseSelection> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<StudentCourseSelection> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<StudentCourseSelection> findByCourseId(Long courseId);
}
