package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoVote;

import java.util.Optional;

@Repository
public interface RepoVoteRepository extends JpaRepository<RepoVote, Long> {
    Optional<RepoVote> findByRepoIdAndStudentId(Long repoId, Long studentId);
}
