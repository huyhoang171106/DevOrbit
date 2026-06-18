package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
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

/**
 * Admin controller for managing tech stacks.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/techstack - List all tech stacks</li>
 *   <li>POST /api/admin/techstack - Create a new tech stack</li>
 *   <li>DELETE /api/admin/techstack/{id} - Delete a tech stack</li>
 * </ul>
 * <p>
 * Security: ADMIN role required.
 * Creating a tech stack also auto-creates a community chat channel (TECH_STACK type).
 * Deleting either hard-deletes the associated channel (if no messages exist)
 * or deactivates it (if messages exist) to preserve history.
 */
@RestController
@RequestMapping("/api/admin/techstack")
@RequiredArgsConstructor
public class AdminTechStackController {

    private final TechStackRepository techStackRepo;
    private final CommunityChatService communityChatService;
    private final ChatChannelRepository chatChannelRepository;
    private final CommunityMessageRepository communityMessageRepository;

    /**
     * List all distinct tech stacks ordered by name.
     * Response has Cache-Control: no-cache, must-revalidate.
     *
     * @return 200 OK with list of AdminTechStackResponse
     * @apiNote GET /api/admin/techstack
     */
    @GetMapping
    public ResponseEntity<List<AdminTechStackResponse>> list() {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache().mustRevalidate())
            .body(techStackRepo.findAllDistinctOrderByName()
                .stream().map(this::toResponse)
                .collect(Collectors.toList()));
    }

    /**
     * Create a new tech stack.
     * <p>
     * Also auto-creates a community chat channel (TECH_STACK type) linked to this tech stack.
     * Duplicate names (case-insensitive) are rejected with 409 Conflict.
     *
     * @param body JSON map containing "name" field
     * @return 200 OK with created AdminTechStackResponse, or 400 if name blank, or 409 if duplicate
     * @apiNote POST /api/admin/techstack
     */
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

    /**
     * Delete a tech stack by ID.
     * <p>
     * If the associated chat channel has messages, it is deactivated (soft-delete)
     * to preserve message history. Otherwise the channel is hard-deleted.
     * Returns a JSON map with "channelDeactivated" boolean.
     *
     * @param id Long ID of the tech stack to delete
     * @return 200 OK with JSON map {"channelDeactivated": true/false}
     * @throws NotFoundException if tech stack not found
     * @apiNote DELETE /api/admin/techstack/{id}
     */
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

    /**
     * Map a TechStack entity to AdminTechStackResponse DTO.
     *
     * @param ts TechStack entity
     * @return AdminTechStackResponse with id and name
     */
    private AdminTechStackResponse toResponse(TechStack ts) {
        return AdminTechStackResponse.builder()
            .id(ts.getId())
            .name(ts.getName())
            .build();
    }
}
