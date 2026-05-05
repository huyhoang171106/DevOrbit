package vn.edu.uit.devorbit_api.dto.admin;

public record NoteCodeSnippetResponse(
    Long id,
    Long noteId,
    String language,
    String code,
    String caption,
    int sortOrder
) {}
