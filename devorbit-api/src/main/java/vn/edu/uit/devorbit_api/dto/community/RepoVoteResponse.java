package vn.edu.uit.devorbit_api.dto.community;

/**
 * Response DTO after a vote is cast on a repository.
 * Returned by POST /api/community/repos/{repoId}/vote.
 *
 * @param repoId    ID of the repository that was voted on.
 * @param studentId ID of the student who cast the vote.
 * @param voteValue The vote value that was applied (1, -1, or 0).
 * @param voteScore Updated total vote score for the repository.
 */
public record RepoVoteResponse(
        Long repoId,
        Long studentId,
        int voteValue,
        int voteScore
) {
}
