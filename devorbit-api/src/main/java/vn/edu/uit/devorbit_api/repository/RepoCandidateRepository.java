package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;

import java.util.List;

@Repository
public interface RepoCandidateRepository extends JpaRepository<RepoCandidate, Long> {
    List<RepoCandidate> findByStatus(RepoCandidateStatus status);
}
