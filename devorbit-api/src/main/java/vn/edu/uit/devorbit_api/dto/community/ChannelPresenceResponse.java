package vn.edu.uit.devorbit_api.dto.community;

import java.util.List;

public record ChannelPresenceResponse(
        Long channelId,
        List<OnlineMemberResponse> members
) {
}
