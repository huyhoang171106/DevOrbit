package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * TECH STACK = a technology name that can be tagged to repos.
 *
 * Maps to the "tech_stacks" table.
 * Each row is one technology (e.g., "React", "Spring Boot", "PostgreSQL").
 * A GithubRepo can have MULTIPLE tech stacks via the ManyToMany relationship.
 *
 * Legacy note: The `repo` field exists for backward compatibility with older
 * schemas where each tech stack was tied to one repo. NEW repos use
 * GithubRepo.techStacks (the ManyToMany join table).
 */
@Entity
@Table(name = "tech_stacks", uniqueConstraints = @UniqueConstraint(name = "uk_tech_stack_name", columnNames = "name"))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The name of the technology (e.g., "React", "Spring Boot") */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * LEGACY FIELD: kept for old database rows.
     * New code uses GithubRepo.techStacks instead of this.
     * In the future, this field may be removed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private GithubRepo repo;
}
