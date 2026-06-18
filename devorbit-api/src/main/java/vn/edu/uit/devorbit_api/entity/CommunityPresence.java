package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * COMMUNITY PRESENCE = tracks which students are online in which chat channels.
 *
 * Maps to the "community_presences" table.
 * Used by the WebSocket/STOMP real-time presence system.
 *
 * When a student connects to a channel, a presence row is created.
 * When they disconnect, it's removed. The frontend uses this to show
 * "Online members" lists.
 *
 * sessionId = the WebSocket session ID
 * subscriptionId = the STOMP subscription ID
 * Together they allow tracking multiple tabs/devices for the same student.
 *
 * Flow:
 *   Student connects WebSocket → subscribes to /topic/channel/{id}
 *   → CommunityPresenceEventListener adds a row
 *   → Student disconnects → listener removes the row
 *   → Other students see updated online list via STOMP broadcast
 */
@Entity
@Table(name = "community_presences")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** WebSocket session ID (unique per connection). */
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    /** STOMP subscription ID (unique per channel subscription). */
    @Column(name = "subscription_id", nullable = false)
    private String subscriptionId;

    /** Which channel the student is in. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChatChannel channel;

    /** The student's identifier (student code). */
    @Column(name = "student_code", nullable = false)
    private String studentCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
