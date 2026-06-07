package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

public record ReviewSummaryResponse(
        Long targetId,
        double averageRating,
        List<ReviewResponse> reviews
) {
}
