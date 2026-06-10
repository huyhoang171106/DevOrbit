package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * COURSE ARTICLE = a reference article/link attached to a course.
 *
 * Maps to the "course_articles" table.
 * Each article belongs to ONE course.
 *
 * Fetch via: GET /api/courses/{courseId}/articles
 */
@Entity
@Table(name = "course_articles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The course this article belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Article title */
    @Column(nullable = false, length = 255)
    private String title;

    /** URL to the article */
    @Column(nullable = false, length = 255)
    private String url;

    /** Article author name */
    @Column(length = 255)
    private String author;

    /** Short description of the article */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** When this article was added */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
