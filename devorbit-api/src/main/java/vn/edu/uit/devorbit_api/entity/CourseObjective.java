package vn.edu.uit.devorbit_api.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

/**
 * COURSE OBJECTIVE = a learning goal for a course.
 *
 * Maps to the "course_objectives" table.
 * Objectives describe what students will learn or be able to do
 * after completing the course.
 *
 * Example objectives:
 *   "Understand OOP principles: encapsulation, inheritance, polymorphism"
 *   "Be able to write REST APIs using Spring Boot"
 *
 * Each objective can reference one or more CourseOutcomes via
 * the outcomeRefs JSON field. This links "what we teach" (objective)
 * to "what students can do" (outcome).
 *
 * Extracted from the CourseSyllabus knowledge source.
 */
@Entity
@Table(name = "course_objectives")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The syllabus document this objective was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /** The course this objective belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /** Detailed description of the learning objective. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * JSON array linking this objective to specific outcomes.
     * Each entry references an outcome code.
     * Example: [{ "outcomeCode": "O1", "weight": "high" }]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "outcome_refs")
    private JsonNode outcomeRefs;
}
