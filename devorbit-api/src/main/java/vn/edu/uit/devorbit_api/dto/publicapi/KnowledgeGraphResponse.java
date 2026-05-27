package vn.edu.uit.devorbit_api.dto.publicapi;

import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.util.List;

/**
 * KNOWLEDGE GRAPH = the data structure for the interactive course map.
 *
 * The knowledge graph is a visual representation of courses and their
 * relationships. It's like a subway map where:
 * - Nodes (stations) = courses
 * - Links (tracks)  = relationships between courses
 *
 * GraphNode fields:
 *   val         = display size (bigger = more repos linked)
 *   level       = topological depth (0 = no prerequisites needed)
 *   impactScore = how many downstream courses this course affects
 *   semester    = recommended semester
 *
 * GraphLink fields:
 *   source = the "from" course ID
 *   target = the "to" course ID
 *   type   = PREREQUISITE, COMPLEMENTARY, or COREQUISITE
 *
 * Used by: GET /api/courses/graph
 */
public record KnowledgeGraphResponse(
    List<GraphNode> nodes,
    List<GraphLink> links
) {
    public record GraphNode(
        Long id,
        String name,
        String code,
        String description,
        double val,           // Display size (bigger = more linked repos)
        int level,            // Topological depth (0 = entry-level)
        double impactScore,   // How important this course is (0-10 scale)
        Integer semester,     // Recommended semester number
        String electiveGroup  // Which elective group (null for mandatory)
    ) {}

    public record GraphLink(
        Long source,               // "From" course ID
        Long target,               // "To" course ID
        CourseRelationType type    // PREREQUISITE, COMPLEMENTARY, COREQUISITE
    ) {}
}
