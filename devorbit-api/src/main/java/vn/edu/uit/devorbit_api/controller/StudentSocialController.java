package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.community.RepoVoteRequest;
import vn.edu.uit.devorbit_api.dto.community.RepoVoteResponse;
import vn.edu.uit.devorbit_api.dto.community.ReviewRequest;
import vn.edu.uit.devorbit_api.dto.community.ReviewResponse;
import vn.edu.uit.devorbit_api.service.SocialService;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentSocialController {

    private final SocialService socialService;

    @PostMapping("/repos/{repoId}/review")
    public ReviewResponse upsertRepoReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId,
            @RequestBody @Valid ReviewRequest request) {
        return socialService.upsertRepoReview(studentCode, repoId, request);
    }

    @DeleteMapping("/repos/{repoId}/review")
    public void deleteRepoReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId) {
        socialService.deleteRepoReview(studentCode, repoId);
    }

    @PostMapping("/repos/{repoId}/vote")
    public RepoVoteResponse voteRepo(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId,
            @RequestBody @Valid RepoVoteRequest request) {
        return socialService.voteRepo(studentCode, repoId, request);
    }

    @PostMapping("/courses/{courseId}/review")
    public ReviewResponse upsertCourseReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long courseId,
            @RequestBody @Valid ReviewRequest request) {
        return socialService.upsertCourseReview(studentCode, courseId, request);
    }

    @DeleteMapping("/courses/{courseId}/review")
    public void deleteCourseReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long courseId) {
        socialService.deleteCourseReview(studentCode, courseId);
    }
}
