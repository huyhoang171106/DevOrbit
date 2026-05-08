package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"techStacks"})
    List<GithubRepo> findByActiveTrue();

    long countByActiveTrue();

    @EntityGraph(attributePaths = {"techStacks"})
    List<GithubRepo> findByCourseIdAndActiveTrue(Long courseId);

    Optional<GithubRepo> findByGithubUrlAndCourseId(String githubUrl, Long courseId);

    List<GithubRepo> findByCourseIdAndActiveTrueAndPrimaryLanguage(Long courseId, String primaryLanguage);

    @Query("""
            SELECT DISTINCT r FROM GithubRepo r JOIN r.techStacks t
            WHERE r.course.id = :courseId AND r.active = true AND lower(t.name) = lower(:techStack)
            """)
    List<GithubRepo> findByCourseIdAndActiveTrueAndTechStack(@Param("courseId") Long courseId, @Param("techStack") String techStack);

    @Query("SELECT r.githubUrl FROM GithubRepo r")
    List<String> findAllGithubUrls();

    @EntityGraph(attributePaths = {"techStacks", "course"})
    List<GithubRepo> findTop10ByActiveTrueOrderByIdDesc();
}
