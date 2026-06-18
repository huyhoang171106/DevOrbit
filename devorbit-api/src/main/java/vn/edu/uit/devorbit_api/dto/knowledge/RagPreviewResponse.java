package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * Debug response DTO showing retrieved chunks and the constructed LLM prompt.
 * Returned by POST /api/knowledge/rag-preview.
 *
 * @param courseCode       Course code used for filtering knowledge chunks.
 * @param query            The original user query.
 * @param topK             Number of chunks retrieved.
 * @param retrievedChunks  List of relevant chunks found by semantic search.
 * @param constructedPrompt The full prompt that would be sent to the LLM, including context.
 */
public record RagPreviewResponse(
    String courseCode,
    String query,
    int topK,
    List<SearchResponse.SearchResult> retrievedChunks,
    String constructedPrompt
) {}
