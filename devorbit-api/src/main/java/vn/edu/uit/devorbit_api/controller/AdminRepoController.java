package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.ApprovedRepoUpdateRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;
import vn.edu.uit.devorbit_api.service.GithubRepoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/repos")
@RequiredArgsConstructor
public class AdminRepoController {
    private final GithubRepoService githubRepoService;
    private final GithubRepoRepository githubRepoRepository;
    private final RepoCandidateRepository repoCandidateRepository;

    @GetMapping
    public List<RepoSummaryResponse> getAllApprovedRepos() {
        return githubRepoService.getAllApprovedRepos();
    }

    @PutMapping("/{repoId}")
    public RepoSummaryResponse updateRepo(
        @PathVariable Long repoId,
        @RequestBody ApprovedRepoUpdateRequest request
    ) {
        return githubRepoService.updateApprovedRepo(repoId, request);
    }

    @DeleteMapping("/{repoId}")
    public void deleteRepo(@PathVariable Long repoId) {
        githubRepoService.deleteApprovedRepo(repoId);
    }

    @GetMapping("/consistency")
    public Map<String, Object> checkConsistency() {
        long approvedCandidates = repoCandidateRepository.countByStatus(RepoCandidateStatus.APPROVED);
        long activeRepos = githubRepoRepository.countByActiveTrue();
        return Map.of(
            "approvedCandidates", approvedCandidates,
            "activeRepos", activeRepos,
            "consistent", approvedCandidates == activeRepos
        );
    }
}
