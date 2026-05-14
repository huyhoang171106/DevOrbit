package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.*;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.service.ai.*;

/**
 * Facade for all AI-generated content.
 * Delegates to specialized scenario engines that use knowledge graph data,
 * repository metadata, and curriculum rules — no LLM required.
 */
@Service
@RequiredArgsConstructor
public class AiService {

    private final GithubRepoRepository githubRepoRepository;
    private final SummaryGenerator summaryGenerator;
    private final AdviceGenerator adviceGenerator;
    private final RoadmapGenerator roadmapGenerator;
    private final GraphQueryEngine graphQueryEngine;
    private final KnowledgeGraphService knowledgeGraphService;

    /**
     * Generate an AI summary for a GitHub repository.
     */
    public AiResponse getRepoAiSummary(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));
        return summaryGenerator.generateSummary(repo);
    }

    /**
     * Generate AI tutor advice for a GitHub repository.
     */
    public AiResponse getTutorAdvice(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
            .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        // Compute impact score if repo has a linked course
        double impactScore = 0.0;
        int downstreamCount = 0;
        if (repo.getCourse() != null) {
            KnowledgeGraphResponse graph = knowledgeGraphService.getGraph();
            for (KnowledgeGraphResponse.GraphNode node : graph.nodes()) {
                if (node.id().equals(repo.getCourse().getId())) {
                    impactScore = node.impactScore();
                    break;
                }
            }
            for (KnowledgeGraphResponse.GraphLink link : graph.links()) {
                if (link.type() == vn.edu.uit.devorbit_api.entity.CourseRelationType.PREREQUISITE
                    && link.source().equals(repo.getCourse().getId())) {
                    downstreamCount++;
                }
            }
        }

        return adviceGenerator.generateAdvice(repo, impactScore, downstreamCount);
    }

    /**
     * Generate a personalized learning roadmap based on career goals.
     */
    public RoadmapRecommendationResponse generateRoadmap(RoadmapGenerationRequest request) {
        KnowledgeGraphResponse graph = knowledgeGraphService.getGraph();
        return roadmapGenerator.generate(
            request.learningGoals(),
            request.careerPath(),
            graph
        );
    }

    /**
     * Answer a natural-language query about the knowledge graph.
     */
    public AiQueryResponse queryKnowledgeGraph(AiQueryRequest request) {
        KnowledgeGraphResponse graph = knowledgeGraphService.getGraph();
        return graphQueryEngine.query(request.query(), graph);
    }
}
