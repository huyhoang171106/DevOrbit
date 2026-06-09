package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

public record FolderIngestionSummary(
    int totalFiles,
    int completed,
    int skipped,
    int failed,
    List<String> courseCodes
) {}
