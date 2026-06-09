package vn.edu.uit.devorbit_api.dto.knowledge;

public record CrawlRequest(
    String url,
    String courseCode,
    String trustLevel,
    int maxPages,
    boolean embedAfterImport
) {}
