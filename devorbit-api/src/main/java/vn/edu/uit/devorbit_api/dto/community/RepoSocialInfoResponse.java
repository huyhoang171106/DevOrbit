package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

public record RepoSocialInfoResponse(
        Long repoId,
        int voteScore,
        double averageRating,
        List<ReviewResponse> reviews
) {
}
