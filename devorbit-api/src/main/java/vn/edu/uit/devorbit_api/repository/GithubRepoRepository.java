package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GithubRepo;

import java.util.Optional;
import java.util.List;
@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {
    List<GithubRepo> findBySubjectId(String subjectId);

    List<GithubRepo> findByActiveTrue();

    List<GithubRepo> findByCourseIdAndActiveTrue(Long courseId);

    Optional<GithubRepo> findByGithubUrlAndCourseId(String githubUrl, Long courseId);

    List<GithubRepo> findByCourseIdAndActiveTrueAndPrimaryLanguage(Long courseId, String primaryLanguage);

    @Query("SELECT DISTINCT r FROM GithubRepo r JOIN TechStack t ON t.repo.id = r.id WHERE r.course.id = :courseId AND r.active = true AND t.name = :techStack")
    List<GithubRepo> findByCourseIdAndActiveTrueAndTechStack(@Param("courseId") Long courseId, @Param("techStack") String techStack);
}
