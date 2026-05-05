package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_course_id", nullable = false)
    private Course relatedCourse;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 50)
    private CourseRelationType relationType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
