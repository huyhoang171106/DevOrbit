package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.event.RelationshipChangedEvent;
import vn.edu.uit.devorbit_api.constant.CurriculumConstants;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeGraphService {

    private final CourseService courseService;
    private final CourseRelationshipService relationshipService;



    @Cacheable(value = "knowledgeGraph", unless = "#result.nodes.isEmpty()")
    public KnowledgeGraphResponse getGraph() {
        log.info("Generating knowledge graph...");
        List<CourseSummaryResponse> courses = courseService.getActiveCourseSummaries();
        List<CourseRelationshipResponse> relationships = relationshipService.getAll();
        
        log.info("Loaded {} courses and {} relationships", courses.size(), relationships.size());

        // Filter to only mandatory courses from the KTPM curriculum (Khoá 20-2025)
        // Elective placeholders (TCN*, CDTN) are handled by LLM separately
        List<CourseSummaryResponse> mandatory = courses.stream()
            .filter(c -> CurriculumConstants.MANDATORY_CODES.contains(c.code()))
            .collect(Collectors.toList());

        log.info("Filtered to {} mandatory KTPM courses", mandatory.size());

        // Build all nodes
        List<KnowledgeGraphResponse.GraphNode> nodes = new ArrayList<>();

        for (CourseSummaryResponse c : mandatory) {
            // Use semester from curriculum plan; fall back to DB value
            Integer semester = CurriculumConstants.CURRICULUM_SEMESTERS.getOrDefault(c.code(), c.semester());
            if (semester == null) semester = 1;

            nodes.add(new KnowledgeGraphResponse.GraphNode(
                c.id(), c.name(), c.code(), c.description(),
                12.0 + (c.repoCount() != null ? c.repoCount() * 1.5 : 0),
                0, 0.0, semester, null
            ));
        }

        // Build links from all relationships
        // For PREREQUISITE, the edge direction is: prerequisite → course
        // (prerequisite unlocks the course in the knowledge graph)
        List<KnowledgeGraphResponse.GraphLink> links = relationships.stream()
            .map(r -> {
                if (r.relationType() == CourseRelationType.PREREQUISITE) {
                    // PREREQUISITE: source=prerequisite, target=course
                    return new KnowledgeGraphResponse.GraphLink(
                        r.relatedCourseId(), r.courseId(), r.relationType()
                    );
                }
                // COMPLEMENTARY / COREQUISITE: non-directional, keep as-is
                return new KnowledgeGraphResponse.GraphLink(
                    r.courseId(), r.relatedCourseId(), r.relationType()
                );
            })
            .collect(Collectors.toList());

        // Calculate topological levels
        Map<Long, Integer> levels = calculateLevels(nodes, links);

        // Calculate Impact Scores
        Map<Long, Double> impactScores = calculateImpactScores(nodes, links);

        // Attach computed levels and scores
        List<KnowledgeGraphResponse.GraphNode> nodesWithMetadata = nodes.stream()
            .map(n -> new KnowledgeGraphResponse.GraphNode(
                n.id(), n.name(), n.code(), n.description(), n.val(),
                levels.getOrDefault(n.id(), 0),
                impactScores.getOrDefault(n.id(), 0.0),
                n.semester(),
                n.electiveGroup()
            ))
            .collect(Collectors.toList());

        return new KnowledgeGraphResponse(nodesWithMetadata, links);
    }

    @CacheEvict(value = "knowledgeGraph", allEntries = true)
    public void evictGraphCache() {}

    @EventListener
    public void onRelationshipChanged(RelationshipChangedEvent event) {
        evictGraphCache();
    }

    // --- level / impact score calculations ---

    private Map<Long, Integer> calculateLevels(
            List<KnowledgeGraphResponse.GraphNode> nodes,
            List<KnowledgeGraphResponse.GraphLink> links) {

        Map<Long, Integer> inDegree = new HashMap<>();
        Map<Long, List<Long>> adjacency = new HashMap<>();

        for (KnowledgeGraphResponse.GraphNode node : nodes) {
            inDegree.put(node.id(), 0);
            adjacency.put(node.id(), new ArrayList<>());
        }

        for (KnowledgeGraphResponse.GraphLink link : links) {
            if (link.type() != CourseRelationType.PREREQUISITE) continue;
            if (!adjacency.containsKey(link.source()) || !adjacency.containsKey(link.target())) continue;
            adjacency.get(link.source()).add(link.target());
            inDegree.merge(link.target(), 1, Integer::sum);
        }

        Queue<Long> queue = new LinkedList<>();
        Map<Long, Integer> levels = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
                levels.put(entry.getKey(), 0);
            }
        }

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

    private Map<Long, Double> calculateImpactScores(
            List<KnowledgeGraphResponse.GraphNode> nodes,
            List<KnowledgeGraphResponse.GraphLink> links) {

        Map<Long, List<Long>> adjacency = new HashMap<>();
        Map<Long, Integer> outDegree = new HashMap<>();

        for (KnowledgeGraphResponse.GraphNode node : nodes) {
            adjacency.put(node.id(), new ArrayList<>());
            outDegree.put(node.id(), 0);
        }

        for (KnowledgeGraphResponse.GraphLink link : links) {
            if (link.type() != CourseRelationType.PREREQUISITE) continue;
            if (!adjacency.containsKey(link.source()) || !adjacency.containsKey(link.target())) continue;
            adjacency.get(link.source()).add(link.target());
            outDegree.merge(link.source(), 1, Integer::sum);
        }

        Map<Long, Double> rawScores = new HashMap<>();
        double maxRaw = 0.1;

        for (KnowledgeGraphResponse.GraphNode node : nodes) {
            int unlockedCount = countReachable(node.id(), adjacency);
            int depth = calculateMaxDepth(node.id(), adjacency, new HashMap<>());
            int bottleneck = outDegree.getOrDefault(node.id(), 0);

            double score = (unlockedCount * 0.4) + (depth * 0.3) + (bottleneck * 0.3);
            rawScores.put(node.id(), score);
            maxRaw = Math.max(maxRaw, score);
        }

        Map<Long, Double> normalized = new HashMap<>();
        for (Map.Entry<Long, Double> entry : rawScores.entrySet()) {
            normalized.put(entry.getKey(), (entry.getValue() / maxRaw) * 10.0);
        }

        return normalized;
    }

    private int countReachable(Long startNodeId, Map<Long, List<Long>> adjacency) {
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(startNodeId);
        visited.add(startNodeId);

        int count = 0;
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            for (Long neighbor : adjacency.getOrDefault(current, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    count++;
                }
            }
        }
        return count;
    }

    private int calculateMaxDepth(Long nodeId, Map<Long, List<Long>> adjacency, Map<Long, Integer> memo) {
        return calculateMaxDepthRecursive(nodeId, adjacency, memo, new HashSet<>());
    }

    private int calculateMaxDepthRecursive(Long nodeId, Map<Long, List<Long>> adjacency, Map<Long, Integer> memo, Set<Long> visitedInPath) {
        if (memo.containsKey(nodeId)) return memo.get(nodeId);
        if (visitedInPath.contains(nodeId)) return 0; // Cycle detected

        visitedInPath.add(nodeId);
        int max = 0;
        for (Long neighbor : adjacency.getOrDefault(nodeId, List.of())) {
            max = Math.max(max, 1 + calculateMaxDepthRecursive(neighbor, adjacency, memo, visitedInPath));
        }
        visitedInPath.remove(nodeId);

        memo.put(nodeId, max);
        return max;
    }
}
