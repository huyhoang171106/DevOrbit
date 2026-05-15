package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private String scanQuery;

    private String githubOwner;

    private String githubName;

    @Column(nullable = false)
    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String primaryLanguage;

    @Column(columnDefinition = "TEXT")
    private String topics;

    private Integer stars;

    private Integer forks;

    private String lastPushedAt;

    @Column(columnDefinition = "TEXT")
    private String readmeExcerpt;

    @Enumerated(EnumType.STRING)
    private RepoCandidateStatus status;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @Column(name = "assigned_reviewer", length = 50)
    private String assignedReviewer;
}
