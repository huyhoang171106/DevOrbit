package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * COURSE TUTORIAL = a step-by-step guide or walkthrough for a course.
 *
 * Maps to the "course_tutorials" table.
 * Each tutorial belongs to ONE course.
 * Type can be "article", "guide", "lab", etc.
 *
 * Fetch via: GET /api/courses/{courseId}/tutorials
 */
@Entity
@Table(name = "course_tutorials")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseTutorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The course this tutorial belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Tutorial title */
    @Column(nullable = false, length = 255)
    private String title;

    /** URL to the tutorial content */
    @Column(nullable = false, length = 255)
    private String url;

    /** What kind of tutorial: "article", "video", "lab", etc. */
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String type = "article";

    /** Short description of what the tutorial covers */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** When this tutorial was added */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
