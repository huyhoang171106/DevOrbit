package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.ChatMessageAdminResponse;
import vn.edu.uit.devorbit_api.dto.admin.ChatSessionAdminResponse;
import vn.edu.uit.devorbit_api.entity.ChatSession;
import vn.edu.uit.devorbit_api.repository.ChatMessageRepository;
import vn.edu.uit.devorbit_api.repository.ChatSessionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Admin controller for managing chat support sessions.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/chat/sessions - List all chat sessions with message counts</li>
 *   <li>GET /api/admin/chat/sessions/{id}/messages - List messages in a session</li>
 * </ul>
 * <p>
 * Security: ADMIN role required. Read-only by default (transactional).
 * Request flow: Admin views sessions, then drills into individual session messages.
 */
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatController {

    private final ChatSessionRepository chatSessionRepo;
    private final ChatMessageRepository chatMessageRepo;

    /**
     * List all chat sessions with their message counts.
     * <p>
     * Returns each session with student name, title, message count, and creation timestamp.
     *
     * @return 200 OK with list of ChatSessionAdminResponse
     * @apiNote GET /api/admin/chat/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionAdminResponse>> listSessions() {
        return ResponseEntity.ok(chatSessionRepo.findAllWithStudentAndCount()
            .stream().map(row -> {
                ChatSession s = (ChatSession) row[0];
                long count = (row[1] != null) ? (Long) row[1] : 0L;
                return ChatSessionAdminResponse.builder()
                    .id(s.getId())
                    .studentName(s.getStudent() != null ? s.getStudent().getFullName() : "Unknown")
                    .title(s.getTitle())
                    .messageCount(count)
                    .createdAt(s.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList()));
    }

    /**
     * List all messages in a specific chat session, ordered by creation time ascending.
     *
     * @param id UUID of the chat session
     * @return 200 OK with list of ChatMessageAdminResponse
     * @apiNote GET /api/admin/chat/sessions/{id}/messages
     */
    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<List<ChatMessageAdminResponse>> listMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(chatMessageRepo.findBySessionIdOrderByCreatedAtAsc(id)
            .stream().map(m -> ChatMessageAdminResponse.builder()
                .id(m.getId())
                .sender(m.getSender())
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build())
            .collect(Collectors.toList()));
    }
}
