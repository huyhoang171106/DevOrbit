package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Response for embedding operation.
 */
public record EmbedResponse(
    String status,
    int chunksEmbedded,
    int totalChunks,
    String message
) {}