package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Debug request DTO to preview RAG (Retrieval-Augmented Generation) retrieval + prompt construction.
 * Used by POST /api/knowledge/rag-preview.
 * This does NOT invoke the LLM; it only shows what chunks would be retrieved and the prompt that
 * would be sent. Useful for debugging retrieval quality.
 *
 * @param courseCode Course code to scope the search (e.g. "CS106").
 * @param query      The user's question or query string.
 * @param topK       Number of top chunks to retrieve (defaults to 5, clamped if <= 0).
 */
public record RagPreviewRequest(
    String courseCode,
    String query,
    int topK
) {
    public RagPreviewRequest {
        if (topK <= 0) topK = 5;
    }
}
