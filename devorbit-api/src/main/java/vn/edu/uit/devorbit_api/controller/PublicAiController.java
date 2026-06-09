package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.service.AiService;
import vn.edu.uit.devorbit_api.service.ai.ChatService;

import java.util.List;
import java.util.UUID;

/**
 * PUBLIC AI CONTROLLER = AI-powered features.
 *
 * Provides endpoints for:
 * - AI-generated repo summaries
 * - Tutor advice on specific repos
 * - Personalized learning roadmap generation
 * - Natural language queries against the knowledge graph
 * - Conversational AI chat
 *
 * These use various AI services (AdviceGenerator, SummaryGenerator,
 * RoadmapGenerator, GraphQueryEngine, ChatService) via AiService.
 *
 * No authentication required.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class PublicAiController {
    private final AiService aiService;
    private final ChatService chatService;

    /** Get an AI-generated summary of a GitHub repo */
    @GetMapping("/repo/{repoId}/summary")
    public AiResponse getRepoSummary(@PathVariable Long repoId) {
        return aiService.getRepoAiSummary(repoId);
    }

    /** Get AI tutor advice/explanation for a specific repo */
    @GetMapping("/repo/{repoId}/advice")
    public AiResponse getTutorAdvice(@PathVariable Long repoId) {
        return aiService.getTutorAdvice(repoId);
    }

    /** Generate a personalized learning roadmap based on student input */
    @PostMapping("/generate-roadmap")
    public RoadmapRecommendationResponse generateRoadmap(@RequestBody @Valid RoadmapGenerationRequest request) {
        return aiService.generateRoadmap(request);
    }

    /** Ask a natural language question about the course knowledge graph */
    @PostMapping("/knowledge-graph/query")
    public AiQueryResponse queryKnowledgeGraph(@RequestBody @Valid AiQueryRequest request) {
        return aiService.queryKnowledgeGraph(request);
    }

    /** Send a chat message and get AI response */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody @Valid ChatRequest request) {
        return chatService.sendMessage(request);
    }

    /** Get chat history for a session */
    @GetMapping("/chat/{sessionId}/history")
    public List<ChatResponse> getChatHistory(@PathVariable UUID sessionId) {
        return chatService.getHistory(sessionId);
    }
}
