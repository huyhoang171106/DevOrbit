package vn.edu.uit.devorbit_api.dto.publicapi;

import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.util.List;

public record KnowledgeGraphResponse(
    List<GraphNode> nodes,
    List<GraphLink> links
) {
    public record GraphNode(Long id, String name, String code, String description, double val, int level, double impactScore, Integer semester, String electiveGroup) {}
    public record GraphLink(Long source, Long target, CourseRelationType type) {}
}
