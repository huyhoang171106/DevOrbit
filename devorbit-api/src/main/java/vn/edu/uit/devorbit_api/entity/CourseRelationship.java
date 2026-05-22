package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * COURSE RELATIONSHIP = a link between two courses.
 *
 * Maps to the "course_relationships" table.
 * Defines how courses connect to each other in the knowledge graph.
 *
 * RELATIONSHIP TYPES:
 *   PREREQUISITE   — Course A must be done BEFORE Course B
 *   COMPLEMENTARY  — Course A and Course B are related but neither requires the other
 *   COREQUISITE    — Course A and Course B should be taken in the SAME semester
 *
 * Example:
 *   course = SE201 (Advanced Programming)
 *   relatedCourse = SE101 (Intro to Programming)
 *   relationType = PREREQUISITE
 *   Meaning: SE101 is a prerequisite FOR SE201
 *
 * The unique constraint prevents creating the same relationship twice.
 */
@Entity
@Table(name = "course_relationships", uniqueConstraints = @UniqueConstraint(
        columnNames = {"course_id", "related_course_id", "relation_type"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The target course (the one that HAS the relationship) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** The related course (the one the relationship POINTS TO) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_course_id", nullable = false)
    private Course relatedCourse;

    /**
     * PREREQUISITE = must be done first
     * COMPLEMENTARY = related but not required
     * COREQUISITE = should be taken together
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 50)
    private CourseRelationType relationType;

    /** When this relationship was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
