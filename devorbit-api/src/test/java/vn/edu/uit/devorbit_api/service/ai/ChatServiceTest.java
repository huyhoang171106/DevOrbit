package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatResponse;
import vn.edu.uit.devorbit_api.entity.ChatMessage;
import vn.edu.uit.devorbit_api.entity.ChatSession;
import vn.edu.uit.devorbit_api.repository.ChatMessageRepository;
import vn.edu.uit.devorbit_api.repository.ChatSessionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private OpenCodeAiService openCodeAiService;
    @Mock private ChatSessionRepository sessionRepository;
    @Mock private ChatMessageRepository messageRepository;
    @Mock private LlmContextBuilder contextBuilder;

    private ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<ChatMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(
            openCodeAiService, sessionRepository, messageRepository,
            objectMapper, contextBuilder);
    }

    @Test
    void sendMessage_llmEnabled_usesContextBuilderAndOpenCodeAiService() {
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(sessionRepository.save(any())).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });
        when(messageRepository.save(any())).thenAnswer(invocation -> {
            ChatMessage m = invocation.getArgument(0);
            return m;
        });
        
        when(contextBuilder.buildCourseContext("IT003")).thenReturn("Context for IT003");
        when(openCodeAiService.generateCompletion(any(), anyString()))
            .thenReturn("AI Answer");

        ChatResponse response = chatService.sendMessage(
            new ChatRequest(null, "IT003 mấy tín chỉ?"));

        assertThat(response.message()).isEqualTo("AI Answer");
        verify(contextBuilder).buildCourseContext("IT003");
        verify(openCodeAiService).generateCompletion(eq(PromptTemplates.CHAT_TUTOR), contains("Context for IT003"));
    }

    @Test
    void sendMessage_llmDisabled_usesOfflineFallback() {
        when(openCodeAiService.isLlmEnabled()).thenReturn(false);
        when(sessionRepository.save(any())).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ChatResponse response = chatService.sendMessage(
            new ChatRequest(null, "Xin chào"));

        assertThat(response.message()).isNotBlank();
        verifyNoInteractions(contextBuilder);
    }

    @Test
    void sendMessage_persistsAiMessage() {
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(sessionRepository.save(any())).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        doAnswer(invocation -> {
            ChatMessage msg = invocation.getArgument(0);
            return msg;
        }).when(messageRepository).save(any());

        when(contextBuilder.buildCourseContext(any())).thenReturn("Test context");
        when(openCodeAiService.generateCompletion(any(), anyString())).thenReturn("Test answer");

        ChatResponse response = chatService.sendMessage(
            new ChatRequest(null, "Test question"));

        assertThat(response.message()).isEqualTo("Test answer");
        verify(messageRepository, atLeast(2)).save(messageCaptor.capture());
        
        List<ChatMessage> savedMessages = messageCaptor.getAllValues();
        assertThat(savedMessages).hasSize(2);
        assertThat(savedMessages.get(0).getSender()).isEqualTo("STUDENT");
        assertThat(savedMessages.get(1).getSender()).isEqualTo("AI");
    }
}
