package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ROADMAP ITEM = a single item inside a roadmap phase.
 *
 * Maps to the "roadmap_items" table.
 * Each item represents ONE thing to study: either a COURSE or a REPO.
 *
 * Structure:
 *   LearningRoadmap → RoadmapPhase → RoadmapItem
 *
 * targetType + targetId tell us WHAT to study:
 *   - COURSE + courseId → take this course
 *   - REPO + repoId     → study this GitHub repo
 *
 * The note field can contain tips or explanations.
 * sortOrder determines display sequence within the phase.
 */
@Entity
@Table(name = "roadmap_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The phase this item belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private RoadmapPhase phase;

    /** Is this item a COURSE or a REPO? */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private RoadmapItemTargetType targetType;

    /** The ID of the course or repo to study */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /** Display title of this item */
    @Column(length = 255)
    private String title;

    /** Optional note with tips or explanations */
    @Column(columnDefinition = "TEXT")
    private String note;

    /** Display order within the phase (0 = first) */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    /** When this item was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
