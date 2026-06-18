package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CommunityMessageAdminResponse;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageResponse;
import vn.edu.uit.devorbit_api.entity.ChatChannel;
import vn.edu.uit.devorbit_api.entity.CommunityMessage;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.ChatChannelRepository;
import vn.edu.uit.devorbit_api.repository.CommunityMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for community management (channels and messages).
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/community/channels - List all chat channels</li>
 *   <li>GET /api/admin/community/messages - List non-deleted community messages</li>
 *   <li>DELETE /api/admin/community/messages/{id} - Soft-delete a message (replaces content + sets deleted flag) + WebSocket broadcast</li>
 * </ul>
 * <p>
 * Security: ADMIN role required. Soft-delete preserves message record but replaces
 * content with a Vietnamese notice; the deleted message is broadcast via STOMP /topic/channel/{channelId}.
 * Read-only by default (transactional), except deleteMessage which is transactional write.
 */
@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunityController {

    private final CommunityMessageRepository communityMessageRepo;
    private final ChatChannelRepository chatChannelRepo;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * List all community chat channels ordered by type then name ascending.
     *
     * @return 200 OK with list of ChatChannel entities
     * @apiNote GET /api/admin/community/channels
     */
    @GetMapping("/channels")
    public ResponseEntity<List<ChatChannel>> listChannels() {
        return ResponseEntity.ok(chatChannelRepo.findAllByOrderByTypeAscNameAsc());
    }

    /**
     * List non-deleted community messages, most recent first.
     * Messages from deleted channels or students are labeled "Deleted".
     *
     * @return 200 OK with list of CommunityMessageAdminResponse
     * @apiNote GET /api/admin/community/messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<CommunityMessageAdminResponse>> listMessages() {
        return ResponseEntity.ok(communityMessageRepo.findAllByOrderByCreatedAtDesc()
            .stream().filter(m -> !m.isDeleted()).map(m -> CommunityMessageAdminResponse.builder()
                .id(m.getId())
                .channelName(m.getChannel() != null ? m.getChannel().getName() : "Deleted")
                .studentName(m.getStudent() != null ? m.getStudent().getFullName() : "Deleted")
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build())
            .collect(Collectors.toList()));
    }

    /**
     * Soft-delete a community message by ID.
     * <p>
     * The message content is replaced with a Vietnamese admin-removal notice,
     * the deleted flag is set to true, and the update is broadcast via STOMP
     * to /topic/channel/{channelId} so all clients see the change in realtime.
     *
     * @param id Long ID of the community message to soft-delete
     * @return 200 OK if successful
     * @throws NotFoundException if message not found
     * @apiNote DELETE /api/admin/community/messages/{id}
     */
    @DeleteMapping("/messages/{id}")
    @Transactional
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        CommunityMessage msg = communityMessageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Community message not found"));
        Long channelId = msg.getChannel().getId();
        msg.setContent("Tin nhắn này đã bị admin xóa vì nghi ngờ vi phạm tiêu chuẩn cộng đồng");
        msg.setDeleted(true);
        communityMessageRepo.save(msg);
        ChatMessageResponse resp = new ChatMessageResponse(
                msg.getId(),
                msg.getChannel().getId(),
                msg.getStudent().getId(),
                msg.getStudent().getFullName(),
                msg.getStudent().getAvatar(),
                msg.getContent(),
                msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null,
                msg.isDeleted());
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, resp);
        return ResponseEntity.ok().build();
    }
}
