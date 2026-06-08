package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

/**
 * WebSearchResponse DTO representing the search results structure.
 */
public record WebSearchResponse(
    String status,
    List<WebSearchResult> web
) {
    public record WebSearchResult(
        String url,
        String title,
        String description,
        int position
    ) {}
}
