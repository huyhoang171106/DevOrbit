package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ROADMAP PHASE = a stage/phase in a learning roadmap.
 *
 * Maps to the "roadmap_phases" table.
 * A roadmap has MULTIPLE phases (e.g., "Phase 1: Basics", "Phase 2: Advanced").
 * Each phase contains MULTIPLE roadmap items (courses or repos to study).
 *
 * Structure:
 *   LearningRoadmap (1 student's roadmap)
 *     └── RoadmapPhase (a phase in that roadmap)
 *           └── RoadmapItem (a course or repo to study in that phase)
 *
 * sortOrder determines the display sequence (0 = first phase).
 */
@Entity
@Table(name = "roadmap_phases")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The roadmap this phase belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private LearningRoadmap roadmap;

    /** Phase title (e.g., "Foundation", "Core", "Advanced") */
    @Column(nullable = false, length = 255)
    private String title;

    /** What this phase focuses on */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Display order: 0 = first phase, 1 = second, etc. */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    /** When this phase was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
