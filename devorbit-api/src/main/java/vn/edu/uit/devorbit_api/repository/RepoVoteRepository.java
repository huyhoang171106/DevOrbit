package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoVote;

import java.util.Optional;

@Repository
public interface RepoVoteRepository extends JpaRepository<RepoVote, Long> {
    Optional<RepoVote> findByRepoIdAndStudentId(Long repoId, Long studentId);

    @Query("SELECT COALESCE(SUM(v.voteValue), 0) FROM RepoVote v WHERE v.repo.id = :repoId")
    Integer sumVoteValueByRepoId(Long repoId);
}
