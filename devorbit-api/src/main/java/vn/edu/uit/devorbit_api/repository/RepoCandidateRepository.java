package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepoCandidateRepository extends JpaRepository<RepoCandidate, Long> {
    @EntityGraph(attributePaths = {"course"})
    List<RepoCandidate> findByStatus(RepoCandidateStatus status);

    long countByStatus(RepoCandidateStatus status);

    @EntityGraph(attributePaths = {"course"})
    List<RepoCandidate> findByStatusAndAssignedReviewer(RepoCandidateStatus status, String assignedReviewer);

    Optional<RepoCandidate> findByGithubUrlAndCourseId(String githubUrl, Long courseId);

    long countByStatusAndAssignedReviewer(RepoCandidateStatus status, String assignedReviewer);

    long countByStatusInAndAssignedReviewer(List<RepoCandidateStatus> statuses, String assignedReviewer);

    long countByAssignedReviewer(String assignedReviewer);

    @Query("SELECT r.githubUrl FROM RepoCandidate r WHERE r.course.id = :courseId")
    List<String> findGithubUrlByCourseId(Long courseId);

    @Query("SELECT r.githubUrl FROM RepoCandidate r")
    List<String> findAllGithubUrls();
}
