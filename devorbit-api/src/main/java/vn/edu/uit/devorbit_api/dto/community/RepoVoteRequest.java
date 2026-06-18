package vn.edu.uit.devorbit_api.dto.community;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for casting a vote on a repository.
 * Used by POST /api/community/repos/{repoId}/vote.
 * Vote values: 1 = upvote, -1 = downvote, 0 = remove vote.
 *
 * @param voteValue Vote direction. Must be -1, 0, or 1.
 *                  Example: 1 to upvote, -1 to downvote.
 */
public record RepoVoteRequest(
        @NotNull
        @Min(-1)
        @Max(1)
        Integer voteValue
) {
}
