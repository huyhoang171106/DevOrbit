package vn.edu.uit.devorbit_api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * GITHUB REPO = a GitHub repository linked to a course.
 *
 * Maps to the "github_repos" table.
 * Each repo is tied to ONE course and represents a project that
 * students can study, fork, or use as reference.
 *
 * CONFUSING FIELD WARNING: techStack vs techStacks
 * --------------------------------------------------
 * - techStack  (singular) = OLD single-value field, being phased out
 * - techStacks (plural)   = CURRENT ManyToMany relationship to TechStack table
 *
 * A repo can have MULTIPLE tech stacks (e.g., "React", "Spring Boot", "Docker").
 * The old `techStack` was a single string — it's kept for backward compatibility
 * with existing database rows but NEW repos should use `techStacks`.
 */
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

    /** The name of the GitHub repo (e.g., "my-spring-boot-app") */
    @Column(name = "repo_name", nullable = false, length = 255)
    private String repoName;

    /** Description from the GitHub repo (README short description) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Full URL to the GitHub repo (e.g., https://github.com/user/repo) */
    @Column(name = "github_url", nullable = false, length = 255)
    private String githubUrl;

    /**
     * OLD single-value tech stack field (being phased out).
     * Prefer `techStacks` (ManyToMany) for new data.
     */
    @Column(name = "tech_stack", length = 255)
    private String techStack;

    /** The course code this repo is linked to (e.g., "SE101") */
    @Column(name = "subject_id", length = 20)
    private String subjectId;

    /** A friendly display name (may differ from the GitHub repo name) */
    @Column(name = "display_name", length = 255)
    private String displayName;

    /** The main programming language (e.g., "Java", "Python", "TypeScript") */
    @Column(name = "primary_language", length = 100)
    private String primaryLanguage;

    /** Is this repo visible/active? Inactive repos are hidden from the public API */
    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    /** The course this repo belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /**
     * CURRENT tech stack field: a repo can have MULTIPLE tech stacks.
     * Uses a join table "repo_tech_stacks" connecting to the "tech_stacks" table.
     * Example: ["React", "Spring Boot", "PostgreSQL"]
     */
    @ManyToMany
    @JoinTable(
            name = "repo_tech_stacks",
            joinColumns = @JoinColumn(name = "repo_id"),
            inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    @Builder.Default
    private Set<TechStack> techStacks = new LinkedHashSet<>();

    /** Short, newline-separated repository tree used by frontend evaluation */
    @Column(name = "file_tree", columnDefinition = "TEXT")
    private String fileTree;

    /** Timestamp when this repo was approved by an admin */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /** AI-evaluated repo type: e.g. "fullstack", "backend", "frontend", "mobile", "data-science" */
    @Column(name = "repo_type", length = 50)
    private String repoType;

    /** AI-evaluated usefulness rating: e.g. "excellent", "good", "average", "limited", "unusable" */
    @Column(name = "usefulness_rating", length = 50)
    private String usefulnessRating;

    /** AI-evaluated usefulness score (numeric, higher = more useful) */
    @Column(name = "usefulness_score")
    private Integer usefulnessScore;

    /** AI-evaluated readiness level: e.g. "ready", "needs-work", "incomplete" */
    @Column(name = "ready_to_use_level", length = 50)
    private String readyToUseLevel;
}

