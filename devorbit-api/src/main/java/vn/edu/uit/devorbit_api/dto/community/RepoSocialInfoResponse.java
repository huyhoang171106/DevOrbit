package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

/**
 * Response DTO aggregating social information for a repository.
 * Returned by GET /api/community/repos/{repoId}/social.
 * Combines vote score, average rating, and all reviews.
 *
 * @param repoId        ID of the repository.
 * @param voteScore     Net vote score (upvotes - downvotes).
 * @param averageRating Average star rating from all reviews.
 * @param reviews       Full list of {@link ReviewResponse} for this repo.
 */
public record RepoSocialInfoResponse(
        Long repoId,
        int voteScore,
        double averageRating,
        List<ReviewResponse> reviews
) {
}
