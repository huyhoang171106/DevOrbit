package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

/**
 * Response DTO aggregating all reviews for a single target (course or repo).
 * Returned by endpoints that fetch the review summary.
 *
 * @param targetId      ID of the reviewed entity.
 * @param averageRating Arithmetic mean of all ratings (e.g. 4.2).
 * @param reviews       Full list of individual {@link ReviewResponse} entries.
 */
public record ReviewSummaryResponse(
        Long targetId,
        double averageRating,
        List<ReviewResponse> reviews
) {
}
