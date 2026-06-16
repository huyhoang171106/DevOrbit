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

@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatController {

    private final ChatSessionRepository chatSessionRepo;
    private final ChatMessageRepository chatMessageRepo;

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
