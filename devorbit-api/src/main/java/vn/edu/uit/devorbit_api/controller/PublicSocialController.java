package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.community.RepoSocialInfoResponse;
import vn.edu.uit.devorbit_api.dto.community.ReviewSummaryResponse;
import vn.edu.uit.devorbit_api.service.SocialService;

/**
 * Public controller for social/community data (no authentication required).
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/repos/{repoId}/social-info - Get social info for a repo (reviews, votes)</li>
 *   <li>GET /api/courses/{courseId}/reviews - Get course reviews summary</li>
 * </ul>
 * <p>
 * These endpoints serve public-facing data used in cards, tooltips, and embedded widgets.
 * Delegates to SocialService for business logic.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicSocialController {

    private final SocialService socialService;

    /**
     * Get social information for a repository.
     * <p>
     * Returns aggregated data including review count, average rating,
     * and vote summary for the given repo.
     *
     * @param repoId Long ID of the repository
     * @return RepoSocialInfoResponse with social aggregation
     * @apiNote GET /api/repos/{repoId}/social-info
     */
    @GetMapping("/repos/{repoId}/social-info")
    public RepoSocialInfoResponse getRepoSocialInfo(@PathVariable Long repoId) {
        return socialService.getRepoSocialInfo(repoId);
    }

    /**
     * Get course reviews summary.
     * <p>
     * Returns aggregated review data (average rating, review count, recent reviews)
     * for the given course.
     *
     * @param courseId Long ID of the course
     * @return ReviewSummaryResponse with review aggregation
     * @apiNote GET /api/courses/{courseId}/reviews
     */
    @GetMapping("/courses/{courseId}/reviews")
    public ReviewSummaryResponse getCourseReviews(@PathVariable Long courseId) {
        return socialService.getCourseReviews(courseId);
    }
}
