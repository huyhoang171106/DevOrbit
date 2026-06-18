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

/**
 * Student controller for social features (reviews, votes).
 * <p>
 * Endpoints:
 * <ul>
 *   <li>POST /api/student/repos/{repoId}/review - Upsert a repo review</li>
 *   <li>DELETE /api/student/repos/{repoId}/review - Delete a repo review</li>
 *   <li>POST /api/student/repos/{repoId}/vote - Vote on a repo</li>
 *   <li>POST /api/student/courses/{courseId}/review - Upsert a course review</li>
 *   <li>DELETE /api/student/courses/{courseId}/review - Delete a course review</li>
 * </ul>
 * <p>
 * Security: Authenticated student (identified via @AuthenticationPrincipal studentCode).
 * Review endpoints use upsert behavior: if a review by this student for the target already exists,
 * it is updated; otherwise a new review is created. Delegates to SocialService.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentSocialController {

    private final SocialService socialService;

    /**
     * Create or update a review for a repository (upsert).
     * <p>
     * If the authenticated student already has a review for this repo, it is updated.
     * Otherwise a new review is created.
     *
     * @param studentCode Authenticated student code (from security context)
     * @param repoId      Long ID of the repository being reviewed
     * @param request     ReviewRequest containing rating and comment
     * @return ReviewResponse with the saved/updated review
     * @apiNote POST /api/student/repos/{repoId}/review
     */
    @PostMapping("/repos/{repoId}/review")
    public ReviewResponse upsertRepoReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId,
            @RequestBody @Valid ReviewRequest request) {
        return socialService.upsertRepoReview(studentCode, repoId, request);
    }

    /**
     * Delete the authenticated student's review for a repository.
     *
     * @param studentCode Authenticated student code
     * @param repoId      Long ID of the repository
     * @apiNote DELETE /api/student/repos/{repoId}/review
     */
    @DeleteMapping("/repos/{repoId}/review")
    public void deleteRepoReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId) {
        socialService.deleteRepoReview(studentCode, repoId);
    }

    /**
     * Cast or update a vote on a repository.
     * <p>
     * Supports upvote/downvote. If the student already voted, the vote is updated.
     *
     * @param studentCode Authenticated student code
     * @param repoId      Long ID of the repository being voted on
     * @param request     RepoVoteRequest containing vote type (UP/DOWN)
     * @return RepoVoteResponse with updated vote info
     * @apiNote POST /api/student/repos/{repoId}/vote
     */
    @PostMapping("/repos/{repoId}/vote")
    public RepoVoteResponse voteRepo(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long repoId,
            @RequestBody @Valid RepoVoteRequest request) {
        return socialService.voteRepo(studentCode, repoId, request);
    }

    /**
     * Create or update a review for a course (upsert).
     * <p>
     * If the authenticated student already has a review for this course, it is updated.
     * Otherwise a new review is created.
     *
     * @param studentCode Authenticated student code
     * @param courseId    Long ID of the course being reviewed
     * @param request     ReviewRequest containing rating and comment
     * @return ReviewResponse with the saved/updated review
     * @apiNote POST /api/student/courses/{courseId}/review
     */
    @PostMapping("/courses/{courseId}/review")
    public ReviewResponse upsertCourseReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long courseId,
            @RequestBody @Valid ReviewRequest request) {
        return socialService.upsertCourseReview(studentCode, courseId, request);
    }

    /**
     * Delete the authenticated student's review for a course.
     *
     * @param studentCode Authenticated student code
     * @param courseId    Long ID of the course
     * @apiNote DELETE /api/student/courses/{courseId}/review
     */
    @DeleteMapping("/courses/{courseId}/review")
    public void deleteCourseReview(
            @AuthenticationPrincipal String studentCode,
            @PathVariable Long courseId) {
        socialService.deleteCourseReview(studentCode, courseId);
    }
}
