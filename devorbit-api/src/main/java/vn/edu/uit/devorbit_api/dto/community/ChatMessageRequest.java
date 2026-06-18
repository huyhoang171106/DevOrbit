package vn.edu.uit.devorbit_api.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for sending a new chat message in a channel.
 * Used by POST /api/community/channels/{channelId}/messages.
 *
 * @param content The text of the message. Must not be blank and at most 1000 characters.
 *                Example: "Has anyone started the homework for week 4?"
 */
public record ChatMessageRequest(
        @NotBlank
        @Size(max = 1000)
        String content
) {
}
