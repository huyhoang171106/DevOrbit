package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.service.RepoCandidateService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/repo-candidates")
@RequiredArgsConstructor
public class AdminRepoCandidateController {
    private final RepoCandidateService repoCandidateService;

    @GetMapping
    public List<RepoCandidateResponse> getPendingCandidates() {
        return repoCandidateService.getPendingCandidates();
    }

    @PostMapping("/{candidateId}/approve")
    public RepoCandidateResponse approve(
        @PathVariable Long candidateId,
        @RequestBody CandidateReviewRequest request
    ) {
        return repoCandidateService.approveCandidate(candidateId, request);
    }

    @PostMapping("/{candidateId}/reject")
    public RepoCandidateResponse reject(@PathVariable Long candidateId) {
        return repoCandidateService.rejectCandidate(candidateId);
    }
}
