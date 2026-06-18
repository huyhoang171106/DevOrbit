package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * REPO VOTE = a student's vote on a GitHub repository.
 *
 * Maps to the "repo_votes" table.
 * Students can upvote/downvote repos to show which ones are useful.
 *
 * voteValue determines the direction:
 *   1   = upvote (useful, recommend)
 *   -1  = downvote (not useful)
 *   0   = neutral (optional)
 *
 * Unique constraint (repo_id + student_id): each student can only
 * vote ONCE per repo. Changing their vote updates the existing row.
 *
 * Compare with RepoReview (rating + comment) — vote is simpler,
 * just a yes/no signal. RepoReview has a detailed comment.
 */
@Entity
@Table(
        name = "repo_votes",
        uniqueConstraints = @UniqueConstraint(name = "uk_repo_vote_student", columnNames = {"repo_id", "student_id"})
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepoVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The repo being voted on. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private GithubRepo repo;

    /** The student who voted. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /**
     * Vote value: 1 = upvote, -1 = downvote.
     * The total score for a repo = SUM(voteValue) across all votes.
     */
    @NotNull
    @Column(name = "vote_value", nullable = false)
    private Integer voteValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
