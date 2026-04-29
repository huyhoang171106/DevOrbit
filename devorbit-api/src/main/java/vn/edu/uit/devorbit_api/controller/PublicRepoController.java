package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicRepoController {
    private final GithubRepoService githubRepoService;

    @GetMapping("/courses/{courseId}/repos")
    public List<RepoSummaryResponse> getReposByCourse(@PathVariable Long courseId) {
        return githubRepoService.getApprovedReposByCourse(courseId);
    }
}
