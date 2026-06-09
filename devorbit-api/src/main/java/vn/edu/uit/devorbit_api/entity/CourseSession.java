package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "course_sessions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    @Column(name = "session_no", length = 50)
    private String sessionNo;

    @Column(name = "session_type", nullable = false, length = 50)
    private String sessionType; // e.g., THEORY, PRACTICE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String activities;

    @Column(name = "assessment_component", columnDefinition = "TEXT")
    private String assessmentComponent;
}
