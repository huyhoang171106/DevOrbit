package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.NoteTargetType;
import java.time.LocalDateTime;

public record NoteResponse(
    Long id,
    Long studentId,
    String studentCode,
    String studentName,
    String title,
    String contentMarkdown,
    NoteTargetType targetType,
    Long targetId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    java.util.List<NoteCodeSnippetResponse> snippets
) {}
