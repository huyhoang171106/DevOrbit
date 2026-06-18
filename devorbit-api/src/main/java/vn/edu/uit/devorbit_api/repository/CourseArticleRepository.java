package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseArticle;
import java.util.List;

/**
 * COURSE ARTICLE REPOSITORY = data access for reference articles linked to courses.
 * Managed by admin via AdminCourseResourceController.
 */
@Repository
public interface CourseArticleRepository extends JpaRepository<CourseArticle, Long> {

    /** All articles for a course, newest first. */
    List<CourseArticle> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    /** Cascade cleanup when course is deleted. */
    void deleteByCourseId(Long courseId);
}
