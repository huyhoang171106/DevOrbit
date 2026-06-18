package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * Response DTO for the AI tutor endpoint.
 * Returned by POST /api/knowledge/tutor/ask.
 * Contains the generated answer, supporting citations, and confidence level.
 *
 * @param answer     The AI-generated answer to the student's question.
 * @param citations  List of {@link Citation} objects referencing source materials.
 * @param confidence Confidence level of the answer: "HIGH", "MEDIUM", or "LOW".
 */
public record TutorResponse(
    String answer,
    List<Citation> citations,
    String confidence
) {}
