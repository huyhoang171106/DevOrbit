package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStatsResponse {
    private long totalStudents;
    private long totalCourses;
    private long totalRepos;
    private long pendingCandidates;
    private List<StudentSummary> recentStudents;
    private List<ReviewSummary> recentCourseReviews;
    private List<SubmissionSummary> recentSubmissions;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudentSummary {
        private Long id;
        private String fullName;
        private String studentCode;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ReviewSummary {
        private Long id;
        private String studentName;
        private String courseName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubmissionSummary {
        private Long id;
        private String githubUrl;
        private String courseName;
        private String status;
    }

    private List<RepoStatsEntry> topFavoritedRepos;
    private List<RepoStatsEntry> topViewedRepos;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RepoStatsEntry {
        private Long repoId;
        private String repoName;
        private String githubUrl;
        private String courseName;
        private String primaryLanguage;
        private long count;
        private long bookmarkCount;
        private double averageRating;
        private long reviewCount;
        private long voteScore;
        private long viewCount;
        private double popularityScore;
    }
}
