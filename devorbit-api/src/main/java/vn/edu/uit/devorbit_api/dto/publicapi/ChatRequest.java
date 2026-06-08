package vn.edu.uit.devorbit_api.dto.publicapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for AI Chat endpoint.
 */
public record ChatRequest(
    /**
     * Session ID for continuing a conversation.
     * Null for new session.
     */
    UUID sessionId,

    /**
     * User message (required).
     */
    @NotBlank
    @Size(max = 2000)
    String message
) {}
