package vn.edu.uit.devorbit_api.dto.admin;

import java.time.LocalDateTime;

public record YoutubePlaylistResponse(
    Long id,
    Long courseId,
    String title,
    String url,
    String description,
    String channelName,
    LocalDateTime createdAt
) {}
