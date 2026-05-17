package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.NoteCodeSnippetResponse;
import vn.edu.uit.devorbit_api.dto.admin.NoteResponse;
import vn.edu.uit.devorbit_api.service.AdminNoteService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notes")
@RequiredArgsConstructor
public class AdminNoteController {

    private final AdminNoteService adminNoteService;

    @GetMapping
    public List<NoteResponse> list() {
        return adminNoteService.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponse detail(@PathVariable Long id) {
        return adminNoteService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminNoteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{noteId}/snippets")
    public List<NoteCodeSnippetResponse> getSnippets(@PathVariable Long noteId) {
        return adminNoteService.getSnippets(noteId);
    }
}
