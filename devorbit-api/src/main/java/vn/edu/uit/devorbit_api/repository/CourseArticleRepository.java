package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseArticle;
import java.util.List;

@Repository
public interface CourseArticleRepository extends JpaRepository<CourseArticle, Long> {
    List<CourseArticle> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    void deleteByCourseId(Long courseId);
}
