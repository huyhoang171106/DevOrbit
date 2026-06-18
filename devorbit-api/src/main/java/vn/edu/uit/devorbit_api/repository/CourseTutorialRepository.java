package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseTutorial;
import java.util.List;

/**
 * COURSE TUTORIAL REPOSITORY = data access for tutorials linked to courses.
 */
@Repository
public interface CourseTutorialRepository extends JpaRepository<CourseTutorial, Long> {

    /** All tutorials for a course, newest first. */
    List<CourseTutorial> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    /** Cascade cleanup when course is deleted. */
    void deleteByCourseId(Long courseId);
}
