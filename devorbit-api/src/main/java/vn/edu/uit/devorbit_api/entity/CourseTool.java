package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    @Column(name = "tool_name", nullable = false, columnDefinition = "TEXT")
    private String toolName;
}
