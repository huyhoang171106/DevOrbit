package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.TutorialRequest;
import vn.edu.uit.devorbit_api.dto.admin.TutorialResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseTutorial;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.CourseTutorialRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTutorialService {
    private final CourseTutorialRepository repository;
    private final CourseRepository courseRepository;

    public List<TutorialResponse> getByCourse(Long courseId) {
        return repository.findByCourseIdOrderByCreatedAtDesc(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TutorialResponse create(Long courseId, TutorialRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        CourseTutorial entity = CourseTutorial.builder()
                .course(course)
                .title(request.title())
                .url(request.url())
                .type(request.type() != null ? request.type() : "article")
                .description(request.description())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public TutorialResponse update(Long id, TutorialRequest request) {
        CourseTutorial entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tutorial not found: " + id));
        entity.setTitle(request.title());
        entity.setUrl(request.url());
        entity.setType(request.type() != null ? request.type() : "article");
        entity.setDescription(request.description());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        CourseTutorial entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tutorial not found: " + id));
        repository.delete(entity);
    }

    private TutorialResponse toResponse(CourseTutorial entity) {
        return new TutorialResponse(
                entity.getId(), entity.getCourse().getId(),
                entity.getTitle(), entity.getUrl(),
                entity.getType(), entity.getDescription(),
                entity.getCreatedAt());
    }
}
