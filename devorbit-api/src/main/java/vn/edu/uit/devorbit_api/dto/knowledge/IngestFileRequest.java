package vn.edu.uit.devorbit_api.dto.knowledge;

/**
 * Request DTO for importing a single file into the knowledge base.
 * Used by POST /api/knowledge/ingest/file.
 *
 * @param filePath Absolute or relative path to the file on the server filesystem.
 *                 Example: "/data/syllabi/CS101.pdf"
 */
public record IngestFileRequest(String filePath) {}
