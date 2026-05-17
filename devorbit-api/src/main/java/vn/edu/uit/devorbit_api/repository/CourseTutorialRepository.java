package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseTutorial;
import java.util.List;

@Repository
public interface CourseTutorialRepository extends JpaRepository<CourseTutorial, Long> {
    List<CourseTutorial> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    void deleteByCourseId(Long courseId);
}
