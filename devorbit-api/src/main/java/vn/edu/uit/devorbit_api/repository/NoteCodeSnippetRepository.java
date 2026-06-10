package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.NoteCodeSnippet;
import java.util.List;

@Repository
public interface NoteCodeSnippetRepository extends JpaRepository<NoteCodeSnippet, Long> {
    List<NoteCodeSnippet> findByNoteIdOrderBySortOrderAsc(Long noteId);

    void deleteByNoteId(Long noteId);
}
