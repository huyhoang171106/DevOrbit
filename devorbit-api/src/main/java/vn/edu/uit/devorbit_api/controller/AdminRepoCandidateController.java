package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.dto.admin.ReviewerStatsResponse;
import vn.edu.uit.devorbit_api.service.RepoCandidateService;

import java.util.List;

/**
 * ADMIN REPO CANDIDATE CONTROLLER = manage repos waiting for review.
 *
 * When the system scans GitHub, it finds "candidate" repos that admins
 * can APPROVE (make visible) or REJECT (discard).
 * This controller manages that review workflow.
 *
 * All endpoints require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/repo-candidates")
@RequiredArgsConstructor
public class AdminRepoCandidateController {
    private final RepoCandidateService repoCandidateService;

    /**
     * GET list of pending candidates.
     * Optionally filter by reviewer name.
     */
    @GetMapping
    public List<RepoCandidateResponse> getPendingCandidates(
        @RequestParam(required = false, defaultValue = "all") String reviewer
    ) {
        return repoCandidateService.getPendingCandidates(reviewer);
    }

    /** Get review statistics by reviewer */
    @GetMapping("/stats")
    public List<ReviewerStatsResponse> stats() {
        return repoCandidateService.getReviewerStats();
    }

    /** Approve a candidate — makes it visible as a GithubRepo */
    @PostMapping("/{candidateId}/approve")
    public RepoCandidateResponse approve(
        @PathVariable Long candidateId,
        @RequestBody CandidateReviewRequest request
    ) {
        return repoCandidateService.approveCandidate(candidateId, request);
    }

    /** Reject a candidate — won't appear anywhere */
    @PostMapping("/{candidateId}/reject")
    public RepoCandidateResponse reject(@PathVariable Long candidateId) {
        return repoCandidateService.rejectCandidate(candidateId);
    }

}
