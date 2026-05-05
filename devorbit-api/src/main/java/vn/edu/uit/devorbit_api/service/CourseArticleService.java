package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.ArticleRequest;
import vn.edu.uit.devorbit_api.dto.admin.ArticleResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseArticle;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseArticleRepository;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseArticleService {
    private final CourseArticleRepository repository;
    private final CourseRepository courseRepository;

    public List<ArticleResponse> getByCourse(Long courseId) {
        return repository.findByCourseIdOrderByCreatedAtDesc(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ArticleResponse create(Long courseId, ArticleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        CourseArticle entity = CourseArticle.builder()
                .course(course)
                .title(request.title())
                .url(request.url())
                .author(request.author())
                .description(request.description())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public ArticleResponse update(Long id, ArticleRequest request) {
        CourseArticle entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found: " + id));
        entity.setTitle(request.title());
        entity.setUrl(request.url());
        entity.setAuthor(request.author());
        entity.setDescription(request.description());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        CourseArticle entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found: " + id));
        repository.delete(entity);
    }

    private ArticleResponse toResponse(CourseArticle entity) {
        return new ArticleResponse(
                entity.getId(), entity.getCourse().getId(),
                entity.getTitle(), entity.getUrl(),
                entity.getAuthor(), entity.getDescription(),
                entity.getCreatedAt());
    }
}
