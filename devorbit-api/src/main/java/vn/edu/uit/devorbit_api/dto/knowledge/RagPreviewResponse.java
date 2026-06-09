package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * Debug response showing retrieved chunks and constructed prompt.
 */
public record RagPreviewResponse(
    String courseCode,
    String query,
    int topK,
    List<SearchResponse.SearchResult> retrievedChunks,
    String constructedPrompt
) {}
