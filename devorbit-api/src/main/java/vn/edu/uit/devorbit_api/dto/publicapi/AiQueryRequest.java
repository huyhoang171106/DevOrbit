package vn.edu.uit.devorbit_api.dto.publicapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AI QUERY REQUEST = a question to the AI assistant.
 *
 * POST /api/ai/query
 *
 * @NotBlank → query must not be empty
 * @Size(max = 500) → limits input length to prevent abuse
 *   and keep processing time reasonable.
 */
public record AiQueryRequest(
    String query
) {}
