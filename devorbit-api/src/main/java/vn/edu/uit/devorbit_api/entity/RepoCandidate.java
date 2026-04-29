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

    @Enumerated(EnumType.STRING)
    private RepoCandidateStatus status;
}
