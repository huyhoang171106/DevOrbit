package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing the result of ingesting a single knowledge source.
 * Returned per-file after ingestion processing.
 *
 * @param sourceId     Unique ID of the created/updated knowledge source.
 * @param courseCode   Course code associated with the source (e.g. "CS106").
 * @param status       Processing status: "SUCCESS", "WARNING", or "FAILED".
 * @param warnings     List of non-fatal warning messages during ingestion.
 * @param errorMessage Fatal error message if ingestion failed.
 */
public record IngestionReport(
    UUID sourceId,
    String courseCode,
    String status,
    List<String> warnings,
    String errorMessage
) {}
