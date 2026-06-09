package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Debug request to preview RAG retrieval + prompt construction.
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
