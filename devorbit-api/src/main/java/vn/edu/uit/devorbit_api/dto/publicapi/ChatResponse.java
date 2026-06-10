package vn.edu.uit.devorbit_api.dto.publicapi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for AI Chat endpoint.
 */
public record ChatResponse(
    /**
     * Session ID for this conversation.
     */
    UUID sessionId,

    /**
     * AI response message.
     */
    String message,

    /**
     * Source citations (URLs, course codes, etc.).
     */
    List<String> sources,

    /**
     * Timestamp of the response.
     */
    LocalDateTime createdAt
) {}
