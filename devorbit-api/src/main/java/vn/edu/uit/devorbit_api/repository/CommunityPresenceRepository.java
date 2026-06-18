package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CommunityPresence;

import java.util.List;
import java.util.Optional;

/**
 * COMMUNITY PRESENCE REPOSITORY = tracks who's online in community chat channels.
 *
 * Used by the WebSocket presence system:
 *   - Student connects   → findBySessionIdAndSubscriptionId (check if already tracked)
 *   - Student disconnects → deleteBySessionId (cleanup all their entries)
 *   - Frontend needs list → findByChannelId (get all students in a channel)
 *
 * sessionId = WebSocket session (unique per browser tab)
 * subscriptionId = STOMP subscription (unique per channel per tab)
 */
@Repository
public interface CommunityPresenceRepository extends JpaRepository<CommunityPresence, Long> {

    /** Find a specific session's presence in a specific channel subscription. */
    Optional<CommunityPresence> findBySessionIdAndSubscriptionId(String sessionId, String subscriptionId);

    /** All presence entries for a WebSocket session (could be multiple tabs). */
    List<CommunityPresence> findBySessionId(String sessionId);

    /** All students currently present in a channel. Used to render "Online" list. */
    List<CommunityPresence> findByChannelId(Long channelId);

    /** Remove one specific subscription (e.g., student left one channel). */
    void deleteBySessionIdAndSubscriptionId(String sessionId, String subscriptionId);

    /** Remove ALL entries for a session (e.g., WebSocket disconnected). */
    void deleteBySessionId(String sessionId);
}
