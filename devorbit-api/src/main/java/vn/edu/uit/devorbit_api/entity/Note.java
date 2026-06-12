package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import java.time.LocalDateTime;

/**
 * NOTE = a note that a student writes about a course or repo.
 *
 * Maps to the "notes" table.
 * Each note belongs to ONE student and can be linked to a course or repo.
 * The content is written in Markdown format (rich text).
 *
 * targetType + targetId tell us WHAT the note is about:
 *   - COURSE + courseId → note about a course
 *   - REPO + repoId   → note about a GitHub repo
 *   - NONE + null     → general note with no specific target
 */
@Entity
@Table(name = "notes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Which student wrote this note */
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /** Note title */
    @Column(nullable = false, length = 255)
    private String title;

    /** The note content, written in Markdown format */
    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    /**
     * What this note is about: COURSE, REPO, or NONE (general)
     * Used together with targetId to find the target object.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    @Builder.Default
    private NoteTargetType targetType = NoteTargetType.NONE;

    /** The ID of the course/repo this note refers to (null if general note) */
    @Column(name = "target_id")
    private Long targetId;

    /** When the note was created */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** When the note was last updated */
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
