package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(length = 255)
    private String caption;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
