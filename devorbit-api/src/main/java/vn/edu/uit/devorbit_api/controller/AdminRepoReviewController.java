package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.RepoReviewAdminResponse;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.RepoReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for managing repository (GitHub) reviews.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/reviews/repos - List all repo reviews</li>
 *   <li>DELETE /api/admin/reviews/repos/{id} - Delete a repo review</li>
 * </ul>
 * <p>
 * Security: ADMIN role required. Read-only by default; delete is explicitly transactional.
 */
@RestController
@RequestMapping("/api/admin/reviews/repos")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRepoReviewController {

    private final RepoReviewRepository repoReviewRepo;

    /**
     * List all repository reviews ordered by creation date descending.
     * Student/repo names fall back to "Deleted" if the related entity is missing.
     *
     * @return 200 OK with list of RepoReviewAdminResponse
     * @apiNote GET /api/admin/reviews/repos
     */
    @GetMapping
    public ResponseEntity<List<RepoReviewAdminResponse>> listRepoReviews() {
        return ResponseEntity.ok(repoReviewRepo.findAllByOrderByCreatedAtDesc()
            .stream().map(r -> RepoReviewAdminResponse.builder()
                .id(r.getId())
                .studentName(r.getStudent() != null ? r.getStudent().getFullName() : "Deleted")
                .repoName(r.getRepo() != null ? r.getRepo().getRepoName() : "Deleted")
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build())
            .collect(Collectors.toList()));
    }

    /**
     * Permanently delete a repo review by ID.
     *
     * @param id Long ID of the repo review to delete
     * @return 204 No Content if successful
     * @throws NotFoundException if review not found
     * @apiNote DELETE /api/admin/reviews/repos/{id}
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteRepoReview(@PathVariable Long id) {
        if (!repoReviewRepo.existsById(id)) {
            throw new NotFoundException("Repo review not found");
        }
        repoReviewRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
