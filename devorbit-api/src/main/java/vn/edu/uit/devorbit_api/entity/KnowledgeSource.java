package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * KNOWLEDGE SOURCE = a document or URL that was imported into the knowledge base.
 *
 * Maps to the "knowledge_sources" table.
 * This is the ROOT of the knowledge model — every piece of knowledge
 * (syllabus, session, objective, outcome, assessment, chunk) traces back
 * to ONE KnowledgeSource.
 *
 * source_type tells us where the content came from:
 *   FILE     — imported from a local file (PDF, Word, text)
 *   URL      — crawled from a web page
 *   FIRECRAWL — imported via the Firecrawl web scraper
 *   MANUAL   — manually entered by admin
 *
 * Lifecycle:
 *   New source → status = "PENDING" → ingestion → "COMPLETED" or "FAILED"
 *
 * After successful ingestion, the content is split into KnowledgeChunks
 * and stored separately for vector search.
 */
@Entity
@Table(name = "knowledge_sources")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeSource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * How this source was obtained.
     * Values: "FILE", "URL", "FIRECRAWL", "MANUAL"
     */
    @Column(name = "source_type", nullable = false, length = 100)
    private String sourceType;

    /** Human-readable title of the source. */
    @Column(columnDefinition = "TEXT")
    private String title;

    /** Original filename (for FILE sources). */
    @Column(name = "file_name", columnDefinition = "TEXT")
    private String fileName;

    /** Path on the server (for FILE sources). */
    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    /** URL the content was fetched from (for URL sources). */
    @Column(columnDefinition = "TEXT")
    private String url;

    /**
     * Hash of the content — used to detect changes and avoid re-importing
     * the same content twice.
     */
    @Column(name = "content_hash", nullable = false, columnDefinition = "TEXT")
    private String contentHash;

    /**
     * How trustworthy this source is.
     * OFFICIAL    — From the university (syllabus, official docs) — highest priority
     * UNOFFICIAL  — Community-contributed or web-sourced — lower priority
     */
    @Column(name = "trust_level", nullable = false, length = 50)
    @Builder.Default
    private String trustLevel = "OFFICIAL";

    /**
     * Ingestion status.
     * Values: "PENDING", "PROCESSING", "COMPLETED", "FAILED"
     */
    @Column(nullable = false, length = 50)
    private String status;

    /** The full raw text content of the source (before chunking). */
    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    /** Error message if ingestion failed. */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
