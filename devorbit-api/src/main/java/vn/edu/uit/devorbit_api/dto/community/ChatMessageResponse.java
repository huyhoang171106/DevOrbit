package vn.edu.uit.devorbit_api.dto.community;

public record ChatMessageResponse(
        Long id,
        Long channelId,
        Long studentId,
        String senderName,
        String senderAvatar,
        String content,
        String createdAt
) {
}
