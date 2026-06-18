package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Admin-facing DTO for a single chat message within a student chat session.
 *
 * <p>This DTO exposes the messages inside a student's AI-chat session
 * to the admin for monitoring and moderation purposes. It shows who
 * sent the message ({@code sender}), the message content, and when it
 * was created. The admin can read through conversations to understand
 * student struggles or verify appropriate usage.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/chat/sessions/{sessionId}/messages}
 * — list all messages in a specific chat session (ordered by creation time).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageAdminResponse {

    /** Internal primary key of the chat message. */
    private Long id;

    /**
     * Display name or role of the message sender.
     * Typically {@code "Student"} or the AI assistant name.
     */
    private String sender;

    /** The text content of the message. */
    private String content;

    /** Timestamp when the message was sent. */
    private LocalDateTime createdAt;
}
