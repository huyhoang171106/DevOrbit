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

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class PublicAiController {
    private final AiService aiService;

    @GetMapping("/repo/{repoId}/summary")
    public AiResponse getRepoSummary(@PathVariable Long repoId) {
        return aiService.getRepoAiSummary(repoId);
    }

    @GetMapping("/repo/{repoId}/advice")
    public AiResponse getTutorAdvice(@PathVariable Long repoId) {
        return aiService.getTutorAdvice(repoId);
    }

    @PostMapping("/generate-roadmap")
    public RoadmapRecommendationResponse generateRoadmap(@RequestBody @Valid RoadmapGenerationRequest request) {
        return aiService.generateRoadmap(request);
    }

    @PostMapping("/knowledge-graph/query")
    public AiQueryResponse queryKnowledgeGraph(@RequestBody @Valid AiQueryRequest request) {
        return aiService.queryKnowledgeGraph(request);
    }
}
