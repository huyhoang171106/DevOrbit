package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.publicapi.AiQueryRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.AiQueryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RoadmapGenerationRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RoadmapRecommendationResponse;
import vn.edu.uit.devorbit_api.service.AiService;

/**
 * PUBLIC AI CONTROLLER = AI-powered features.
 *
 * Provides endpoints for:
 * - AI-generated repo summaries
 * - Tutor advice on specific repos
 * - Personalized learning roadmap generation
 * - Natural language queries against the knowledge graph
 *
 * These use various AI services (AdviceGenerator, SummaryGenerator,
 * RoadmapGenerator, GraphQueryEngine) via AiService.
 *
 * No authentication required.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class PublicAiController {
    private final AiService aiService;

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
}
