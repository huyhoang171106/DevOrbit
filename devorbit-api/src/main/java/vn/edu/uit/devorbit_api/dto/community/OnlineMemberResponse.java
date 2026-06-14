package vn.edu.uit.devorbit_api.dto.community;

public record OnlineMemberResponse(
        Long studentId,
        String studentCode,
        String displayName
) {
}
