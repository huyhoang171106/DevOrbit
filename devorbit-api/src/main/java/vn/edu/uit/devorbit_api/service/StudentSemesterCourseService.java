package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.SemesterCourseRequest;
import vn.edu.uit.devorbit_api.dto.student.SemesterCourseResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.StudentCourseSelection;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.StudentCourseSelectionRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentSemesterCourseService {

    private final StudentCourseSelectionRepository selectionRepository;
    private final StudentUserRepository studentUserRepository;
    private final CourseRepository courseRepository;

    @org.springframework.cache.annotation.Cacheable(value = "semesterCourses", key = "#studentCode")
    public List<SemesterCourseResponse> getSelections(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        return selectionRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "semesterCourses", key = "#studentCode")
    public SemesterCourseResponse addSelection(String studentCode, SemesterCourseRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (selectionRepository.existsByStudentIdAndCourseId(student.getId(), request.courseId())) {
            throw new BadRequestException("Course already selected");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + request.courseId()));

        StudentCourseSelection selection = StudentCourseSelection.builder()
                .student(student)
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();

        selection = selectionRepository.save(selection);
        return toResponse(selection);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "semesterCourses", key = "#studentCode")
    public void removeSelection(String studentCode, Long courseId) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentCourseSelection selection = selectionRepository
                .findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new NotFoundException("Course selection not found"));

        selectionRepository.delete(selection);
    }

    private SemesterCourseResponse toResponse(StudentCourseSelection selection) {
        return new SemesterCourseResponse(
                selection.getId(),
                selection.getCourse().getId(),
                selection.getCourse().getMaMH(),
                selection.getCourse().getTenMH(),
                selection.getCreatedAt() != null ? selection.getCreatedAt().toString() : null
        );
    }
}
