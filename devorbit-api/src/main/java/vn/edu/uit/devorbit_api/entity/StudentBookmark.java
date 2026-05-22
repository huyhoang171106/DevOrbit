package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * STUDENT BOOKMARK = a saved link/bookmark by a student.
 *
 * Maps to the "student_bookmarks" table.
 * Students can bookmark courses, repos, tutorials, articles, etc.
 * Each bookmark belongs to ONE student and has:
 * - A target type (what kind of thing is bookmarked)
 * - A target ID (the specific item)
 * - A display title + URL
 *
 * The unique constraint prevents bookmarking the same thing twice.
 */
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

    /** The student who saved this bookmark */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /** What type of thing is bookmarked (e.g., "COURSE", "REPO", "ARTICLE") */
    @Column(name = "target_type", nullable = false)
    private String targetType;

    /** The ID of the bookmarked item */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /** Display title */
    @Column(nullable = false)
    private String title;

    /** Optional subtitle/description */
    @Column
    private String subtitle;

    /** The URL of the bookmarked item */
    @Column(nullable = false)
    private String url;

    /** When the bookmark was created */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
