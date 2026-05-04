package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, length = 120)
    private String name;

    // Kept for legacy schemas that still have tech_stacks.repo_id; new code uses GithubRepo.techStacks.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private GithubRepo repo;
}
