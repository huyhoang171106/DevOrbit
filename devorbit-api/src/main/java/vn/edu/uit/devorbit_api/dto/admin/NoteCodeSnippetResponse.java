package vn.edu.uit.devorbit_api.dto.admin;

/**
 * Response DTO for a code snippet embedded within a student note.
 *
 * <p>Students can include code snippets in their notes with a specific
 * programming language annotation. This DTO represents one such snippet,
 * including its position ({@code sortOrder}), language, the code content,
 * and an optional caption. Snippets are always part of a parent
 * {@link NoteResponse}.</p>
 *
 * <p><b>Used by:</b> Nested within {@link NoteResponse#snippets} —
 * returned as part of {@code GET /api/admin/notes}.</p>
 *
 * @param id        Internal primary key of the snippet.
 * @param noteId    ID of the parent note this snippet belongs to.
 * @param language  Programming language identifier for syntax highlighting
 *                  (e.g. {@code "java"}, {@code "python"}, {@code "javascript"},
 *                  {@code "sql"}). May be {@code null} if no language specified.
 * @param code      The actual source code content of the snippet.
 *                  Preserves indentation and line breaks.
 * @param caption   Optional short label or description for the snippet
 *                  (e.g. {@code "Main controller class"}). May be {@code null}.
 * @param sortOrder Zero-based position of this snippet within the note's
 *                  snippet list. Lower values appear first.
 */
public record NoteCodeSnippetResponse(
    Long id,
    Long noteId,
    String language,
    String code,
    String caption,
    int sortOrder
) {}
