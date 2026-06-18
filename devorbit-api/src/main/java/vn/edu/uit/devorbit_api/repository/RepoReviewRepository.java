package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoReviewRepository extends JpaRepository<RepoReview, Long> {
    Optional<RepoReview> findByRepoIdAndStudentId(Long repoId, Long studentId);
    List<RepoReview> findByRepoIdOrderByUpdatedAtDesc(Long repoId);
    List<RepoReview> findAllByOrderByCreatedAtDesc();

    @Query("SELECT AVG(r.rating) FROM RepoReview r WHERE r.repo.id = :repoId")
    Double averageRatingByRepoId(Long repoId);

    void deleteByRepoId(Long repoId);

    void deleteByRepoIdIn(List<Long> repoIds);

    @Query("SELECT r.repo.id, COUNT(r), COALESCE(AVG(r.rating), 0.0) FROM RepoReview r WHERE r.repo.id IN :repoIds GROUP BY r.repo.id")
    List<Object[]> countAndAverageByRepoIds(@Param("repoIds") List<Long> repoIds);
}
