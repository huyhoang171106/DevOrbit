package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * Response DTO summarizing the result of ingesting an entire folder of documents.
 * Used by folder ingestion endpoints (POST /api/knowledge/ingest/folder).
 *
 * @param totalFiles  Total number of files found in the folder.
 * @param completed   Number of files successfully processed.
 * @param skipped     Number of files skipped (e.g. unsupported format, already imported).
 * @param failed      Number of files that failed to process.
 * @param courseCodes List of unique course codes detected across all processed files.
 */
public record FolderIngestionSummary(
    int totalFiles,
    int completed,
    int skipped,
    int failed,
    List<String> courseCodes
) {}
