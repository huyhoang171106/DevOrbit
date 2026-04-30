package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.BookmarkRequest;
import vn.edu.uit.devorbit_api.dto.student.BookmarkResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.StudentBookmarkRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentBookmarkService {
    private final StudentUserRepository studentUserRepository;
    private final StudentBookmarkRepository bookmarkRepository;
    private final CourseRepository courseRepository;
    private final GithubRepoRepository githubRepoRepository;

    @Transactional(readOnly = true)
    public List<BookmarkResponse> list(String studentCode) {
        StudentUser student = requireStudent(studentCode);
        return bookmarkRepository.findByStudentOrderByCreatedAtDesc(student).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookmarkResponse save(String studentCode, BookmarkRequest request) {
        StudentUser student = requireStudent(studentCode);
        validateTarget(request.targetType(), request.targetId());
        StudentBookmark bookmark = bookmarkRepository
                .findByStudentAndTargetTypeAndTargetId(student, request.targetType(), request.targetId())
                .orElseGet(() -> bookmarkRepository.save(StudentBookmark.builder()
                        .student(student)
                        .targetType(request.targetType())
                        .targetId(request.targetId())
                        .build()));
        return toResponse(bookmark);
    }

    @Transactional
    public void delete(String studentCode, BookmarkTargetType targetType, Long targetId) {
        StudentUser student = requireStudent(studentCode);
        bookmarkRepository.deleteByStudentAndTargetTypeAndTargetId(student, targetType, targetId);
    }

    private StudentUser requireStudent(String studentCode) {
        return studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
    }

    private void validateTarget(BookmarkTargetType targetType, Long targetId) {
        if (targetType == BookmarkTargetType.COURSE && !courseRepository.existsById(targetId)) {
            throw new NotFoundException("Course not found");
        }
        if (targetType == BookmarkTargetType.REPO && !githubRepoRepository.existsById(targetId)) {
            throw new NotFoundException("Repository not found");
        }
    }

    private BookmarkResponse toResponse(StudentBookmark bookmark) {
        if (bookmark.getTargetType() == BookmarkTargetType.COURSE) {
            Course course = courseRepository.findById(bookmark.getTargetId())
                    .orElseThrow(() -> new NotFoundException("Bookmarked course not found"));
            return new BookmarkResponse(bookmark.getId(), bookmark.getTargetType(), bookmark.getTargetId(),
                    course.getTenMH(), course.getMaMH(), "/courses/" + course.getId(), bookmark.getCreatedAt());
        }
        GithubRepo repo = githubRepoRepository.findById(bookmark.getTargetId())
                .orElseThrow(() -> new NotFoundException("Bookmarked repository not found"));
        String title = repo.getDisplayName() != null && !repo.getDisplayName().isBlank()
                ? repo.getDisplayName()
                : repo.getRepoName();
        return new BookmarkResponse(bookmark.getId(), bookmark.getTargetType(), bookmark.getTargetId(),
                title, repo.getPrimaryLanguage(), repo.getGithubUrl(), bookmark.getCreatedAt());
    }
}
