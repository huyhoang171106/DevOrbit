package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.YoutubePlaylistRequest;
import vn.edu.uit.devorbit_api.dto.admin.YoutubePlaylistResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseYoutubePlaylist;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.CourseYoutubePlaylistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseYoutubePlaylistService {
    private final CourseYoutubePlaylistRepository repository;
    private final CourseRepository courseRepository;

    public List<YoutubePlaylistResponse> getByCourse(Long courseId) {
        return repository.findByCourseIdOrderByCreatedAtDesc(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public YoutubePlaylistResponse create(Long courseId, YoutubePlaylistRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        CourseYoutubePlaylist entity = CourseYoutubePlaylist.builder()
                .course(course)
                .title(request.title())
                .url(request.url())
                .description(request.description())
                .channelName(request.channelName())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public YoutubePlaylistResponse update(Long id, YoutubePlaylistRequest request) {
        CourseYoutubePlaylist entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Youtube playlist not found: " + id));
        entity.setTitle(request.title());
        entity.setUrl(request.url());
        entity.setDescription(request.description());
        entity.setChannelName(request.channelName());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        CourseYoutubePlaylist entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Youtube playlist not found: " + id));
        repository.delete(entity);
    }

    private YoutubePlaylistResponse toResponse(CourseYoutubePlaylist entity) {
        return new YoutubePlaylistResponse(
                entity.getId(), entity.getCourse().getId(),
                entity.getTitle(), entity.getUrl(),
                entity.getDescription(), entity.getChannelName(),
                entity.getCreatedAt());
    }
}
