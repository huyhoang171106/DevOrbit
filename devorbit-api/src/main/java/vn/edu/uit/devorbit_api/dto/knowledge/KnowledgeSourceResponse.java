package vn.edu.uit.devorbit_api.dto.knowledge;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a knowledge source's metadata.
 * Returned by GET /api/knowledge/sources and similar listing endpoints.
 *
 * @param id           Unique UUID of the knowledge source.
 * @param sourceType   Type of source: "FILE", "URL", "CRAWL", etc.
 * @param fileName     Original file name or URL title.
 * @param status       Processing status: "PENDING", "PROCESSING", "COMPLETED", "FAILED".
 * @param contentHash  Hash of the source content (for deduplication).
 * @param errorMessage Error details if processing failed.
 * @param updatedAt    Timestamp of last update (LocalDateTime).
 */
public record KnowledgeSourceResponse(
    UUID id,
    String sourceType,
    String fileName,
    String status,
    String contentHash,
    String errorMessage,
    LocalDateTime updatedAt
) {}
