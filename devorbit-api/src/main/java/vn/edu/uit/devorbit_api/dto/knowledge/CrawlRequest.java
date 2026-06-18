package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Request DTO for crawling a website to extract and import knowledge.
 * Used by POST /api/knowledge/crawl.
 *
 * @param url              The starting URL to crawl (e.g. "https://example.com/course")
 * @param courseCode       Course code to associate imported content with (e.g. "CS106").
 * @param trustLevel       Trust level for content (e.g. "low", "medium", "high").
 * @param maxPages         Maximum number of pages to crawl.
 * @param embedAfterImport If true, automatically create embeddings after crawling completes.
 */
public record CrawlRequest(
    String url,
    String courseCode,
    String trustLevel,
    int maxPages,
    boolean embedAfterImport
) {}
