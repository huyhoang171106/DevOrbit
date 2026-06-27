package vn.edu.uit.devorbit_api.dto.community;

/**
 * Response DTO for a single chat message.
 * Returned by GET /api/community/channels/{channelId}/messages and after sending a message.
 *
 * @param id          Unique message ID.
 * @param channelId   ID of the channel this message belongs to.
 * @param studentId   ID of the student who sent the message.
 * @param senderName  Display name of the sender.
 * @param senderAvatar URL or path to the sender's avatar image.
 * @param content     The message text content.
 * @param imageUrl    Public URL of the image (null for text-only messages).
 * @param createdAt   Timestamp when the message was sent (ISO-8601 format).
 * @param deleted     Whether this message has been deleted (soft-delete).
 */
public record ChatMessageResponse(
        Long id,
        Long channelId,
        Long studentId,
        String senderName,
        String senderAvatar,
        String content,
        String imageUrl,
        String createdAt,
        boolean deleted
) {
}
