package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.config.ElectiveGroupConfig;
import vn.edu.uit.devorbit_api.event.RelationshipChangedEvent;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.ElectiveGroupResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeGraphService {

    private final CourseService courseService;
    private final CourseRelationshipService relationshipService;
    private final ElectiveGroupConfig electiveGroupConfig;

    @Cacheable(value = "knowledgeGraph", unless = "#result.nodes.isEmpty()")
    public KnowledgeGraphResponse getGraph() {
        List<CourseSummaryResponse> courses = courseService.getActiveCourseSummaries();
        List<CourseRelationshipResponse> relationships = relationshipService.getAll();

        // Separate mandatory vs elective
        List<CourseSummaryResponse> mandatory = courses.stream()
            .filter(c -> !"TU_CHON".equals(c.loaiMonHoc()) && c.semester() != null)
            .collect(Collectors.toList());
        List<CourseSummaryResponse> elective = courses.stream()
            .filter(c -> "TU_CHON".equals(c.loaiMonHoc()))
            .collect(Collectors.toList());

        // Map elective courses by maMH for group assignment
        Map<String, List<CourseSummaryResponse>> electiveByCode = new HashMap<>();
        for (CourseSummaryResponse ec : elective) {
            electiveByCode.computeIfAbsent(ec.code(), k -> new ArrayList<>()).add(ec);
        }

        // Build mandatory nodes with semester
        List<KnowledgeGraphResponse.GraphNode> nodes = new ArrayList<>();
        for (CourseSummaryResponse c : mandatory) {
            String electiveGroup = null;
            if (c.code().startsWith("TC_CSN")) electiveGroup = "TC_CSN";
            else if (c.code().startsWith("TC_CN")) {
                // Check if it's specialized or general
                if (c.code().startsWith("TC_CN_PM")) electiveGroup = "TC_CN_PM";
                else if (c.code().startsWith("TC_CN_GAME")) electiveGroup = "TC_CN_GAME";
                else electiveGroup = "TC_CN";
            }
            else if (c.code().startsWith("TC_TN")) electiveGroup = "TC_TN";

            nodes.add(new KnowledgeGraphResponse.GraphNode(
                c.id(), c.name(), c.code(), c.description(),
                12.0 + (c.repoCount() != null ? c.repoCount() * 1.5 : 0),
                0, 0.0, c.semester(), electiveGroup
            ));
        }

        // Build elective group nodes (synthetic for groups not represented in DB)
        Map<String, Integer> groupSemesterHint = new HashMap<>();
        groupSemesterHint.put("TC_CSN", 4);
        groupSemesterHint.put("TC_CN", 6);
        groupSemesterHint.put("TC_CN_PM", 6);
        groupSemesterHint.put("TC_CN_GAME", 6);
        groupSemesterHint.put("TC_TN", 8);

        Set<String> processedCodes = new HashSet<>();
        for (var entry : electiveGroupConfig.getGroups().entrySet()) {
            String groupCode = entry.getKey();
            ElectiveGroupConfig.ElectiveGroupDef def = entry.getValue();

            // Skip sub-groups (parentGroupCode != null) — they appear as filters, not independent nodes
            if (def.parentGroupCode() != null) continue;

            // Collect elective course IDs belonging to this group
            List<Long> groupCourseIds = new ArrayList<>();
            for (String maMH : def.courseCodes()) {
                List<CourseSummaryResponse> matched = electiveByCode.getOrDefault(maMH, List.of());
                for (CourseSummaryResponse ec : matched) {
                    if (!processedCodes.contains(ec.code())) {
                        groupCourseIds.add(ec.id());
                        processedCodes.add(ec.code());
                    }
                }
            }

            // Add a synthetic node for the group
            long groupNodeId = -(new Random(Objects.hash(groupCode)).nextInt(9000) + 1000); // deterministic negative ID
            nodes.add(new KnowledgeGraphResponse.GraphNode(
                groupNodeId,
                def.name(),
                groupCode,
                def.description(),
                18.0, // slightly larger node
                0, 0.0,
                groupSemesterHint.getOrDefault(groupCode, 5),
                groupCode
            ));
        }

        // Build links from all relationships
        List<KnowledgeGraphResponse.GraphLink> links = relationships.stream()
            .map(r -> new KnowledgeGraphResponse.GraphLink(
                r.courseId(), r.relatedCourseId(), r.relationType()
            ))
            .collect(Collectors.toList());

        // Also connect group nodes to mandatory nodes where a course in the group
        // is prerequisite of a mandatory course
        for (var entry : electiveGroupConfig.getGroups().entrySet()) {
            ElectiveGroupConfig.ElectiveGroupDef def = entry.getValue();
            if (def.parentGroupCode() != null) continue;

            for (String maMH : def.courseCodes()) {
                List<CourseSummaryResponse> matched = electiveByCode.getOrDefault(maMH, List.of());
                if (matched.isEmpty()) continue;

                List<Long> groupNodeIds = findAllGroupNodeIds(nodes, def.code());
                if (groupNodeIds.isEmpty()) continue;

                for (CourseRelationshipResponse r : relationships) {
                    // If an elective course in this group is prerequisite or complementary to a mandatory course
                    if (r.relationType() == CourseRelationType.PREREQUISITE || r.relationType() == CourseRelationType.COMPLEMENTARY) {
                        for (CourseSummaryResponse ec : matched) {
                            if (r.courseId().equals(ec.id())) {
                                // link source=all matching group nodes, target=the mandatory course
                                for (Long gId : groupNodeIds) {
                                    links.add(new KnowledgeGraphResponse.GraphLink(
                                        gId, r.relatedCourseId(), CourseRelationType.COMPLEMENTARY
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        }

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

    public List<ElectiveGroupResponse> getElectiveGroups() {
        return electiveGroupConfig.getGroups().values().stream()
            .map(def -> new ElectiveGroupResponse(
                def.code(), def.name(), def.description(),
                def.minCredits(), def.parentGroupCode(),
                def.courseCodes().size()
            ))
            .collect(Collectors.toList());
    }

    public List<CourseSummaryResponse> getElectiveGroupCourses(String groupCode) {
        ElectiveGroupConfig.ElectiveGroupDef def = electiveGroupConfig.getGroups().get(groupCode);
        if (def == null) return List.of();

        List<CourseSummaryResponse> all = courseService.getActiveCourseSummaries();
        Set<String> targetCodes = new HashSet<>(def.courseCodes());

        // Include sub-groups (e.g. if TC_CN is requested, include TC_CN_PM and TC_CN_GAME)
        for (var gDef : electiveGroupConfig.getGroups().values()) {
            if (groupCode.equals(gDef.parentGroupCode())) {
                targetCodes.addAll(gDef.courseCodes());
            }
        }

        // If sub-group is requested, include parent group courses too
        if (def.parentGroupCode() != null) {
            ElectiveGroupConfig.ElectiveGroupDef parent = electiveGroupConfig.getGroups().get(def.parentGroupCode());
            if (parent != null) targetCodes.addAll(parent.courseCodes());
        }

        return all.stream()
            .filter(c -> targetCodes.contains(c.code()))
            .collect(Collectors.toList());
    }

    @CacheEvict(value = "knowledgeGraph", allEntries = true)
    public void evictGraphCache() {}

    @EventListener
    public void onRelationshipChanged(RelationshipChangedEvent event) {
        evictGraphCache();
    }

    private List<Long> findAllGroupNodeIds(List<KnowledgeGraphResponse.GraphNode> nodes, String groupCode) {
        return nodes.stream()
            .filter(n -> groupCode.equals(n.electiveGroup()))
            .map(KnowledgeGraphResponse.GraphNode::id)
            .collect(Collectors.toList());
    }

    // --- level / impact score calculations (unchanged) ---

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
        if (memo.containsKey(nodeId)) return memo.get(nodeId);

        int max = 0;
        for (Long neighbor : adjacency.getOrDefault(nodeId, List.of())) {
            max = Math.max(max, 1 + calculateMaxDepth(neighbor, adjacency, memo));
        }

        memo.put(nodeId, max);
        return max;
    }
}
