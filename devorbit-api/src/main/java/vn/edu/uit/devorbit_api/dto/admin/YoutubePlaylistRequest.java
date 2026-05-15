package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record YoutubePlaylistRequest(
    @NotBlank String title,
    @NotBlank String url,
    String description,
    String channelName
) {}
