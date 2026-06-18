package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

/**
 * Response DTO for the online presence status of a channel.
 * Returned by WebSocket presence events or GET endpoints showing who is online in a channel.
 *
 * @param channelId ID of the channel.
 * @param members   List of online members currently present in this channel.
 */
public record ChannelPresenceResponse(
        Long channelId,
        List<OnlineMemberResponse> members
) {
}
