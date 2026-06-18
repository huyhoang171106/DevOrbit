package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoReview;

import java.util.List;
import java.util.Optional;

/**
 * REPO REVIEW REPOSITORY = data access for student reviews of GitHub repos.
 *
 * Works identically to CourseReviewRepository but for repos instead of courses.
 * Each student can leave ONE review per repo (rating 1-5 + optional comment).
 *
 * Unique constraint (repo_id + student_id) in RepoReview entity prevents duplicates.
 */
@Repository
public interface RepoReviewRepository extends JpaRepository<RepoReview, Long> {

    /** Find a specific student's review for a specific repo. */
    Optional<RepoReview> findByRepoIdAndStudentId(Long repoId, Long studentId);

    /** All reviews for a repo, newest first. */
    List<RepoReview> findByRepoIdOrderByUpdatedAtDesc(Long repoId);

    /** [ADMIN] All repo reviews across the system. */
    List<RepoReview> findAllByOrderByCreatedAtDesc();

    /** Average rating for a repo. Returns null if no reviews exist. */
    @Query("SELECT AVG(r.rating) FROM RepoReview r WHERE r.repo.id = :repoId")
    Double averageRatingByRepoId(Long repoId);

    /** Delete all reviews for one repo (cascade cleanup). */
    void deleteByRepoId(Long repoId);

    /** Delete all reviews for MULTIPLE repos (batch cleanup). */
    void deleteByRepoIdIn(List<Long> repoIds);

    @Query("SELECT r.repo.id, COUNT(r), COALESCE(AVG(r.rating), 0.0) FROM RepoReview r WHERE r.repo.id IN :repoIds GROUP BY r.repo.id")
    List<Object[]> countAndAverageByRepoIds(@Param("repoIds") List<Long> repoIds);
}
