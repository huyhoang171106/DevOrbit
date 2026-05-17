package vn.edu.uit.devorbit_api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "primary_language", length = 100)
    private String primaryLanguage;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany
    @JoinTable(
            name = "repo_tech_stacks",
            joinColumns = @JoinColumn(name = "repo_id"),
            inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    @Builder.Default
    private Set<TechStack> techStacks = new LinkedHashSet<>();

    private Integer stars;
}
