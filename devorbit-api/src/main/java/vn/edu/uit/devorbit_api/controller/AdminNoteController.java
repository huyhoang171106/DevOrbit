package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.NoteResponse;
import vn.edu.uit.devorbit_api.service.AdminNoteService;

import java.util.List;

/**
 * ADMIN NOTE CONTROLLER = manage student notes (admin view).
 *
 * Admins can view, search, and delete student notes.
 * Note approval/moderation may be added in the future.
 *
 * All endpoints require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/notes")
@RequiredArgsConstructor
public class AdminNoteController {

    private final AdminNoteService adminNoteService;

    /** List ALL notes (from all students) */
    @GetMapping
    public List<NoteResponse> list() {
        return adminNoteService.getAll();
    }

    /** Delete a note */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminNoteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
