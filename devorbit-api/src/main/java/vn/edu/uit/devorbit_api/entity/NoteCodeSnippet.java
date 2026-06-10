package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * NOTE CODE SNIPPET = a piece of code embedded inside a student note.
 *
 * Maps to the "note_code_snippets" table.
 * Each snippet belongs to ONE note and contains:
 * - The code itself
 * - The programming language (for syntax highlighting)
 * - An optional caption explaining the code
 *
 * sortOrder controls the display order within the note.
 */
@Entity
@Table(name = "note_code_snippets")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteCodeSnippet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The note this snippet belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    /** Programming language (e.g., "java", "python", "javascript") */
    @Column(nullable = false, length = 50)
    private String language;

    /** The actual code content */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    /** Short description of what this code does */
    @Column(length = 255)
    private String caption;

    /** Display order (0 = first, 1 = second, etc.) */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
