package vn.edu.uit.devorbit_api.dto.community;

import vn.edu.uit.devorbit_api.entity.ChatChannelType;

/**
 * Response DTO for a chat channel in the community.
 * Returned by GET /api/community/channels and similar endpoints.
 *
 * @param id          Internal database ID of the channel.
 * @param channelId   Public unique identifier (UUID string) for the channel.
 * @param name        Human-readable name of the channel (e.g. "CS106 Lab Group").
 * @param type        Channel type: e.g. COURSE_GROUP, PROJECT, GENERAL.
 * @param referenceId Optional ID of the referenced entity (e.g. course ID) this channel belongs to.
 */
public record ChatChannelResponse(
        Long id,
        String channelId,
        String name,
        ChatChannelType type,
        String referenceId
) {
}
