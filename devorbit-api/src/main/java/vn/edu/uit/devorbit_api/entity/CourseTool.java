package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * COURSE TOOL = a software tool or technology used in a course.
 *
 * Maps to the "course_tools" table.
 * Different from TechStack — this records tools mentioned in the
 * syllabus that students will USE during the course.
 *
 * Examples:
 *   "Eclipse IDE", "Visual Studio Code", "Git", "Postman"
 *
 * Extracted from the CourseSyllabus knowledge source.
 */
@Entity
@Table(name = "course_tools")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseTool {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The syllabus document this tool was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /** The course this tool belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /** Name of the tool (e.g., "Visual Studio Code"). */
    @Column(name = "tool_name", nullable = false, columnDefinition = "TEXT")
    private String toolName;
}
