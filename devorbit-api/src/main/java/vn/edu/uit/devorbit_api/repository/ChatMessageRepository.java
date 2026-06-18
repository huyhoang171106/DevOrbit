package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.ChatMessage;
import java.util.List;
import java.util.UUID;

/**
 * CHAT MESSAGE REPOSITORY = data access for AI tutor chat messages.
 *
 * Messages belong to ChatSessions (each session = one Q&A conversation).
 * OrderByCreatedAtAsc ensures messages are returned in chronological order
 * (oldest first = the natural reading order for a chat).
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** All messages in a session, oldest first (chronological order). */
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);

    /** Count how many messages are in a session. */
    long countBySessionId(UUID sessionId);
}
