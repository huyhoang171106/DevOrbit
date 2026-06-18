package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dashboard statistics response for the admin overview page.
 *
 * <p>This is the main payload returned by the admin stats endpoint.
 * It provides a bird's-eye view of the platform: aggregate counts
 * (students, courses, repos, pending candidates) plus recent-activity
 * lists so the dashboard can render a "latest activity" feed without
 * additional API calls.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/stats}</p>
 *
 * <p><b>Flow:</b><ol>
 *   <li>The admin dashboard loads and calls the stats endpoint.</li>
 *   <li>{@link vn.edu.uit.devorbit_api.controller.AdminStatsController}
 *       queries various repositories and assembles this response.</li>
 *   <li>The frontend renders summary cards (totals) and recent-activity
 *       tables from the nested lists.</li>
 * </ol></p>
 *
 * <p><b>Nested types:</b><ul>
 *   <li>{@link StudentSummary} — lightweight student info for the "recent
 *       students" feed.</li>
 *   <li>{@link ReviewSummary} — course review snippet for the "recent
 *       reviews" feed.</li>
 *   <li>{@link SubmissionSummary} — repository/github submission entry
 *       for the "recent submissions" feed.</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    /** Total number of registered students on the platform. */
    private long totalStudents;

    /** Total number of courses in the system (active + inactive). */
    private long totalCourses;

    /** Total number of approved GitHub repositories. */
    private long totalRepos;

    /** Number of repo candidates still awaiting review (status = PENDING). */
    private long pendingCandidates;

    /** Most recently registered students (typically last 10). */
    private List<StudentSummary> recentStudents;

    /** Most recent course reviews submitted by students (typically last 10). */
    private List<ReviewSummary> recentCourseReviews;

    /** Most recent GitHub submission activity (typically last 10 entries). */
    private List<SubmissionSummary> recentSubmissions;

    /**
     * Lightweight student summary used in the "recent students" dashboard widget.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentSummary {
        /** Student's internal primary key. */
        private Long id;

        /** Student's full display name (e.g. "Nguyen Van A"). */
        private String fullName;

        /** University student code (e.g. "21520101"). */
        private String studentCode;
    }

    /**
     * Course review snippet used in the "recent reviews" dashboard widget.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummary {
        /** Review's internal primary key. */
        private Long id;

        /** Display name of the student who wrote the review. */
        private String studentName;

        /** Name of the course being reviewed (e.g. "Cau truc du lieu & Giai thuat"). */
        private String courseName;

        /** Star rating (1-5). */
        private Integer rating;

        /** Free-text review comment (may be {@code null} or empty). */
        private String comment;

        /** Timestamp when the review was submitted. */
        private LocalDateTime createdAt;
    }

    /**
     * GitHub submission entry used in the "recent submissions" dashboard widget.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionSummary {
        /** Submission's internal primary key. */
        private Long id;

        /** URL of the submitted GitHub repository. */
        private String githubUrl;

        /** Name of the course the submission is for. */
        private String courseName;

        /**
         * Submission status (e.g. {@code "PENDING"}, {@code "APPROVED"},
         * {@code "REJECTED"}, {@code "GRADED"}).
         */
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
