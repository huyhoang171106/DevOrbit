package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

/**
 * COMMUNITY MESSAGE = a public message in a course community chat channel.
 *
 * Maps to the "community_messages" table.
 * Unlike ChatMessage (private AI chat), this is PUBLIC — everyone
 * in the channel can see it.
 *
 * Messages can be "soft-deleted" (deleted = true) instead of actually
 * being removed from the database. This keeps the conversation flow
 * intact for other participants while hiding the content.
 *
 * Compare with ChatMessage:
 *   ChatMessage        → private AI tutor chat (sender = STUDENT or AI)
 *   CommunityMessage   → public peer-to-peer chat (sender = StudentUser)
 */
@Entity
@Table(name = "community_messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Which channel this message was posted in. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChatChannel channel;

    /** Which student sent this message. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentUser student;

    /** The message body text. */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** Public URL of an image message (nullable for text-only messages). */
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * Soft delete flag.
     * true = message is hidden from users but stays in DB.
     * This preserves replies and conversation context.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
