package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.community.RepoSocialInfoResponse;
import vn.edu.uit.devorbit_api.dto.community.ReviewSummaryResponse;
import vn.edu.uit.devorbit_api.service.SocialService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicSocialController {

    private final SocialService socialService;

    @GetMapping("/repos/{repoId}/social-info")
    public RepoSocialInfoResponse getRepoSocialInfo(@PathVariable Long repoId) {
        return socialService.getRepoSocialInfo(repoId);
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ReviewSummaryResponse getCourseReviews(@PathVariable Long courseId) {
        return socialService.getCourseReviews(courseId);
    }
}
