package vn.edu.uit.devorbit_api.dto.community;

public record RepoVoteResponse(
        Long repoId,
        Long studentId,
        int voteValue,
        int voteScore
) {
}
