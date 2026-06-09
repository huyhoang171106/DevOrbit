package vn.edu.uit.devorbit_api.dto.knowledge;

public record WebImportRequest(
    String url,
    String courseCode,
    String trustLevel,
    boolean embedAfterImport
) {}
