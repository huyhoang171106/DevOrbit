package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Response DTO for an embedding operation result.
 * Returned after calling embedding endpoints (e.g. POST /api/knowledge/embed).
 *
 * @param status         Operation status (e.g. "SUCCESS", "PARTIAL", "FAILED").
 * @param chunksEmbedded Number of text chunks successfully embedded.
 * @param totalChunks    Total number of chunks processed.
 * @param message        Human-readable status message with details.
 */
public record EmbedResponse(
    String status,
    int chunksEmbedded,
    int totalChunks,
    String message
) {}