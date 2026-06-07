package vn.edu.uit.devorbit_api.dto.community;

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
