package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.event.RelationshipChangedEvent;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeGraphService {

    private final CourseService courseService;
    private final CourseRelationshipService relationshipService;

    @Cacheable(value = "knowledgeGraph", unless = "#result.nodes.isEmpty()")
    public KnowledgeGraphResponse getGraph() {
        List<CourseSummaryResponse> courses = courseService.getActiveCourseSummaries();
        List<CourseRelationshipResponse> relationships = relationshipService.getAll();

        // Build nodes with default level 0
        List<KnowledgeGraphResponse.GraphNode> nodes = courses.stream()
            .map(c -> new KnowledgeGraphResponse.GraphNode(
                c.id(), c.name(), c.code(),
                12.0 + (c.repoCount() != null ? c.repoCount() * 1.5 : 0),
                0
            ))
            .collect(Collectors.toList());

        // Build links from all relationships
        List<KnowledgeGraphResponse.GraphLink> links = relationships.stream()
            .map(r -> new KnowledgeGraphResponse.GraphLink(
                r.courseId(), r.relatedCourseId(), r.relationType()
            ))
            .collect(Collectors.toList());

        // Calculate topological levels using Kahn's algorithm (BFS) — O(V + E)
        Map<Long, Integer> levels = calculateLevels(nodes, links);

        // Attach computed levels to nodes
        List<KnowledgeGraphResponse.GraphNode> nodesWithLevels = nodes.stream()
            .map(n -> new KnowledgeGraphResponse.GraphNode(
                n.id(), n.name(), n.code(), n.val(),
                levels.getOrDefault(n.id(), 0)
            ))
            .collect(Collectors.toList());

        return new KnowledgeGraphResponse(nodesWithLevels, links);
    }

    /**
     * Evict the knowledge graph cache whenever course relationships change.
     */
    @CacheEvict(value = "knowledgeGraph", allEntries = true)
    public void evictGraphCache() {
        // Intentionally empty — the @CacheEvict annotation handles cache invalidation
    }

    @EventListener
    public void onRelationshipChanged(RelationshipChangedEvent event) {
        evictGraphCache();
    }

    /**
     * Kahn's algorithm (BFS-based) for topological sorting.
     * Only PREREQUISITE edges affect level propagation.
     * Time complexity: O(V + E) vs the previous brute-force O(10 * E).
     */
    private Map<Long, Integer> calculateLevels(
            List<KnowledgeGraphResponse.GraphNode> nodes,
            List<KnowledgeGraphResponse.GraphLink> links) {

        Map<Long, Integer> inDegree = new HashMap<>();
        Map<Long, List<Long>> adjacency = new HashMap<>();

        // Initialize adjacency and in-degree for every node
        for (KnowledgeGraphResponse.GraphNode node : nodes) {
            inDegree.put(node.id(), 0);
            adjacency.put(node.id(), new ArrayList<>());
        }

        // Build directed graph from PREREQUISITE edges only.
        // Edge source -> target means "source is prerequisite of target"
        // So target's level > source's level.
        // Guard against null adjacency when a link references a course ID
        // that is not in the active courses set (inactive/deleted courses).
        for (KnowledgeGraphResponse.GraphLink link : links) {
            if (link.type() != CourseRelationType.PREREQUISITE) continue;
            if (!adjacency.containsKey(link.source()) || !adjacency.containsKey(link.target())) continue;
            adjacency.get(link.source()).add(link.target());
            inDegree.merge(link.target(), 1, Integer::sum);
        }

        // Seed queue with nodes that have no prerequisites (in-degree = 0)
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Integer> levels = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
                levels.put(entry.getKey(), 0);
            }
        }

        // Process queue level by level
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            int currentLevel = levels.get(current);

            for (Long neighbor : adjacency.getOrDefault(current, List.of())) {
                int newLevel = currentLevel + 1;
                levels.put(neighbor, Math.max(levels.getOrDefault(neighbor, 0), newLevel));

                int updatedInDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, updatedInDegree);
                if (updatedInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return levels;
    }
}
