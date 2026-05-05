package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipRequest;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseRelationship;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRelationshipRepository;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRelationshipService {
    private final CourseRelationshipRepository repository;
    private final CourseRepository courseRepository;

    public List<CourseRelationshipResponse> getByCourse(Long courseId) {
        return repository.findByCourseIdOrRelatedCourseIdOrderByCreatedAtAsc(courseId, courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CourseRelationshipResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CourseRelationshipResponse create(CourseRelationshipRequest request) {
        if (request.courseId().equals(request.relatedCourseId())) {
            throw new BadRequestException("A course cannot relate to itself");
        }
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + request.courseId()));
        Course related = courseRepository.findById(request.relatedCourseId())
                .orElseThrow(() -> new NotFoundException("Related course not found: " + request.relatedCourseId()));
        repository.findByCourseIdAndRelatedCourseIdAndRelationType(
                request.courseId(), request.relatedCourseId(), request.relationType())
                .ifPresent(r -> { throw new BadRequestException("Relationship already exists"); });
        CourseRelationship entity = CourseRelationship.builder()
                .course(course)
                .relatedCourse(related)
                .relationType(request.relationType())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        CourseRelationship entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Relationship not found: " + id));
        repository.delete(entity);
    }

    private CourseRelationshipResponse toResponse(CourseRelationship entity) {
        return new CourseRelationshipResponse(
                entity.getId(),
                entity.getCourse().getId(), entity.getCourse().getMaMH(), entity.getCourse().getTenMH(),
                entity.getRelatedCourse().getId(), entity.getRelatedCourse().getMaMH(), entity.getRelatedCourse().getTenMH(),
                entity.getRelationType(), entity.getCreatedAt());
    }
}
