package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.CourseTutorialService;
import vn.edu.uit.devorbit_api.service.CourseYoutubePlaylistService;
import vn.edu.uit.devorbit_api.service.CourseArticleService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class PublicCourseController {
    private final CourseService courseService;
    private final CourseTutorialService tutorialService;
    private final CourseYoutubePlaylistService playlistService;
    private final CourseArticleService articleService;

    @GetMapping
    public List<CourseSummaryResponse> getCourses() {
        return courseService.getActiveCourseSummaries();
    }

    @GetMapping("/{id}")
    public CourseDetailResponse getCourseDetail(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }

    @GetMapping("/{id}/tutorials")
    public List<?> getTutorials(@PathVariable Long id) {
        return tutorialService.getByCourse(id);
    }

    @GetMapping("/{id}/videos")
    public List<?> getVideos(@PathVariable Long id) {
        return playlistService.getByCourse(id);
    }

    @GetMapping("/{id}/articles")
    public List<?> getArticles(@PathVariable Long id) {
        return articleService.getByCourse(id);
    }
}
