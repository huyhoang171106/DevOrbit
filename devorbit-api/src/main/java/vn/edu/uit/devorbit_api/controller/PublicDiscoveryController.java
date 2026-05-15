package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class PublicDiscoveryController {

    private final GithubRepoRepository repoRepository;
    private final TechStackRepository techStackRepository;

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
                        repo.getCourse() != null ? repo.getCourse().getTenMH() : null))
                .toList();
    }

    @GetMapping("/top-stacks")
    public List<String> getTopStacks() {
        return techStackRepository.findTop10TechStacksByUsage();
    }
}
