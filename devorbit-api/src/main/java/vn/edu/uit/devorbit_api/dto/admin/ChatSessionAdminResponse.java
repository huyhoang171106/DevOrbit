package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Admin-facing DTO for a student AI-chat session summary.
 *
 * <p>This DTO provides a high-level overview of each chat session
 * between a student and the AI assistant. It is used in the admin
 * monitoring panel to list all active and historical conversations.
 * Each session has a unique {@code id} (UUID), a student name, a
 * session title, the total message count, and the creation timestamp.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/chat/sessions}
 * — list all chat sessions with student names and message counts.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionAdminResponse {

    /** Unique identifier of the chat session (UUID, not auto-increment). */
    private UUID id;

    /** Display name of the student who owns this session (e.g. "Nguyen Van A"). */
    private String studentName;

    /**
     * Auto-generated or student-provided title for the session.
     * Example: {@code "Help with Java Stream API homework"}.
     */
    private String title;

    /** Total number of messages exchanged in this session. */
    private long messageCount;

    /** Timestamp when the chat session was first created. */
    private LocalDateTime createdAt;
}
