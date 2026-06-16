package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminTechStackResponse;
import vn.edu.uit.devorbit_api.entity.ChatChannelType;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.ChatChannelRepository;
import vn.edu.uit.devorbit_api.repository.CommunityMessageRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;
import vn.edu.uit.devorbit_api.service.CommunityChatService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/techstack")
@RequiredArgsConstructor
public class AdminTechStackController {

    private final TechStackRepository techStackRepo;
    private final CommunityChatService communityChatService;
    private final ChatChannelRepository chatChannelRepository;
    private final CommunityMessageRepository communityMessageRepository;

    @GetMapping
    public ResponseEntity<List<AdminTechStackResponse>> list() {
        return ResponseEntity.ok(techStackRepo.findAllDistinctOrderByName()
            .stream().map(this::toResponse)
            .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AdminTechStackResponse> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (techStackRepo.findByNameIgnoreCase(name.trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        TechStack techStack = TechStack.builder().name(name.trim()).build();
        TechStack saved = techStackRepo.save(techStack);
        communityChatService.createChannel(ChatChannelType.TECH_STACK, String.valueOf(saved.getId()), saved.getName());
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        if (!techStackRepo.existsById(id)) {
            throw new NotFoundException("Tech stack not found");
        }
        var channels = chatChannelRepository.findByTypeAndReferenceId(ChatChannelType.TECH_STACK, String.valueOf(id));
        boolean channelDeactivated = false;
        if (!channels.isEmpty()) {
            var channel = channels.get(0);
            if (communityMessageRepository.existsByChannelId(channel.getId())) {
                channel.setActive(false);
                chatChannelRepository.save(channel);
                channelDeactivated = true;
            } else {
                chatChannelRepository.delete(channel);
            }
        }
        techStackRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("channelDeactivated", channelDeactivated));
    }

    private AdminTechStackResponse toResponse(TechStack ts) {
        return AdminTechStackResponse.builder()
            .id(ts.getId())
            .name(ts.getName())
            .build();
    }
}
