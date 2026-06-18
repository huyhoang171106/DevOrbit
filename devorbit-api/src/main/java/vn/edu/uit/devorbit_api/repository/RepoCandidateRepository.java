package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;

import java.util.List;
import java.util.Optional;

/**
 * REPO CANDIDATE REPOSITORY = data access for repos discovered via GitHub scanning.
 *
 * The scanning flow:
 *   1. Admin triggers a scan (or it runs on a schedule)
 *   2. GitHub API returns matching repos
 *   3. Each found repo becomes a RepoCandidate with status = NEW
 *   4. Admin reviews candidates in the admin panel
 *   5. Approved → GithubRepo created. Rejected → stays as rejected candidate.
 *
 * @EntityGraph("course") eagerly loads the Course to avoid N+1 queries
 * when showing the candidate list with course names.
 */
@Repository
public interface RepoCandidateRepository extends JpaRepository<RepoCandidate, Long> {

    /** All candidates with a given status (e.g., all NEW candidates awaiting review). */
    @EntityGraph(attributePaths = {"course"})
    List<RepoCandidate> findByStatus(RepoCandidateStatus status);

    /** 10 most recent candidates (for admin dashboard). */
    List<RepoCandidate> findTop10ByOrderByIdDesc();
    @EntityGraph(attributePaths = {"course"})
    List<RepoCandidate> findTop10ByStatusOrderByIdDesc(RepoCandidateStatus status);

    /** Delete candidates associated with a course (cascade cleanup on course deletion). */
    void deleteByCourseId(Long courseId);

    /** Count candidates by status (for admin stats). */
    long countByStatus(RepoCandidateStatus status);

    /** Filter candidates by status + assignee (for reviewer workflow). */
    @EntityGraph(attributePaths = {"course"})
    List<RepoCandidate> findByStatusAndAssignedReviewer(RepoCandidateStatus status, String assignedReviewer);

    /** Check for duplicate candidate (same GitHub URL + same course). */
    Optional<RepoCandidate> findByGithubUrlAndCourseId(String githubUrl, Long courseId);

    /** Count candidates for a specific reviewer by status. */
    long countByStatusAndAssignedReviewer(RepoCandidateStatus status, String assignedReviewer);

    /** Count candidates in MULTIPLE statuses for a reviewer. */
    long countByStatusInAndAssignedReviewer(List<RepoCandidateStatus> statuses, String assignedReviewer);

    /** Total candidates assigned to a reviewer. */
    long countByAssignedReviewer(String assignedReviewer);

    /** All GitHub URLs already discovered for a course (avoid re-adding). */
    @Query("SELECT r.githubUrl FROM RepoCandidate r WHERE r.course.id = :courseId")
    List<String> findGithubUrlByCourseId(Long courseId);

    /** All GitHub URLs across ALL candidates (for global dedup). */
    @Query("SELECT r.githubUrl FROM RepoCandidate r")
    List<String> findAllGithubUrls();

    /**
     * Update the repository context data (README excerpt, file tree, hasReadme flag).
     * This is fetched from GitHub after the candidate is created.
     * Uses @Modifying + @Transactional because it's an UPDATE, not a SELECT.
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE RepoCandidate r
            SET r.readmeExcerpt = :excerpt,
                r.fileTree = :fileTree,
                r.hasReadme = :hasReadme
            WHERE r.id = :id
            """)
    void updateRepositoryContext(
        @org.springframework.data.repository.query.Param("id") Long id,
        @org.springframework.data.repository.query.Param("excerpt") String excerpt,
        @org.springframework.data.repository.query.Param("fileTree") String fileTree,
        @org.springframework.data.repository.query.Param("hasReadme") Boolean hasReadme
    );
}
