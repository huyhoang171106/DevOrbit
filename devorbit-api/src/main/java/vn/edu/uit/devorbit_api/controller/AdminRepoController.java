package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.ApprovedRepoUpdateRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.GithubRepoService;
import vn.edu.uit.devorbit_api.service.ai.RepoEvaluationService;

import java.util.List;

/**
 * ADMIN REPO CONTROLLER = manage the approved GitHub repos.
 *
 * Once a RepoCandidate is APPROVED, it becomes a GithubRepo that
 * students can see. This controller handles CRUD on those approved repos.
 *
 * All endpoints require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/repos")
@RequiredArgsConstructor
public class AdminRepoController {
    private final GithubRepoService githubRepoService;
    private final RepoEvaluationService repoEvaluationService;

    /** List ALL approved repos */
    @GetMapping
    public List<RepoSummaryResponse> getAllApprovedRepos() {
        return githubRepoService.getAllApprovedRepos();
    }

    /** Update a repo's details */
    @PutMapping("/{repoId}")
    public RepoSummaryResponse updateRepo(
        @PathVariable Long repoId,
        @RequestBody ApprovedRepoUpdateRequest request
    ) {
        return githubRepoService.updateApprovedRepo(repoId, request);
    }

    /** Soft-delete a repo (sets active=false, doesn't actually remove it) */
    @DeleteMapping("/{repoId}")
    public void deleteRepo(@PathVariable Long repoId) {
        githubRepoService.deleteApprovedRepo(repoId);
    }

    /** Manually trigger rule-based evaluation of all repositories */
    @PostMapping("/evaluate-all")
    public void evaluateAll() {
        repoEvaluationService.evaluateAndSaveAll();
    }

    /** Re-sync metadata and re-evaluate a specific approved repo from GitHub */
    @PostMapping("/{repoId}/sync")
    public RepoSummaryResponse syncRepo(@PathVariable Long repoId) {
        return githubRepoService.syncApprovedRepo(repoId);
    }
}
