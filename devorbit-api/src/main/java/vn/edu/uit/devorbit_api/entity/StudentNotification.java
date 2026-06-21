package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false)
    private String studentCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Builder.Default
    private String body = "";

    @Column(nullable = false)
    @Builder.Default
    private String type = "NEW_REPO";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private GithubRepo repo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "tech_stack_name")
    private String techStackName;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
