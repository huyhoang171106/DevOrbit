package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.UUID;

public record Citation(
    UUID sourceId,
    String fileName,
    String url,
    String sectionTitle,
    Integer pageFrom,
    Integer pageTo,
    Integer chunkIndex
) {}
