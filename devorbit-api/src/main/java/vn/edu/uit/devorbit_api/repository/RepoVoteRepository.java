package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RepoVote;

import java.util.List;
import java.util.Optional;

/**
 * REPO VOTE REPOSITORY = data access for student votes on GitHub repos.
 *
 * Votes are simpler than reviews — just a +1/-1 signal without comments.
 * The total score for a repo = SUM of all voteValue entries.
 *
 * Unique constraint (repo_id + student_id) means one vote per student per repo.
 */
@Repository
public interface RepoVoteRepository extends JpaRepository<RepoVote, Long> {

    /** Find a specific student's vote for a specific repo. */
    Optional<RepoVote> findByRepoIdAndStudentId(Long repoId, Long studentId);

    /**
     * Calculate the TOTAL vote score for a repo.
     * COALESCE handles the case where there are no votes (returns 0 instead of NULL).
     *
     * Example: 5 upvotes (+1 each) + 2 downvotes (-1 each) = total score of 3.
     *
     * @param repoId the repo's database ID
     * @return total vote sum (can be negative, zero, or positive)
     */
    @Query("SELECT COALESCE(SUM(v.voteValue), 0) FROM RepoVote v WHERE v.repo.id = :repoId")
    Integer sumVoteValueByRepoId(Long repoId);

    /** Delete all votes for one repo (cascade cleanup). */
    @Query("SELECT v.repo.id, COUNT(v) FROM RepoVote v WHERE v.voteValue > 0 GROUP BY v.repo.id")
    List<Object[]> upvoteCountGroupByRepoId();

    void deleteByRepoId(Long repoId);

    /** Delete all votes for MULTIPLE repos (batch cleanup). */
    void deleteByRepoIdIn(List<Long> repoIds);
    /** Get all votes by a specific student. */
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repo"})
    List<RepoVote> findByStudentId(Long studentId);
}
