package vn.edu.uit.devorbit_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.knowledge.Citation;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.ChatResponse;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.AiService;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;
import vn.edu.uit.devorbit_api.service.ai.ChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicAiController.class)
class PublicAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiService aiService;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private StudentUserRepository studentUserRepository;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RevokedTokenStore revokedTokenStore;

    @MockitoBean
    private CacheManager cacheManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void chat_factQuery_returnsAnswerWithSources() throws Exception {
        ChatResponse expectedResponse = new ChatResponse(
            UUID.randomUUID(),
            "IT003 có 4 tín chỉ", List.of("data.sql"),
            LocalDateTime.now());
        when(chatService.sendMessage(any())).thenReturn(expectedResponse);

        ChatRequest request = new ChatRequest(null, "IT003 mấy tín chỉ?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("IT003 có 4 tín chỉ"))
            .andExpect(jsonPath("$.sources").isArray())
            .andExpect(jsonPath("$.sources[0]").value("data.sql"));
    }

    @Test
    void chat_ragQuery_returnsStructuredCitations() throws Exception {
        ChatResponse expectedResponse = new ChatResponse(
            UUID.randomUUID(),
            "Quy hoạch động nằm ở Session 5-6.",
            List.of("syllabus.md"), LocalDateTime.now());
        when(chatService.sendMessage(any())).thenReturn(expectedResponse);

        ChatRequest request = new ChatRequest(null, "Quy hoạch động trong IT003 nằm phần nào?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Quy hoạch động nằm ở Session 5-6."))
            .andExpect(jsonPath("$.sources[0]").value("syllabus.md"));
    }
}
