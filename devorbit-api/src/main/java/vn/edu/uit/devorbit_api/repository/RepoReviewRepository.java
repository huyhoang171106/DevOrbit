package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoReview;

import java.util.Optional;

@Repository
public interface RepoReviewRepository extends JpaRepository<RepoReview, Long> {
    Optional<RepoReview> findByRepoIdAndStudentId(Long repoId, Long studentId);
}
