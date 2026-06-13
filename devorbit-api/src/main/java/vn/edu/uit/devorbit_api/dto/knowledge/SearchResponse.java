package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * Response for semantic search over knowledge chunks.
 */
public record SearchResponse(
    String query,
    String courseCode,
    List<SearchResult> results
) {
    /**
     * A single search result with relevance score.
     */
    public record SearchResult(
        String chunkId,
        String sourceId,
        String courseCode,
        String sectionTitle,
        Integer pageFrom,
        Integer pageTo,
        double score,
        String text,
        String fileName,
        String url
    ) {}
}