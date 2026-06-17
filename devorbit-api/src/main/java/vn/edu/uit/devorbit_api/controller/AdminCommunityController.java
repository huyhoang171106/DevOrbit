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

@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunityController {

    private final CommunityMessageRepository communityMessageRepo;
    private final ChatChannelRepository chatChannelRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/channels")
    public ResponseEntity<List<ChatChannel>> listChannels() {
        return ResponseEntity.ok(chatChannelRepo.findAllByOrderByTypeAscNameAsc());
    }

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
