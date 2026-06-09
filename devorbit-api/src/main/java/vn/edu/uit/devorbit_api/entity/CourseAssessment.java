package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    @Column(name = "component_code", nullable = false, length = 50)
    private String componentCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "weight_percent")
    private Integer weightPercent;
}
