package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

/**
 * WEB SEARCH RESPONSE = results from a web search (used by AI features).
 *
 * The AI searches the web for supplementary answers. Results ordered by position.
 * highlights = relevant text excerpts from the page.
 */
public record WebSearchResponse(
    String status,                  // "success", "error", "no_results"
    List<WebSearchResult> web
) {
    public record WebSearchResult(
        String url,
        String title,
        String description,
        int position,                   // Rank (1 = top result)
        List<String> highlights,        // Relevant excerpts
        String publishedDate,           // When published
        String author,                  // Content author
        String sourceProvider           // Search provider
    ) {
        public boolean hasHighlights() {
            return highlights != null && !highlights.isEmpty();
        }
    }
}
