package vn.edu.uit.devorbit_api.dto.community;

import vn.edu.uit.devorbit_api.entity.ChatChannelType;

public record ChatChannelResponse(
        Long id,
        String channelId,
        String name,
        ChatChannelType type,
        String referenceId
) {
}
