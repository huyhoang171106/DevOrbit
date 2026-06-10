package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.admin.CourseRelationshipResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.service.CourseRelationshipService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds rich context strings for LLM prompts using real course data from DB.
 * Used by AI services to provide accurate, grounded responses.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmContextBuilder {

    private final CourseRepository courseRepository;
    private final CourseRelationshipService relationshipService;

    private static final int MAX_CONTEXT_LENGTH = 2000;

    /**
     * Build context for repo summary/advice from GithubRepo + Course data.
     */
    public String buildRepoContext(GithubRepo repo) {
        if (repo == null) return "";

        Course course = repo.getCourse();
        if (course == null && repo.getSubjectId() != null) {
            course = courseRepository.findByMaMH(repo.getSubjectId()).orElse(null);
        }
        if (course == null) return "";

        StringBuilder sb = new StringBuilder();

        // Course info
        sb.append(String.format("Môn: %s (%s)\n", course.getTenMH(), course.getMaMH()));
        if (course.getDescription() != null && !course.getDescription().isBlank()) {
            sb.append(String.format("Mô tả: %s\n", truncate(course.getDescription(), 200)));
        }
        if (course.getSemester() != null) {
            sb.append(String.format("Học kỳ: %d\n", course.getSemester()));
        }
        if (course.getSoTC() > 0) {
            sb.append(String.format("Tín chỉ: %d\n", course.getSoTC()));
        }

        // Learning objectives
        if (course.getLearningObjectives() != null && !course.getLearningObjectives().isBlank()) {
            sb.append(String.format("Mục tiêu học tập: %s\n", truncate(course.getLearningObjectives(), 300)));
        }

        // Topics (JSONB)
        if (course.getTopics() != null && !course.getTopics().isNull()) {
            String topicsStr = parseTopics(course.getTopics());
            if (!topicsStr.isBlank()) {
                sb.append(String.format("Chủ đề: %s\n", topicsStr));
            }
        }

        // Prerequisites
        String prerequisites = getPrerequisiteNames(course);
        if (!prerequisites.isBlank()) {
            sb.append(String.format("Tiên quyết: %s\n", prerequisites));
        }

        // Tech stacks from repo
        if (repo.getTechStacks() != null && !repo.getTechStacks().isEmpty()) {
            String techStacks = repo.getTechStacks().stream()
                    .map(ts -> ts.getName())
                    .collect(Collectors.joining(", "));
            sb.append(String.format("Công nghệ: %s\n", techStacks));
        }

        // Repo description
        if (repo.getDescription() != null && !repo.getDescription().isBlank()) {
            sb.append(String.format("Mô tả repo: %s\n", truncate(repo.getDescription(), 200)));
        }

        return truncate(sb.toString(), MAX_CONTEXT_LENGTH);
    }

    /**
     * Build context for chat from course code.
     */
    public String buildCourseContext(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) return "";

        Course course = courseRepository.findByMaMH(courseCode).orElse(null);
        if (course == null) return "";

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Môn: %s (%s)\n", course.getTenMH(), course.getMaMH()));
        if (course.getDescription() != null && !course.getDescription().isBlank()) {
            sb.append(String.format("Mô tả: %s\n", truncate(course.getDescription(), 300)));
        }
        if (course.getLearningObjectives() != null && !course.getLearningObjectives().isBlank()) {
            sb.append(String.format("Mục tiêu: %s\n", truncate(course.getLearningObjectives(), 300)));
        }
        if (course.getGradingCriteria() != null && !course.getGradingCriteria().isBlank()) {
            sb.append(String.format("Đánh giá: %s\n", truncate(course.getGradingCriteria(), 200)));
        }
        if (course.getTopics() != null && !course.getTopics().isNull()) {
            String topicsStr = parseTopics(course.getTopics());
            if (!topicsStr.isBlank()) {
                sb.append(String.format("Chủ đề: %s\n", topicsStr));
            }
        }

        String prerequisites = getPrerequisiteNames(course);
        if (!prerequisites.isBlank()) {
            sb.append(String.format("Tiên quyết: %s\n", prerequisites));
        }

        return truncate(sb.toString(), MAX_CONTEXT_LENGTH);
    }

    /**
     * Build context for knowledge graph queries.
     */
    public String buildQueryContext(String question, KnowledgeGraphResponse graph) {
        if (graph == null || graph.nodes() == null) return "";

        StringBuilder sb = new StringBuilder();

        // Course list with codes and names
        sb.append("Danh sách môn học:\n");
        for (KnowledgeGraphResponse.GraphNode node : graph.nodes()) {
            sb.append(String.format("- %s: %s (HK%s)\n",
                    node.code(), node.name(),
                    node.semester() != null ? node.semester() : "?"));
        }

        // Prerequisite relationships
        List<KnowledgeGraphResponse.GraphLink> prereqs = graph.links().stream()
                .filter(l -> l.type() == CourseRelationType.PREREQUISITE)
                .toList();
        if (!prereqs.isEmpty()) {
            sb.append("\nMối quan hệ tiên quyết:\n");
            for (KnowledgeGraphResponse.GraphLink link : prereqs) {
                String sourceCode = findNodeCode(link.source(), graph);
                String targetCode = findNodeCode(link.target(), graph);
                if (sourceCode != null && targetCode != null) {
                    sb.append(String.format("- %s là tiên quyết của %s\n", sourceCode, targetCode));
                }
            }
        }

        return truncate(sb.toString(), MAX_CONTEXT_LENGTH);
    }

    private String getPrerequisiteNames(Course course) {
        if (course.getPrerequisiteMH() == null || course.getPrerequisiteMH().isBlank()) {
            return "";
        }
        // prerequisiteMH is a comma-separated string of course codes
        String[] codes = course.getPrerequisiteMH().split(",");
        StringBuilder sb = new StringBuilder();
        for (String code : codes) {
            String trimmed = code.trim();
            if (!trimmed.isBlank()) {
                courseRepository.findByMaMH(trimmed).ifPresent(prereq ->
                        sb.append(String.format("%s (%s), ", prereq.getTenMH(), trimmed)));
            }
        }
        // Remove trailing comma
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    private String parseTopics(JsonNode topics) {
        if (topics == null || topics.isNull() || topics.isEmpty()) return "";
        if (topics.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode topic : topics) {
                if (topic.isTextual()) {
                    sb.append(topic.asText()).append(", ");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return topics.toString();
    }

    private String findNodeCode(Long nodeId, KnowledgeGraphResponse graph) {
        return graph.nodes().stream()
                .filter(n -> n.id().equals(nodeId))
                .map(KnowledgeGraphResponse.GraphNode::code)
                .findFirst()
                .orElse(null);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
