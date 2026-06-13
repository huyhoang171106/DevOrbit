package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
