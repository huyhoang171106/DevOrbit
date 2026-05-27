package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * REPO CANDIDATE = a GitHub repo discovered by scanning, waiting for admin review.
 *
 * Maps to the "repo_candidates" table.
 * The system scans GitHub for repos matching specific queries (see scanQuery).
 * Found repos become "candidates" that an admin must review and either:
 *   APPROVE — makes it visible as a GithubRepo on the platform
 *   REJECT  — discards it (won't appear anywhere)
 *
 * Once approved, a new GithubRepo entity is created from this candidate's data.
 */
@Entity
@Table(name = "repo_candidates")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepoCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The course this repo is being considered for */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /** The GitHub search query that found this repo */
    private String scanQuery;

    /** GitHub username/organization that owns the repo */
    private String githubOwner;

    /** The repo name on GitHub */
    private String githubName;

    /** Full GitHub URL */
    @Column(nullable = false)
    private String githubUrl;

    /** Repo description from GitHub */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Main programming language */
    @Column(length = 100)
    private String primaryLanguage;

    /** Comma-separated topics/tags from GitHub */
    @Column(columnDefinition = "TEXT")
    private String topics;

    /** Number of GitHub stars */
    private Integer stars;

    /** Number of GitHub forks */
    private Integer forks;

    /** ISO date of last push (e.g., "2024-12-01T10:30:00Z") */
    private String lastPushedAt;

    /** Short excerpt from the README */
    @Column(columnDefinition = "TEXT")
    private String readmeExcerpt;

    /** Current review status: NEW, APPROVED, or REJECTED */
    @Enumerated(EnumType.STRING)
    private RepoCandidateStatus status;

    /** Admin's note about this candidate (why approved/rejected) */
    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    /** Admin assigned to review this candidate */
    @Column(name = "assigned_reviewer", length = 50)
    private String assignedReviewer;
}
