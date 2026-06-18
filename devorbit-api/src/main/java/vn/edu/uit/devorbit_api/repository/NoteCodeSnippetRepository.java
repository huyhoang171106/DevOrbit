package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.NoteCodeSnippet;
import java.util.List;

/**
 * NOTE CODE SNIPPET REPOSITORY = data access for code snippets inside student notes.
 *
 * Each snippet is a piece of code with a programming language tag (for syntax highlighting)
 * and an optional caption. Snippets are ordered by sortOrder within a note.
 */
@Repository
public interface NoteCodeSnippetRepository extends JpaRepository<NoteCodeSnippet, Long> {

    /** All snippets for a note, ordered by sortOrder (0 = first). */
    List<NoteCodeSnippet> findByNoteIdOrderBySortOrderAsc(Long noteId);

    /** Delete all snippets for a note (used when note is deleted). */
    void deleteByNoteId(Long noteId);
}
