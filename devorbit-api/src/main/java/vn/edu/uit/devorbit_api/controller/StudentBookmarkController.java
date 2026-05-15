package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkResponse;
import vn.edu.uit.devorbit_api.service.StudentBookmarkService;

import java.util.List;

@RestController
@RequestMapping("/api/student/bookmarks")
@RequiredArgsConstructor
public class StudentBookmarkController {

    private final StudentBookmarkService studentBookmarkService;

    @GetMapping
    public List<StudentBookmarkResponse> getBookmarks(@AuthenticationPrincipal String studentCode) {
        return studentBookmarkService.getBookmarks(studentCode);
    }

    @PostMapping
    public StudentBookmarkResponse addBookmark(
            @AuthenticationPrincipal String studentCode,
            @RequestBody @Valid StudentBookmarkRequest request) {
        return studentBookmarkService.addBookmark(studentCode, request);
    }

    @DeleteMapping("/{id}")
    public void removeBookmark(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long id) {
        studentBookmarkService.removeBookmark(studentCode, id);
    }
}
