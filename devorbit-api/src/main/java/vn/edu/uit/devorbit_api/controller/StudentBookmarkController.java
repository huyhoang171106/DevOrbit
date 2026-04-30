package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.BookmarkRequest;
import vn.edu.uit.devorbit_api.dto.student.BookmarkResponse;
import vn.edu.uit.devorbit_api.entity.BookmarkTargetType;
import vn.edu.uit.devorbit_api.service.StudentBookmarkService;

import java.util.List;

@RestController
@RequestMapping("/api/student/bookmarks")
@RequiredArgsConstructor
public class StudentBookmarkController {
    private final StudentBookmarkService bookmarkService;

    @GetMapping
    public List<BookmarkResponse> list(@AuthenticationPrincipal String studentCode) {
        return bookmarkService.list(studentCode);
    }

    @PostMapping
    public BookmarkResponse save(@AuthenticationPrincipal String studentCode, @RequestBody @Valid BookmarkRequest request) {
        return bookmarkService.save(studentCode, request);
    }

    @DeleteMapping("/{targetType}/{targetId}")
    public void delete(
            @AuthenticationPrincipal String studentCode,
            @PathVariable BookmarkTargetType targetType,
            @PathVariable Long targetId
    ) {
        bookmarkService.delete(studentCode, targetType, targetId);
    }
}
