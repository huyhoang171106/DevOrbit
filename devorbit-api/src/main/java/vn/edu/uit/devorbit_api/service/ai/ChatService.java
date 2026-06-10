package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatResponse;
import vn.edu.uit.devorbit_api.entity.ChatMessage;
import vn.edu.uit.devorbit_api.entity.ChatSession;
import vn.edu.uit.devorbit_api.repository.ChatMessageRepository;
import vn.edu.uit.devorbit_api.repository.ChatSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing AI chat sessions with conversation history.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenCodeAiService openCodeAiService;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final LlmContextBuilder contextBuilder;

    private static final int MAX_HISTORY_MESSAGES = 10;

    /**
     * Send a message and get AI response.
     * Creates new session if sessionId is null.
     */
    @Transactional
    public ChatResponse sendMessage(ChatRequest request) {
        // Get or create session
        ChatSession session = getOrCreateSession(request.sessionId());
        
        // Save user message
        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .sender("STUDENT")
                .content(request.message())
                .build();
        messageRepository.save(userMessage);

        // Build conversation history for context
        String history = buildHistoryContext(session.getId());

        // Generate AI response
        String aiResponse;
        if (openCodeAiService.isLlmEnabled()) {
            // Try to extract course code from message for context
            String courseCode = extractCourseCode(request.message());
            String courseContext = contextBuilder.buildCourseContext(courseCode);
            
            log.debug("Chat RAG context length: {} chars, course: {}", courseContext.length(), courseCode);
            
            String fullContext = String.format(
                "%s\n\nLịch sử:\n%s\n\nCâu hỏi mới: %s",
                courseContext, history, request.message()
            );
            
            aiResponse = openCodeAiService.generateCompletion(
                PromptTemplates.CHAT_TUTOR, fullContext
            );
        } else {
            aiResponse = generateOfflineResponse(request.message());
        }

        // Save AI response
        ChatMessage aiMessage = ChatMessage.builder()
                .session(session)
                .sender("AI")
                .content(aiResponse)
                .sources(objectMapper.createArrayNode())
                .build();
        messageRepository.save(aiMessage);

        // Update session timestamp
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        log.debug("Chat message sent, session: {}", session.getId());

        return new ChatResponse(
                session.getId(),
                aiResponse,
                List.of(),
                aiMessage.getCreatedAt()
        );
    }

    /**
     * Get chat history for a session.
     */
    public List<ChatResponse> getHistory(UUID sessionId) {
        List<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        
        return messages.stream()
                .map(m -> new ChatResponse(
                        sessionId,
                        m.getContent(),
                        List.of(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    private ChatSession getOrCreateSession(UUID sessionId) {
        if (sessionId != null) {
            return sessionRepository.findById(sessionId)
                    .orElseGet(() -> createNewSession());
        }
        return createNewSession();
    }

    private ChatSession createNewSession() {
        ChatSession session = ChatSession.builder()
                .title("AI Chat Session")
                .build();
        return sessionRepository.save(session);
    }

    private String buildHistoryContext(UUID sessionId) {
        List<ChatMessage> recentMessages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        
        // Take only last N messages
        int startIndex = Math.max(0, recentMessages.size() - MAX_HISTORY_MESSAGES);
        List<ChatMessage> lastMessages = recentMessages.subList(startIndex, recentMessages.size());
        
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : lastMessages) {
            String role = "STUDENT".equals(msg.getSender()) ? "Sinh viên" : "AI Tutor";
            sb.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Extract course code from user message (e.g., "SE104", "SE101").
     */
    private String extractCourseCode(String message) {
        if (message == null) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\\b([A-Z]{2}[0-9]{3})\\b")
                .matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String generateOfflineResponse(String message) {
        String normalized = message.toLowerCase();
        
        if (normalized.contains("xin chào") || normalized.contains("hello") || normalized.contains("hi")) {
            return "Xin chào! Mình là DevOrbit AI Tutor. Mình có thể giúp bạn với các câu hỏi về khóa học, lời khuyên học tập, và hướng dẫn nghề nghiệp. Bạn cần hỗ trợ gì?";
        }
        
        if (normalized.contains("giải tích") || normalized.contains("calculus")) {
            return "Giải tích là môn học quan trọng trong chương trình UIT. Bạn nên:\n\n" +
                   "1. Ôn lại kiến thức về giới hạn, đạo hàm, và tích phân\n" +
                   "2. Thực hành giải bài tập thường xuyên\n" +
                   "3. Sử dụng tài liệu tham khảo từ thư viện\n\n" +
                   "Bạn có câu hỏi cụ thể nào không?";
        }
        
        if (normalized.contains("lộ trình") || normalized.contains("roadmap")) {
            return "Để xem lộ trình học tập phù hợp với định hướng nghề nghiệp của bạn, hãy truy cập trang 'Lộ Trình Học Tập' trên DevOrbit. Hệ thống sẽ gợi ý các môn học dựa trên mục tiêu của bạn.";
        }
        
        return "Cảm ơn bạn đã hỏi! Mình là AI Tutor đang chạy ở chế độ offline. " +
               "Để trải nghiệm đầy đủ, vui lòng cấu hình API Key trong hệ thống. " +
               "Bạn có thể thử hỏi về:\n" +
               "- Các môn học trong chương trình\n" +
               "- Lời khuyên học tập\n" +
               "- Lộ trình nghề nghiệp";
    }
}
