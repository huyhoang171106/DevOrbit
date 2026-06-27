package vn.edu.uit.devorbit_api.dto.community;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for sending a new chat message in a channel.
 * Used by POST /api/community/channels/{channelId}/messages.
 *
 * @param content The text of the message. At most 1000 characters. Null for image-only messages.
 * @param imageUrl Public URL of the uploaded image. Null for text-only messages.
 */
public record ChatMessageRequest(
        @Size(max = 1000)
        String content,
        String imageUrl
) {
}
