package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;

/**
 * PUBLIC DISCOVERY CONTROLLER = explore recent repos and popular tech stacks.
 *
 * Used by the "Discover" page on the frontend to show:
 * - Recently added GitHub repos
 * - Most popular tech stacks
 *
 * No authentication required.
 */
@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class PublicDiscoveryController {

    private final GithubRepoRepository repoRepository;
    private final TechStackRepository techStackRepository;
    private final GithubRepoService githubRepoService;

    /** Get the 10 most recently added active repos */
    @GetMapping("/recent-repos")
    public List<RepoSummaryResponse> getRecentRepos() {
        return repoRepository.findTop10ByActiveTrueOrderByIdDesc().stream()
                .map(repo -> new RepoSummaryResponse(
                        repo.getId(),
                        repo.getDisplayName(),
                        repo.getDescription(),
                        repo.getGithubUrl(),
                        repo.getPrimaryLanguage(),
                        repo.getStars() != null ? repo.getStars() : 0,
                        repo.getTechStacks().stream()
                                .map(ts -> new TechStackResponse(ts.getName()))
                                .toList(),
                        repo.getCourse() != null ? repo.getCourse().getId() : null,
                        repo.getCourse() != null ? repo.getCourse().getMaMH() : null,
                        repo.getCourse() != null ? repo.getCourse().getTenMH() : null,
                        repo.getReadmeExcerpt(),
                        repo.getFileTree(),
                        repo.getHasReadme(),
                        repo.getLastPushedAt(),
                        null,
                        null))
                .toList();
    }

    /** Get all approved repos for cross-course search */
    @GetMapping("/repos")
    public List<RepoSummaryResponse> getAllRepos() {
        return githubRepoService.getAllApprovedRepos();
    }

    /** Get the 10 most used tech stacks */
    @GetMapping("/top-stacks")
    public List<String> getTopStacks() {
        return techStackRepository.findTop10TechStacksByUsage();
    }
}
