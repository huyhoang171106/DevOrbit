package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkResponse;
import vn.edu.uit.devorbit_api.service.StudentBookmarkService;

import java.util.List;

/**
 * STUDENT BOOKMARK CONTROLLER = manage student's saved bookmarks.
 *
 * Students can bookmark courses, repos, articles, and other resources.
 * Each bookmark is tied to the student's account via JWT authentication.
 *
 * All endpoints require a valid student JWT token.
 * The student's identity is extracted from the token via @AuthenticationPrincipal.
 */
@RestController
@RequestMapping("/api/student/bookmarks")
@RequiredArgsConstructor
public class StudentBookmarkController {

    private final StudentBookmarkService studentBookmarkService;

    /** Get all bookmarks for the logged-in student */
    @GetMapping
    public List<StudentBookmarkResponse> getBookmarks(@AuthenticationPrincipal String studentCode) {
        return studentBookmarkService.getBookmarks(studentCode);
    }

    /** Add a new bookmark */
    @PostMapping
    public StudentBookmarkResponse addBookmark(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid StudentBookmarkRequest request) {
        return studentBookmarkService.addBookmark(studentCode, request);
    }

    /** Remove a bookmark by its ID */
    @DeleteMapping("/{id}")
    public void removeBookmark(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        studentBookmarkService.removeBookmark(studentCode, id);
    }
}
