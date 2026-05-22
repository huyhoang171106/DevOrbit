package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;

/**
 * PUBLIC REPO CONTROLLER = endpoints for browsing GitHub repos.
 *
 * Only shows APPROVED repos (active = true).
 * No authentication required.
 *
 * Note: class-level @RequestMapping is "/api", individual methods
 * define the full path. This is slightly unusual — most controllers
 * put the shared prefix at the class level.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicRepoController {
    private final GithubRepoService githubRepoService;

    /**
     * GET /api/repos/{repoId}
     * Returns details of ONE approved repo.
     */
    @GetMapping("/repos/{repoId}")
    public RepoSummaryResponse getRepoById(@PathVariable Long repoId) {
        return githubRepoService.getApprovedRepoById(repoId);
    }

    /**
     * GET /api/courses/{courseId}/repos
     * Returns ALL approved repos for a course.
     * Optionally filter by techStack query parameter.
     *
     * Example: GET /api/courses/1/repos?techStack=React
     */
    @GetMapping("/courses/{courseId}/repos")
    public List<RepoSummaryResponse> getReposByCourse(
        @PathVariable Long courseId,
        @RequestParam(required = false) String techStack
    ) {
        if (techStack != null && !techStack.isBlank()) {
            return githubRepoService.getApprovedReposByCourseAndTechStack(courseId, techStack);
        }
        return githubRepoService.getApprovedReposByCourse(courseId);
    }
}
