package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

/**
 * AI QUERY RESPONSE = the AI's answer to a knowledge graph query.
 *
 * The `type` field tells the frontend HOW to render the answer:
 *   PREREQUISITE  → "You need to take these courses first"
 *   DOWNSTREAM    → "These courses depend on this one"
 *   IMPACT        → "Changing this affects these courses"
 *   SEMESTER      → "Recommended courses for this semester"
 *   STATS         → "Course statistics/data"
 *   UNKNOWN       → "Couldn't understand the question"
 *
 * relevantNodeIds are course IDs that the frontend can highlight
 * on the knowledge graph visualization.
 */
public record AiQueryResponse(
    String answer,               // AI-generated text response
    List<Long> relevantNodeIds,  // Course IDs for graph highlighting
    String type                  // Response type enum (as string)
) {}
