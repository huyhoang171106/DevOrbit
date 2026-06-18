package vn.edu.uit.devorbit_api.dto.community;

/**
 * Response DTO for a single review on a course or repository.
 * Used in review-related community endpoints.
 *
 * @param id         Unique review ID.
 * @param targetId   ID of the entity being reviewed (course or repo).
 * @param studentId  ID of the student who wrote the review.
 * @param studentName Display name of the reviewer.
 * @param rating     Rating score (e.g. 1-5).
 * @param comment    Optional written review text.
 * @param createdAt  Timestamp when the review was created (ISO-8601).
 * @param updatedAt  Timestamp when the review was last updated (ISO-8601).
 */
public record ReviewResponse(
        Long id,
        Long targetId,
        Long studentId,
        String studentName,
        Integer rating,
        String comment,
        String createdAt,
        String updatedAt
) {
}
