package vn.edu.uit.devorbit_api.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "github_repos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_name", nullable = false, length = 255)
    private String repoName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "github_url", nullable = false, length = 255)
    private String githubUrl;

    @Column(name = "tech_stack", length = 255)
    private String techStack;

    @Column(name = "subject_id", length = 20)
    private String subjectId;

    private int stars;
}
