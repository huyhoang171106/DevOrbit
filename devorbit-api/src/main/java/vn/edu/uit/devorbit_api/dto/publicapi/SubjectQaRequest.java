package vn.edu.uit.devorbit_api.dto.publicapi;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * DTO for incoming AI Q&A requests.
 */
public record SubjectQaRequest(
    @NotBlank String message,
    UUID sessionId
) {}
