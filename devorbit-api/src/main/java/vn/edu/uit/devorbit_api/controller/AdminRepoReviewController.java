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

@RestController
@RequestMapping("/api/admin/reviews/repos")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRepoReviewController {

    private final RepoReviewRepository repoReviewRepo;

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
