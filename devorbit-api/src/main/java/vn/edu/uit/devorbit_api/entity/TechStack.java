package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * TECH STACK = a technology label that can be tagged to GitHub repos.
 *
 * Maps to the "tech_stacks" table.
 * Each row is one technology (e.g., "React", "Spring Boot", "PostgreSQL").
 *
 * ┌──────────────────────────────────────────────────────────────────┐
 * │ RELATIONSHIP WITH GITHUBREPO                                    │
 * │                                                                  │
 * │   GithubRepo ----< repo_tech_stacks >---- TechStack             │
 * │   (ManyToMany join table)                                       │
 * │                                                                  │
 * │   A repo can have MANY tech stacks (e.g., React + Node + Docker)│
 * │   A tech stack can be on MANY repos                             │
 * └──────────────────────────────────────────────────────────────────┘
 *
 * ⚠️ LEGACY NOTE:
 *    The `repo` field (ManyToOne) is an OLD design where each tech stack
 *    was tied to exactly ONE repo. This still exists for old database rows.
 *    NEW repos use GithubRepo.techStacks (the ManyToMany join table).
 *    The `repo` field will be removed in a future migration.
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

    /**
     * The technology name.
     * Must be unique (e.g., only one "React" row).
     * Examples: "React", "Spring Boot", "Docker", "PostgreSQL"
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * ⚠️ LEGACY FIELD — DO NOT USE FOR NEW DATA.
     *
     * This was used in the old schema where each TechStack belonged to
     * exactly one repo. Now replaced by GithubRepo.techStacks (ManyToMany).
     *
     * Kept for backward compatibility with existing database rows.
     * Will be removed in a future database migration.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private GithubRepo repo;
}
