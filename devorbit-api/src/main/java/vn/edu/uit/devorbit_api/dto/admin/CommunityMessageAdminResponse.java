package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Admin-facing DTO for a community (public channel) message.
 *
 * <p>Community messages are public messages posted by students in
 * course-related communication channels (e.g. Discord-style text
 * channels). This DTO is used by the admin to monitor and moderate
 * community discussions. It includes the channel name, the student's
 * display name, the message content, and the timestamp.</p>
 *
 * <p><b>Used by:</b> {@code GET /api/admin/community/messages}
 * — list all community messages (newest first) for moderation.</p>
 *
 * <p><b>Note:</b> The corresponding delete endpoint
 * ({@code DELETE /api/admin/community/messages/{id}}) does not return
 * this DTO; it returns {@code 204 No Content}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMessageAdminResponse {

    /** Internal primary key of the community message. */
    private Long id;

    /** Name of the channel where the message was posted
     * (e.g. {@code "thong-bao"}, {@code "hoi-dap"}). */
    private String channelName;

    /** Display name of the student who sent the message. */
    private String studentName;

    /** The text content of the message. */
    private String content;

    /** Timestamp when the message was sent. */
    private LocalDateTime createdAt;
}
