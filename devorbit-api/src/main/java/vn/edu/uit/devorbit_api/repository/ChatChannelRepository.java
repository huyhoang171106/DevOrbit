package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.ChatChannel;
import vn.edu.uit.devorbit_api.entity.ChatChannelType;

import java.util.Optional;
import java.util.List;

/**
 * CHAT CHANNEL REPOSITORY = data access for community chat channels.
 *
 * Channels are created automatically when courses are added
 * (each course gets its own chat channel) and can also be created
 * manually by admins (GENERAL, TECH_STACK types).
 *
 * Channel naming convention:
 *   GENERAL    → channelId = "general"
 *   COURSE     → channelId = "course-" + courseCode (e.g., "course-SE101")
 *   TECH_STACK → channelId = "tech-" + stackName (e.g., "tech-React")
 */
@Repository
public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {

    /** Find by human-readable channel ID (e.g., "course-SE101"). */
    Optional<ChatChannel> findByChannelId(String channelId);

    /** All channels, sorted by type first, then name alphabetically. */
    List<ChatChannel> findAllByOrderByTypeAscNameAsc();

    /** Only active channels, sorted by type then name. */
    List<ChatChannel> findByActiveTrueOrderByTypeAscNameAsc();

    /** Find channels by type + reference (e.g., all COURSE channels for a course). */
    List<ChatChannel> findByTypeAndReferenceId(ChatChannelType type, String referenceId);

    /** Find ONE active channel by type + reference. */
    Optional<ChatChannel> findByTypeAndReferenceIdAndActiveTrue(ChatChannelType type, String referenceId);

    /** Delete all channels of a type + reference (used when deleting a course). */
    void deleteByTypeAndReferenceId(ChatChannelType type, String referenceId);
}
