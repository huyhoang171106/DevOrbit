package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GithubRepo;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

/**
 * GITHUB REPO REPOSITORY = data access for approved GitHub repositories.
 *
 * This is one of the MOST QUERIED repositories — repos are displayed on
 * course pages, search results, and the knowledge graph.
 *
 * @EntityGraph is used extensively to solve the N+1 query problem:
 *   Without @EntityGraph: querying 10 repos = 1 query for repos + 10 queries for techStacks
 *   With @EntityGraph: 1 JOIN query fetches everything at once
 *
 * Key relationships (loaded eagerly via @EntityGraph):
 *   - techStacks: the technology tags (React, Spring Boot...)
 *   - course: the Course this repo belongs to
 */
@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {

    /** [LEGACY] Find repos by subjectId (old course code field). Prefer findByCourseId(). */
    List<GithubRepo> findBySubjectId(String subjectId);

    /** All active repos (visible to students), with techStacks + course eagerly loaded. */
    @EntityGraph(attributePaths = {"techStacks", "course"})
    List<GithubRepo> findByActiveTrue();

    /** Count of active repos (for admin stats dashboard). */
    long countByActiveTrue();

    /** All active repos for one course (used on course detail page). */
    @EntityGraph(attributePaths = {"techStacks", "course"})
    List<GithubRepo> findByCourseIdAndActiveTrue(Long courseId);

    /**
     * ALL repos for a course (active + inactive). Used during cascade delete
     * to clean up associated data before deleting the course.
     */
    @EntityGraph(attributePaths = {"techStacks"})
    List<GithubRepo> findByCourseId(Long courseId);

    /**
     * Batch fetch repos for MULTIPLE courses at once.
     * Replaces the N+1 anti-pattern: looping over courses and querying repos one by one.
     */
    @EntityGraph(attributePaths = {"techStacks", "course"})
    List<GithubRepo> findByCourseIdInAndActiveTrue(Collection<Long> courseIds);

    /** Check if a URL already exists for a course (avoid duplicates). */
    Optional<GithubRepo> findByGithubUrlAndCourseId(String githubUrl, Long courseId);

    /** Filter repos by course + primary programming language. */
    List<GithubRepo> findByCourseIdAndActiveTrueAndPrimaryLanguage(Long courseId, String primaryLanguage);

    /**
     * Filter repos by course + tech stack tag (case-insensitive).
     * Uses a JOIN query on the ManyToMany join table (repo_tech_stacks).
     * DISTINCT ensures each repo appears only once even if it has multiple matching stacks.
     */
    @Query("""
            SELECT DISTINCT r FROM GithubRepo r JOIN r.techStacks t
            WHERE r.course.id = :courseId AND r.active = true AND lower(t.name) = lower(:techStack)
            """)
    List<GithubRepo> findByCourseIdAndActiveTrueAndTechStack(@Param("courseId") Long courseId, @Param("techStack") String techStack);

    /** Get ALL GitHub URLs across all repos (used by scanner to find new repos). */
    @Query("SELECT r.githubUrl FROM GithubRepo r")
    List<String> findAllGithubUrls();

    /** Last 10 active repos (for admin dashboard "recent repos" widget). */
    @EntityGraph(attributePaths = {"techStacks", "course"})
    List<GithubRepo> findTop10ByActiveTrueOrderByIdDesc();

    /** All repos with techStacks eagerly loaded (for batch processing). */
    @EntityGraph(attributePaths = {"techStacks"})
    @Query("SELECT r FROM GithubRepo r")
    List<GithubRepo> findAllWithTechStacks();

    /**
     * Active repos that have STALE or missing metadata (no lastPushedAt).
     * These need to be refreshed from the GitHub API.
     * Called periodically by the GitHub sync scheduler.
     */
    @Query("SELECT r FROM GithubRepo r WHERE r.active = true AND (r.lastPushedAt IS NULL OR r.lastPushedAt = '')")
    List<GithubRepo> findStaleActiveRepos();

    @EntityGraph(attributePaths = {"course"})
    List<GithubRepo> findTop10ByActiveTrueOrderByViewCountDesc();

    @EntityGraph(attributePaths = {"course"})
    List<GithubRepo> findTop100ByActiveTrueOrderByViewCountDesc();
}

