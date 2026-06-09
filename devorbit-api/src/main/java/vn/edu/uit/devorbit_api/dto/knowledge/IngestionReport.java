package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;
import java.util.UUID;

public record IngestionReport(
    UUID sourceId,
    String courseCode,
    String status,
    List<String> warnings,
    String errorMessage
) {}
