package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;
import java.util.UUID;

/**
 * DTO for the AI Q&A response.
 */
public record SubjectQaResponse(
    String answer,
    UUID sessionId,
    List<Long> relevantNodeIds,
    List<String> sources,
    String type
) {}
