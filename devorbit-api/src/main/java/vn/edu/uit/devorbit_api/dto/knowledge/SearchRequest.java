package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Request DTO for semantic search over knowledge chunks.
 * Used by POST /api/knowledge/search.
 * Returns chunks most semantically similar to the query text.
 *
 * @param courseCode Course code to scope the search (e.g. "CS106"). If null or empty, search across all courses.
 * @param query      The natural language query string (e.g. "What are the prerequisites for this course?").
 * @param topK       Number of results to return (default 5, max 20).
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