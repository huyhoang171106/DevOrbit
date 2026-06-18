package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminStatsResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.repository.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsController {

    private final StudentUserRepository studentRepo;
    private final CourseRepository courseRepo;
    private final GithubRepoRepository githubRepoRepo;
    private final RepoCandidateRepository repoCandidateRepo;
    private final CourseReviewRepository courseReviewRepo;
    private final StudentBookmarkRepository studentBookmarkRepo;
    private final RepoReviewRepository repoReviewRepo;
    private final RepoVoteRepository repoVoteRepo;

    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats(
            @RequestParam(defaultValue = "bookmarks") String sortBy) {
        List<AdminStatsResponse.StudentSummary> recentStudents = studentRepo.findTop10ByOrderByIdDesc()
            .stream().map(s -> AdminStatsResponse.StudentSummary.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .studentCode(s.getStudentCode())
                .build())
            .collect(Collectors.toList());

        List<AdminStatsResponse.ReviewSummary> recentReviews = courseReviewRepo.findTop10ByOrderByCreatedAtDesc()
            .stream().map(r -> AdminStatsResponse.ReviewSummary.builder()
                .id(r.getId())
                .studentName(r.getStudent() != null ? r.getStudent().getFullName() : "Deleted")
                .courseName(r.getCourse() != null ? r.getCourse().getTenMH() : "Deleted")
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        List<AdminStatsResponse.SubmissionSummary> recentSubmissions = repoCandidateRepo.findTop10ByOrderByIdDesc()
            .stream().map(s -> AdminStatsResponse.SubmissionSummary.builder()
                .id(s.getId())
                .githubUrl(s.getGithubUrl())
                .courseName(s.getCourse() != null ? s.getCourse().getTenMH() : null)
                .status(s.getStatus().name())
                .build())
            .collect(Collectors.toList());

        List<AdminStatsResponse.RepoStatsEntry> topFavorited = buildTopFavoritedRepos(sortBy);
        List<AdminStatsResponse.RepoStatsEntry> topViewed = buildTopViewedRepos();

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache().mustRevalidate())
            .body(AdminStatsResponse.builder()
                .totalStudents(studentRepo.count())
                .totalCourses(courseRepo.count())
                .totalRepos(githubRepoRepo.countByActiveTrue())
                .pendingCandidates(repoCandidateRepo.countByStatus(RepoCandidateStatus.NEW))
                .recentStudents(recentStudents)
                .recentCourseReviews(recentReviews)
                .recentSubmissions(recentSubmissions)
                .topFavoritedRepos(topFavorited)
                .topViewedRepos(topViewed)
                .build());
    }

    private List<AdminStatsResponse.RepoStatsEntry> buildTopFavoritedRepos(String sortBy) {
        List<GithubRepo> allRepos = githubRepoRepo.findByActiveTrue();
        if (allRepos.isEmpty()) return List.of();

        Map<Long, Long> bookmarkMap = new java.util.HashMap<>();
        studentBookmarkRepo.findTopBookmarkedRepoIds(Pageable.unpaged())
            .forEach(r -> bookmarkMap.put((Long) r[0], (Long) r[1]));

        Map<Long, Double> ratingMap = new java.util.HashMap<>();
        Map<Long, Long> reviewCountMap = new java.util.HashMap<>();
        repoReviewRepo.avgRatingGroupByRepoId()
            .forEach(r -> {
                Long repoId = (Long) r[0];
                long sum = ((Number) r[1]).longValue();
                long count = ((Number) r[2]).longValue();
                ratingMap.put(repoId, count > 0 ? (double) sum / count : 0.0);
                reviewCountMap.put(repoId, count);
            });

        Map<Long, Long> upvoteMap = new java.util.HashMap<>();
        repoVoteRepo.upvoteCountGroupByRepoId()
            .forEach(r -> upvoteMap.put((Long) r[0], (Long) r[1]));

        List<AdminStatsResponse.RepoStatsEntry> entries = new ArrayList<>();
        for (GithubRepo repo : allRepos) {
            long bookmarks = bookmarkMap.getOrDefault(repo.getId(), 0L);
            double avgRating = ratingMap.getOrDefault(repo.getId(), 0.0);
            long reviewCount = reviewCountMap.getOrDefault(repo.getId(), 0L);
            long upvotes = upvoteMap.getOrDefault(repo.getId(), 0L);

            long metricValue = switch (sortBy) {
                case "bookmarks" -> bookmarks;
                case "rating" -> (long) (avgRating * 100);
                case "upvotes" -> upvotes;
                default -> bookmarks;
            };

            entries.add(AdminStatsResponse.RepoStatsEntry.builder()
                .repoId(repo.getId())
                .repoName(repo.getDisplayName() != null ? repo.getDisplayName() : repo.getRepoName())
                .githubUrl(repo.getGithubUrl())
                .courseName(repo.getCourse() != null ? repo.getCourse().getTenMH() : null)
                .primaryLanguage(repo.getPrimaryLanguage())
                .bookmarkCount(bookmarks)
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .voteScore(upvotes)
                .viewCount(repo.getViewCount() != null ? repo.getViewCount() : 0)
                .popularityScore(0)
                .count(metricValue)
                .build());
        }

        entries.sort(Comparator.comparingLong(AdminStatsResponse.RepoStatsEntry::getCount).reversed());
        return entries.stream().limit(100).toList();
    }

    private List<AdminStatsResponse.RepoStatsEntry> buildTopViewedRepos() {
        return githubRepoRepo.findTop100ByActiveTrueOrderByViewCountDesc().stream()
            .filter(r -> r.getViewCount() != null && r.getViewCount() > 0)
            .map(r -> AdminStatsResponse.RepoStatsEntry.builder()
                .repoId(r.getId())
                .repoName(r.getDisplayName() != null ? r.getDisplayName() : r.getRepoName())
                .githubUrl(r.getGithubUrl())
                .courseName(r.getCourse() != null ? r.getCourse().getTenMH() : null)
                .primaryLanguage(r.getPrimaryLanguage())
                .count(r.getViewCount())
                .viewCount(r.getViewCount())
                .bookmarkCount(0)
                .averageRating(0)
                .reviewCount(0)
                .voteScore(0)
                .popularityScore(0)
                .build())
            .toList();
    }
}
