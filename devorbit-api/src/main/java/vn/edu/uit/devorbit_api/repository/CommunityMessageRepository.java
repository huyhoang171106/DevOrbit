package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CommunityMessage;

import java.util.List;

/**
 * COMMUNITY MESSAGE REPOSITORY = data access for public community chat messages.
 *
 * Uses PAGINATION (Page + Pageable) because channels can have thousands of messages.
 * The "deleted = false" filter in findByChannelIdAndDeletedFalse... ensures
 * soft-deleted messages are hidden from users.
 *
 * Compare with ChatMessageRepository — community messages use Pageable pagination
 * while AI chat messages return all messages in a session (sessions are typically short).
 */
@Repository
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {

    /** Paginated messages for a channel, excluding soft-deleted ones, newest first. */
    Page<CommunityMessage> findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(Long channelId, Pageable pageable);

    /** [ADMIN] Paginated messages including deleted ones. */
    Page<CommunityMessage> findByChannelIdOrderByCreatedAtDesc(Long channelId, Pageable pageable);

    /** [ADMIN] All messages across all channels. */
    List<CommunityMessage> findAllByOrderByCreatedAtDesc();

    /** Check if a channel has any messages at all. */
    boolean existsByChannelId(Long channelId);
}
