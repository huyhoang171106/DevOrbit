package vn.edu.uit.devorbit_api.dto.publicapi;

/**
 * SSE event payload for the streaming Q&A endpoint.
 * Each event has a type that determines which field is populated.
 */
public record SubjectQaStreamEvent(
    String type,
    String stage,
    String message,
    String content,
    WebSearchResponse.WebSearchResult searchResult,
    SubjectQaResponse response
) {
    public static SubjectQaStreamEvent status(String stage, String message) {
        return new SubjectQaStreamEvent("status", stage, message, null, null, null);
    }

    public static SubjectQaStreamEvent searchResult(WebSearchResponse.WebSearchResult result) {
        return new SubjectQaStreamEvent("search_result", null, null, null, result, null);
    }

    public static SubjectQaStreamEvent delta(String content) {
        return new SubjectQaStreamEvent("delta", null, null, content, null, null);
    }

    public static SubjectQaStreamEvent complete(SubjectQaResponse response) {
        return new SubjectQaStreamEvent("complete", "done", "Hoàn tất", null, null, response);
    }

    public static SubjectQaStreamEvent error(String message) {
        return new SubjectQaStreamEvent("error", "error", message, null, null, null);
    }
}
