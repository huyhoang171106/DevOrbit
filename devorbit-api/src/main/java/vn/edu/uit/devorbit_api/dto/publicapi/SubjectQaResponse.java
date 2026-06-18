package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SUBJECT QA RESPONSE = AI's answer to a student's course question.
 *
 * This is a MULTI-MODAL response — it can include:
 *   - A text answer (Markdown format)
 *   - Course node IDs for the knowledge graph (to highlight)
 *   - Source citations (URLs, course codes)
 *   - Web search results (supplementary info)
 *   - A roadmap recommendation (if question is about study planning)
 *   - Suggested follow-up questions
 *   - Confidence score (0.0-1.0) for the answer
 *
 * The `type` field tells the frontend how to render:
 *   "text"       → just show the answer text
 *   "roadmap"    → also show the roadmap widget
 *   "search"     → also show web search results
 *   "graph"      → highlight courses on the knowledge graph
 *
 * sessionId: used to continue the conversation (pass in next request).
 */
public record SubjectQaResponse(
    String answer,                                        // AI-generated answer (Markdown)
    UUID sessionId,                                       // Session for follow-up
    List<Long> relevantNodeIds,                           // Course IDs for graph highlight
    List<String> sources,                                 // Source citations
    String type,                                          // Response render type
    List<WebSearchResponse.WebSearchResult> searchResults, // Web search results
    RoadmapRecommendationResponse roadmap,                // Optional roadmap plan
    @JsonProperty("suggestedFollowUps")
    List<String> suggestedFollowUps,                      // AI-suggested next questions
    @JsonProperty("confidenceScore")
    Double confidenceScore                                // 0.0-1.0 confidence
) {}
