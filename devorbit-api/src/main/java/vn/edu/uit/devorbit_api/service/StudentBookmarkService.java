package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentBookmarkResponse;
import vn.edu.uit.devorbit_api.entity.StudentBookmark;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.StudentBookmarkRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentBookmarkService {

    private final StudentBookmarkRepository bookmarkRepository;
    private final StudentUserRepository studentUserRepository;

    public List<StudentBookmarkResponse> getBookmarks(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        return bookmarkRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudentBookmarkResponse addBookmark(String studentCode, StudentBookmarkRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (bookmarkRepository.existsByStudentIdAndTargetTypeAndTargetId(
                student.getId(), request.targetType(), request.targetId())) {
            throw new BadRequestException("Bookmark already exists");
        }

        // Validate target type
        String type = request.targetType().toUpperCase();
        if (!"COURSE".equals(type) && !"REPO".equals(type)) {
            throw new BadRequestException("targetType must be COURSE or REPO");
        }

        StudentBookmark bookmark = StudentBookmark.builder()
                .student(student)
                .targetType(type)
                .targetId(request.targetId())
                .title(request.title())
                .subtitle(request.subtitle())
                .url(request.url())
                .createdAt(LocalDateTime.now())
                .build();

        bookmark = bookmarkRepository.save(bookmark);
        return toResponse(bookmark);
    }

    @Transactional
    public void removeBookmark(String studentCode, Long bookmarkId) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentBookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new NotFoundException("Bookmark not found"));

        if (!bookmark.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("Bookmark does not belong to this student");
        }

        bookmarkRepository.delete(bookmark);
    }

    private StudentBookmarkResponse toResponse(StudentBookmark bookmark) {
        return new StudentBookmarkResponse(
                bookmark.getId(),
                bookmark.getTargetType(),
                bookmark.getTargetId(),
                bookmark.getTitle(),
                bookmark.getSubtitle(),
                bookmark.getUrl(),
                bookmark.getCreatedAt() != null ? bookmark.getCreatedAt().toString() : null
        );
    }
}
