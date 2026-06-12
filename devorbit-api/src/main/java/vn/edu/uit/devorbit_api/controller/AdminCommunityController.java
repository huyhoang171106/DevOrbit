package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.CommunityMessageAdminResponse;
import vn.edu.uit.devorbit_api.entity.ChatChannel;
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

    @GetMapping("/channels")
    public ResponseEntity<List<ChatChannel>> listChannels() {
        return ResponseEntity.ok(chatChannelRepo.findAllByOrderByTypeAscNameAsc());
    }

    @GetMapping("/messages")
    public ResponseEntity<List<CommunityMessageAdminResponse>> listMessages() {
        return ResponseEntity.ok(communityMessageRepo.findAllByOrderByCreatedAtDesc()
            .stream().map(m -> CommunityMessageAdminResponse.builder()
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
        if (!communityMessageRepo.existsById(id)) {
            throw new NotFoundException("Community message not found");
        }
        communityMessageRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
