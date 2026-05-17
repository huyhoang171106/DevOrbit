package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.*;
import vn.edu.uit.devorbit_api.service.CourseArticleService;
import vn.edu.uit.devorbit_api.service.CourseTutorialService;
import vn.edu.uit.devorbit_api.service.CourseYoutubePlaylistService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses/{courseId}/resources")
@RequiredArgsConstructor
public class AdminCourseResourceController {

    private final CourseYoutubePlaylistService youtubePlaylistService;
    private final CourseArticleService articleService;
    private final CourseTutorialService tutorialService;

    @GetMapping("/youtube-playlists")
    public List<YoutubePlaylistResponse> getYoutubePlaylists(@PathVariable Long courseId) {
        return youtubePlaylistService.getByCourse(courseId);
    }

    @PostMapping("/youtube-playlists")
    public YoutubePlaylistResponse createYoutubePlaylist(
            @PathVariable Long courseId,
            @RequestBody @Valid YoutubePlaylistRequest request) {
        return youtubePlaylistService.create(courseId, request);
    }

    @PutMapping("/youtube-playlists/{id}")
    public YoutubePlaylistResponse updateYoutubePlaylist(
            @PathVariable Long id,
            @RequestBody @Valid YoutubePlaylistRequest request) {
        return youtubePlaylistService.update(id, request);
    }

    @DeleteMapping("/youtube-playlists/{id}")
    public ResponseEntity<Void> deleteYoutubePlaylist(@PathVariable Long id) {
        youtubePlaylistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/articles")
    public List<ArticleResponse> getArticles(@PathVariable Long courseId) {
        return articleService.getByCourse(courseId);
    }

    @PostMapping("/articles")
    public ArticleResponse createArticle(
            @PathVariable Long courseId,
            @RequestBody @Valid ArticleRequest request) {
        return articleService.create(courseId, request);
    }

    @PutMapping("/articles/{id}")
    public ArticleResponse updateArticle(
            @PathVariable Long id,
            @RequestBody @Valid ArticleRequest request) {
        return articleService.update(id, request);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tutorials")
    public List<TutorialResponse> getTutorials(@PathVariable Long courseId) {
        return tutorialService.getByCourse(courseId);
    }

    @PostMapping("/tutorials")
    public TutorialResponse createTutorial(
            @PathVariable Long courseId,
            @RequestBody @Valid TutorialRequest request) {
        return tutorialService.create(courseId, request);
    }

    @PutMapping("/tutorials/{id}")
    public TutorialResponse updateTutorial(
            @PathVariable Long id,
            @RequestBody @Valid TutorialRequest request) {
        return tutorialService.update(id, request);
    }

    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<Void> deleteTutorial(@PathVariable Long id) {
        tutorialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
