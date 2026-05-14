package vn.edu.uit.devorbit_api.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record PhotoboothFrameDTO(
    String frameId,
    String name,
    String displayName,
    Integer photoCount,
    String description,
    JsonNode slots,
    String overlayImageUrl,
    String filter,
    String backgroundColor
) {}
