package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Request for semantic search over knowledge chunks.
 */
public record SearchRequest(
    String courseCode,
    String query,
    int topK
) {
    public SearchRequest {
        if (topK <= 0) topK = 5;
        if (topK > 20) topK = 20;
    }
}