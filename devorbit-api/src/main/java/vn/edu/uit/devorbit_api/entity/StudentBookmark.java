package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_bookmarks",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "target_type", "target_id"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
