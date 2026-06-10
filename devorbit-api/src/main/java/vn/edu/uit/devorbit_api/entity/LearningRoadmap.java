package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * LEARNING ROADMAP = a personalized study plan for a student.
 *
 * Maps to the "learning_roadmaps" table.
 * A roadmap is a structured plan that guides a student through courses and repos.
 *
 * Structure:
 *   LearningRoadmap (title, description)
 *     └── RoadmapPhase (a phase: "Basics", "Advanced")
 *           └── RoadmapItem (a course or repo to study)
 *
 * The markdownContent field stores a pre-rendered version of the roadmap
 * in Markdown format (human-readable text with headings, links, etc.).
 */
@Entity
@Table(name = "learning_roadmaps")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The student who owns this roadmap */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /** Roadmap title (e.g., "AI Engineer Path", "Web Dev Roadmap") */
    @Column(nullable = false, length = 255)
    private String title;

    /** What this roadmap covers */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Pre-rendered Markdown version (for easy display on frontend) */
    @Column(name = "markdown_content", columnDefinition = "TEXT")
    private String markdownContent;

    /** Can other students see this roadmap? */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false;

    /** When the roadmap was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** When the roadmap was last updated */
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
