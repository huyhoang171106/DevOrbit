package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.NoteCodeSnippetResponse;
import vn.edu.uit.devorbit_api.dto.admin.NoteResponse;
import vn.edu.uit.devorbit_api.entity.Note;
import vn.edu.uit.devorbit_api.entity.NoteCodeSnippet;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.NoteCodeSnippetRepository;
import vn.edu.uit.devorbit_api.repository.NoteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoteService {
    private final NoteRepository noteRepo;
    private final NoteCodeSnippetRepository snippetRepo;

    // ================ NOTE CRUD ================

    @Transactional(readOnly = true)
    public List<NoteResponse> getAll() {
        return noteRepo.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toNoteResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Note entity = noteRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found: " + id));
        snippetRepo.deleteByNoteId(id);
        noteRepo.delete(entity);
    }

    // ================ HELPERS ================

    private NoteResponse toNoteResponse(Note entity) {
        List<NoteCodeSnippetResponse> snippets = snippetRepo
                .findByNoteIdOrderBySortOrderAsc(entity.getId()).stream()
                .map(this::toSnippetResponse)
                .toList();
        return new NoteResponse(
                entity.getId(),
                entity.getStudent() != null ? entity.getStudent().getId() : null,
                entity.getStudent() != null ? entity.getStudent().getStudentCode() : "Deleted",
                entity.getStudent() != null ? entity.getStudent().getFullName() : "Deleted",
                entity.getTitle(), entity.getContentMarkdown(),
                entity.getTargetType(), entity.getTargetId(),
                entity.getCreatedAt(), entity.getUpdatedAt(),
                snippets);
    }

    private NoteCodeSnippetResponse toSnippetResponse(NoteCodeSnippet entity) {
        return new NoteCodeSnippetResponse(
                entity.getId(), entity.getNote().getId(),
                entity.getLanguage(), entity.getCode(),
                entity.getCaption(), entity.getSortOrder());
    }
}
