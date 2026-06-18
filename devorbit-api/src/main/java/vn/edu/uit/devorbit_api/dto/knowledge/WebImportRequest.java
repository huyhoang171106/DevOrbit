package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Request DTO for importing a single web page (URL) into the knowledge base.
 * Used by POST /api/knowledge/import/web.
 * Unlike crawling, this imports just one specific page.
 *
 * @param url              The web page URL to import (e.g. "https://example.com/course/syllabus").
 * @param courseCode       Course code to associate the content with.
 * @param trustLevel       Trust level for content evaluation (e.g. "low", "medium", "high").
 * @param embedAfterImport If true, automatically create embeddings after import.
 */
public record WebImportRequest(
    String url,
    String courseCode,
    String trustLevel,
    boolean embedAfterImport
) {}
