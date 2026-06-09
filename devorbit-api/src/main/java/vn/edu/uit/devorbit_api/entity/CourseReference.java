package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    @Column(name = "reference_text", nullable = false, columnDefinition = "TEXT")
    private String referenceText;
}
