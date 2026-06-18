package vn.edu.uit.devorbit_api.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

/**
 * CHAT MESSAGE = one message in an AI tutor chat session.
 *
 * Maps to the "chat_messages" table.
 * Each message belongs to ONE ChatSession and is either from
 * the student or the AI tutor.
 *
 * Chat flow:
 *   Student types question → ChatMessage(sender=STUDENT) saved
 *   → AI processes → ChatMessage(sender=AI) saved with answer + sources
 *
 * The `sources` JSON field stores citation links the AI used to
 * generate the answer (extracted from the knowledge base).
 *
 * Compare with CommunityMessage: this is a PRIVATE 1-on-1 chat with AI,
 * while CommunityMessage is a PUBLIC chat in a course community channel.
 */
@Entity
@Table(name = "chat_messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The chat session this message belongs to.
     * A session groups a sequence of Q&A exchanges about one topic.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    /**
     * Who sent this message.
     * Values: "STUDENT" or "AI"
     * Used by the frontend to align text left/right in the chat UI.
     */
    @Column(nullable = false, length = 10)
    private String sender;

    /**
     * The actual message text.
     * For STUDENT: the question they typed.
     * For AI: the generated answer (may contain Markdown formatting).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * [AI only] JSON array of source citations the AI used.
     * Each entry: { title, url, excerpt }
     * Stored as JSONB in PostgreSQL — queryable with JSON operators.
     *
     * Example:
     *   [{ "title": "SE101 Syllabus", "url": "...", "excerpt": "..." }]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sources")
    private JsonNode sources;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
