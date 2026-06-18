package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.NoteTargetType;
import java.time.LocalDateTime;

/**
 * Response DTO for a student note, as seen by the admin.
 *
 * <p>Students can create personal notes linked to specific courses or
 * resources (identified by {@code targetType} and {@code targetId}).
 * This DTO exposes the full note content ({@code contentMarkdown}) plus
 * metadata and any embedded code snippets. The admin can review all
 * notes across all students for monitoring or support purposes.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/notes}
 * — list all notes from all students.</p>
 *
 * <p><b>Note:</b> The corresponding delete endpoint
 * ({@code DELETE /api/admin/notes/{id}}) does not return this DTO;
 * it returns {@code 204 No Content}.</p>
 *
 * @param id              Internal primary key of the note.
 * @param studentId       ID of the student who owns this note.
 * @param studentCode     University student code of the note owner
 *                        (e.g. {@code "21520101"}).
 * @param studentName     Display name of the note owner (e.g. {@code "Nguyen Van A"}).
 * @param title           Note title (may be {@code null} if the student
 *                        didn't provide one).
 * @param contentMarkdown The note body in Markdown format. Preserves the
 *                        student's formatting (headings, lists, code blocks).
 * @param targetType      The type of resource this note is attached to
 *                        (e.g. {@code COURSE}, {@code ARTICLE}, {@code TUTORIAL}).
 *                        See {@link NoteTargetType} for allowed values.
 * @param targetId        The ID of the specific resource this note belongs to.
 * @param createdAt       Timestamp when the note was first created.
 * @param updatedAt       Timestamp when the note was last edited
 *                        (same as {@code createdAt} if never edited).
 * @param snippets        List of code snippets embedded in this note
 *                        (may be empty). Each snippet is represented by
 *                        {@link NoteCodeSnippetResponse}.
 */
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
