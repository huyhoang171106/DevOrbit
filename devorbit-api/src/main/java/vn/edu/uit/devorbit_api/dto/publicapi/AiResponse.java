package vn.edu.uit.devorbit_api.dto.publicapi;

/**
 * AI RESPONSE = a response from an AI-powered feature.
 *
 * Contains:
 * - content: the AI-generated text (summary, advice, roadmap)
 * - type: what kind of response (SUMMARY, TUTOR_ADVICE, ROADMAP)
 *
 * Used by:
 *   GET /api/ai/repo/{repoId}/summary
 *   GET /api/ai/repo/{repoId}/advice
 */
public record AiResponse(
    String content,
    String type  // "SUMMARY", "TUTOR_ADVICE", "ROADMAP"
) {}
