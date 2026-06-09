package vn.edu.uit.devorbit_api.dto.knowledge;

import java.time.LocalDateTime;
import java.util.UUID;

public record KnowledgeSourceResponse(
    UUID id,
    String sourceType,
    String fileName,
    String status,
    String contentHash,
    String errorMessage,
    LocalDateTime updatedAt
) {}
