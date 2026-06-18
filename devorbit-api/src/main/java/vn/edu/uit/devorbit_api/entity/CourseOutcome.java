package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * COURSE OUTCOME = a measurable skill students gain from a course.
 *
 * Maps to the "course_outcomes" table.
 * Outcomes describe what a student can DO after finishing the course.
 * Unlike objectives (intentions), outcomes are measurable and specific.
 *
 * Examples:
 *   Code: "O1"
 *   Desc: "Analyze and design software systems using UML diagrams"
 *
 * Each outcome has a short code (e.g., "O1", "O2") that CourseObjective
 * references in its outcomeRefs JSON field.
 *
 * Extracted from the CourseSyllabus knowledge source.
 */
@Entity
@Table(name = "course_outcomes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The syllabus document this outcome was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /** The course this outcome belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /**
     * Short identifier for this outcome.
     * Examples: "O1", "OUT1", "CLO1"
     * Referenced by CourseObjective.outcomeRefs.
     */
    @Column(name = "outcome_code", nullable = false, length = 50)
    private String outcomeCode;

    /** Full description of what the student can do. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
