package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * COURSE ASSESSMENT = a graded component of a course.
 *
 * Maps to the "course_assessments" table.
 * Defines how students are evaluated in the course.
 *
 * Examples:
 *   componentCode | description              | weightPercent
 *   --------------|--------------------------|--------------
 *   BT            | Bài tập (Homework)        | 20
 *   KT            | Kiểm tra (Midterm)        | 30
 *   THI           | Thi cuối kỳ (Final Exam)  | 50
 *
 * weightPercent values should sum to 100 for a complete assessment plan.
 *
 * Extracted from the CourseSyllabus knowledge source.
 */
@Entity
@Table(name = "course_assessments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The syllabus document this assessment was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /** The course this assessment belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /**
     * Short assessment code.
     * Examples: "BT" (Homework), "KT" (Midterm), "THI" (Final)
     */
    @Column(name = "component_code", nullable = false, length = 50)
    private String componentCode;

    /** Description of what this assessment covers. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Weight toward the final grade (percentage).
     * Example: 20 means 20% of the final grade.
     * Range: 0-100
     */
    @Column(name = "weight_percent")
    private Integer weightPercent;
}
