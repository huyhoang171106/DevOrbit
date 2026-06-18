package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * COURSE REFERENCE = a textbook, paper, or reference material for a course.
 *
 * Maps to the "course_references" table.
 * Stores citations extracted from the course syllabus.
 *
 * Examples:
 *   "[1] Giáo trình Nhập môn CNPM, NXB ĐHQG-HCM, 2020"
 *   "[2] Sommerville, Software Engineering, 10th ed."
 *
 * Part of the CourseSyllabus knowledge model.
 */
@Entity
@Table(name = "course_references")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The syllabus document this reference was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /** The course this reference belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /** Full citation text in the university's format. */
    @Column(name = "reference_text", nullable = false, columnDefinition = "TEXT")
    private String referenceText;
}
