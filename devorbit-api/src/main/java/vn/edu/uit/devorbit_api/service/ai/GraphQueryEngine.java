package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.AiQueryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Answers natural-language questions about the knowledge graph.
 * Uses pattern matching against known question structures.
 * No LLM required — all answers derived from graph data.
 */
@Component
public class GraphQueryEngine {

    private static final List<QueryPattern> PATTERNS = List.of(
        // "Môn nào tiên quyết cho SE104?"
        new QueryPattern("tiên quyết.*cho.*([A-Za-z0-9]{4,8})", QueryType.PREREQUISITE_OF),
        // "SE104 cần tiên quyết gì?"
        new QueryPattern("([A-Za-z0-9]{4,8}).*tiên quyết", QueryType.PREREQUISITES_FOR),
        // "Học sau SE104 có những môn nào?"
        new QueryPattern("học sau.*([A-Za-z0-9]{4,8})", QueryType.DOWNSTREAM_OF),
        // "SE104 học sau môn nào?"
        new QueryPattern("([A-Za-z0-9]{4,8}).*học sau", QueryType.DOWNSTREAM_FOR),
        // "Tác động nếu trượt SE104?"
        new QueryPattern("(?:ảnh hưởng|tác động).*trượt.*([A-Za-z0-9]{4,8})", QueryType.IMPACT_IF_FAIL),
        // "SE104 ảnh hưởng thế nào?"
        new QueryPattern("([A-Za-z0-9]{4,8}).*(?:ảnh hưởng|tác động)", QueryType.IMPACT_IF_FAIL),
        // "SE104 học kỳ mấy?"
        new QueryPattern("([A-Za-z0-9]{4,8}).*học kỳ", QueryType.SEMESTER_OF),
        // "Học kỳ 1 có môn nào?"
        new QueryPattern("học kỳ\\s*(\\d)", QueryType.COURSES_IN_SEMESTER),
        // Tổng quan
        new QueryPattern("(?:tổng|có|liệt kê).*(?:môn|tín chỉ|bao nhiêu)", QueryType.TOTAL_STATS),
        // "Môn SE104 là gì?"
        new QueryPattern("(?:môn|mh|course)\\s*([A-Za-z0-9]{4,8})", QueryType.COURSE_INFO),
        // "SE104 là môn gì?"
        new QueryPattern("([A-Za-z0-9]{4,8}).*(?:là|gì)", QueryType.COURSE_INFO)
    );

    public AiQueryResponse query(String question, KnowledgeGraphResponse graph) {
        String normalized = question.toLowerCase().trim();

        // Try each pattern
        for (QueryPattern qp : PATTERNS) {
            Matcher m = qp.pattern().matcher(normalized);
            if (m.find()) {
                String code = m.groupCount() >= 1 ? m.group(1).toUpperCase() : null;
                return handleQuery(qp.type(), code, graph);
            }
        }

        // Fallback: search course names for keywords
        return fallbackSearch(normalized, graph);
    }

    private AiQueryResponse handleQuery(QueryType type, String code, KnowledgeGraphResponse graph) {
        return switch (type) {
            case PREREQUISITE_OF -> answerPrerequisiteOf(code, graph);
            case PREREQUISITES_FOR -> answerPrerequisitesFor(code, graph);
            case DOWNSTREAM_OF -> answerDownstreamOf(code, graph);
            case DOWNSTREAM_FOR -> answerDownstreamFor(code, graph);
            case IMPACT_IF_FAIL -> answerImpactIfFail(code, graph);
            case SEMESTER_OF -> answerSemesterOf(code, graph);
            case COURSES_IN_SEMESTER -> answerCoursesInSemester(code, graph);
            case COURSE_INFO -> answerCourseInfo(code, graph);
            case TOTAL_STATS -> answerTotalStats(graph);
            default -> unknownQuery();
        };
    }

    private GraphNodeInfo findNodeByCode(String code, KnowledgeGraphResponse graph) {
        return graph.nodes().stream()
            .filter(n -> n.code().equalsIgnoreCase(code))
            .findFirst()
            .map(n -> new GraphNodeInfo(n.id(), n.name(), n.code(), n.description(), n.level(), n.impactScore(), n.semester()))
            .orElse(null);
    }

    private AiQueryResponse answerPrerequisiteOf(String code, KnowledgeGraphResponse graph) {
        GraphNodeInfo node = findNodeByCode(code, graph);
        if (node == null) return notFound(code);

        // Find courses for which this is a prerequisite
        List<GraphNodeInfo> downstream = new ArrayList<>();
        for (KnowledgeGraphResponse.GraphLink link : graph.links()) {
            if (link.type() == CourseRelationType.PREREQUISITE && link.source().equals(node.id())) {
                graph.nodes().stream()
                    .filter(n -> n.id().equals(link.target()))
                    .findFirst()
                    .ifPresent(n -> downstream.add(new GraphNodeInfo(n.id(), n.name(), n.code(), n.description(), n.level(), n.impactScore(), n.semester())));
            }
        }

        if (downstream.isEmpty()) {
            String answer = String.format(
                "**%s (%s)** không phải là môn tiên quyết cho môn nào khác. " +
                "Đây thường là môn cuối trong lộ trình.",
                node.name(), node.code()
            );
            return new AiQueryResponse(answer, List.of(node.id()), "PREREQUISITE");
        }

        String names = downstream.stream()
            .map(n -> String.format("**%s (%s)**", n.name(), n.code()))
            .collect(Collectors.joining(", "));

        String answer = String.format(
            "**%s (%s)** là môn tiên quyết cho **%d môn**: %s.\n\n" +
            "Nếu học tốt môn này, bạn sẽ có nền tảng vững chắc để học các môn sau.",
            node.name(), node.code(), downstream.size(), names
        );

        List<Long> ids = downstream.stream().map(GraphNodeInfo::id).collect(Collectors.toList());
        ids.add(node.id());
        return new AiQueryResponse(answer, ids, "PREREQUISITE");
    }

    private AiQueryResponse answerPrerequisitesFor(String code, KnowledgeGraphResponse graph) {
        GraphNodeInfo node = findNodeByCode(code, graph);
        if (node == null) return notFound(code);

        List<GraphNodeInfo> prereqs = new ArrayList<>();
        for (KnowledgeGraphResponse.GraphLink link : graph.links()) {
            if (link.type() == CourseRelationType.PREREQUISITE && link.target().equals(node.id())) {
                graph.nodes().stream()
                    .filter(n -> n.id().equals(link.source()))
                    .findFirst()
                    .ifPresent(n -> prereqs.add(new GraphNodeInfo(n.id(), n.name(), n.code(), n.description(), n.level(), n.impactScore(), n.semester())));
            }
        }

        if (prereqs.isEmpty()) {
            String answer = String.format(
                "**%s (%s)** không có môn tiên quyết. Đây là môn nền tảng, " +
                "thường được học ở đầu chương trình.",
                node.name(), node.code()
            );
            return new AiQueryResponse(answer, List.of(node.id()), "PREREQUISITE");
        }

        String names = prereqs.stream()
            .map(n -> String.format("**%s (%s)**", n.name(), n.code()))
            .collect(Collectors.joining(", "));

        String answer = String.format(
            "Để học **%s (%s)**, bạn cần hoàn thành các môn tiên quyết sau (**%d môn**): %s.",
            node.name(), node.code(), prereqs.size(), names
        );

        List<Long> ids = prereqs.stream().map(GraphNodeInfo::id).collect(Collectors.toList());
        ids.add(node.id());
        return new AiQueryResponse(answer, ids, "PREREQUISITE");
    }

    private AiQueryResponse answerDownstreamOf(String code, KnowledgeGraphResponse graph) {
        return answerPrerequisiteOf(code, graph);
    }

    private AiQueryResponse answerDownstreamFor(String code, KnowledgeGraphResponse graph) {
        return answerPrerequisitesFor(code, graph);
    }

    private AiQueryResponse answerImpactIfFail(String code, KnowledgeGraphResponse graph) {
        GraphNodeInfo node = findNodeByCode(code, graph);
        if (node == null) return notFound(code);

        int downstreamCount = 0;
        for (KnowledgeGraphResponse.GraphLink link : graph.links()) {
            if (link.type() == CourseRelationType.PREREQUISITE && link.source().equals(node.id())) {
                downstreamCount++;
            }
        }

        String answer;
        double impact = node.impactScore();

        if (impact >= 7.0) {
            answer = String.format(
                "⚠️ **Nguy cơ CAO** — **%s (%s)** có mức ảnh hưởng **%.1f/10**.\n\n" +
                "Nếu trượt môn này, **%d môn** phía sau sẽ bị ảnh hưởng.\n\n" +
                "💡 **Khuyến nghị:** Đầu tư thời gian ôn tập kỹ, tham gia đầy đủ lớp học, " +
                "và làm bài tập thường xuyên để tránh ảnh hưởng dây chuyền.",
                node.name(), node.code(), impact, downstreamCount
            );
        } else if (impact >= 3.0) {
            answer = String.format(
                "📌 **Mức ảnh hưởng trung bình** — **%s (%s)** có mức ảnh hưởng **%.1f/10**.\n\n" +
                "Nếu trượt sẽ ảnh hưởng đến **%d môn** phía sau. Cần chú ý học tập nghiêm túc.",
                node.name(), node.code(), impact, downstreamCount
            );
        } else {
            answer = String.format(
                "✅ **Ít ảnh hưởng** — **%s (%s)** có mức ảnh hưởng **%.1f/10**.\n\n" +
                "Đây là môn độc lập, không ảnh hưởng nhiều đến các môn khác.",
                node.name(), node.code(), impact
            );
        }

        // Find all downstream nodes for highlighting
        List<Long> relevantIds = new ArrayList<>();
        relevantIds.add(node.id());
        for (KnowledgeGraphResponse.GraphLink link : graph.links()) {
            if (link.type() == CourseRelationType.PREREQUISITE && link.source().equals(node.id())) {
                relevantIds.add(link.target());
            }
        }

        return new AiQueryResponse(answer, relevantIds, "IMPACT");
    }

    private AiQueryResponse answerSemesterOf(String code, KnowledgeGraphResponse graph) {
        GraphNodeInfo node = findNodeByCode(code, graph);
        if (node == null) return notFound(code);

        Integer semester = node.semester();
        if (semester == null) {
            return new AiQueryResponse(
                String.format("**%s (%s)** chưa được xếp vào học kỳ nào.", node.name(), node.code()),
                List.of(node.id()), "SEMESTER"
            );
        }

        String answer = String.format(
            "**%s (%s)** thuộc **Học kỳ %d** trong chương trình KTPM 2025.",
            node.name(), node.code(), semester
        );
        return new AiQueryResponse(answer, List.of(node.id()), "SEMESTER");
    }

    private AiQueryResponse answerCoursesInSemester(String semesterStr, KnowledgeGraphResponse graph) {
        int semester = Integer.parseInt(semesterStr);

        List<GraphNodeInfo> inSemester = graph.nodes().stream()
            .filter(n -> n.semester() != null && n.semester() == semester)
            .map(n -> new GraphNodeInfo(n.id(), n.name(), n.code(), n.description(), n.level(), n.impactScore(), n.semester()))
            .sorted(Comparator.comparing(GraphNodeInfo::code))
            .toList();

        if (inSemester.isEmpty()) {
            return new AiQueryResponse(
                String.format("Không có môn nào trong Học kỳ %d.", semester),
                List.of(), "SEMESTER"
            );
        }

        String names = inSemester.stream()
            .map(n -> String.format("- **%s** (%s)", n.name(), n.code()))
            .collect(Collectors.joining("\n"));

        String answer = String.format(
            "📚 **Học kỳ %d** có **%d môn**:\n\n%s",
            semester, inSemester.size(), names
        );

        List<Long> ids = inSemester.stream().map(GraphNodeInfo::id).toList();
        return new AiQueryResponse(answer, ids, "SEMESTER");
    }

    private AiQueryResponse answerCourseInfo(String code, KnowledgeGraphResponse graph) {
        GraphNodeInfo node = findNodeByCode(code, graph);
        if (node == null) return notFound(code);

        String answer = String.format(
            "📖 **%s** (Mã: **%s**)\n\n" +
            "**Học kỳ:** %d\n" +
            "**Mức ảnh hưởng:** %.1f/10\n" +
            "**Cấp độ topo:** %d\n\n" +
            "**Mô tả:** %s",
            node.name(), node.code(),
            node.semester() != null ? node.semester() : "Chưa xác định",
            node.impactScore(),
            node.level(),
            node.description() != null ? node.description() : "Chưa có mô tả."
        );
        return new AiQueryResponse(answer, List.of(node.id()), "PREREQUISITE");
    }

    private AiQueryResponse answerTotalStats(KnowledgeGraphResponse graph) {
        int total = graph.nodes().size();
        long electiveCount = graph.nodes().stream()
            .filter(n -> n.electiveGroup() != null)
            .count();
        long mandatoryCount = total - electiveCount;
        int prereqLinks = (int) graph.links().stream()
            .filter(l -> l.type() == CourseRelationType.PREREQUISITE)
            .count();

        // Calculate average impact score
        double avgImpact = graph.nodes().stream()
            .mapToDouble(KnowledgeGraphResponse.GraphNode::impactScore)
            .average()
            .orElse(0.0);

        // Highest impact course
        KnowledgeGraphResponse.GraphNode highestImpact = graph.nodes().stream()
            .max(Comparator.comparingDouble(KnowledgeGraphResponse.GraphNode::impactScore))
            .orElse(null);

        String answer = String.format(
            "📊 **Thống kê Knowledge Graph (KTPM 2025)**\n\n" +
            "**Tổng số môn:** %d\n" +
            "  - Môn bắt buộc: %d\n" +
            "  - Môn tự chọn: %d\n" +
            "**Số quan hệ tiên quyết:** %d\n" +
            "**Ảnh hưởng trung bình:** %.1f/10\n" +
            "%s",
            total, mandatoryCount, electiveCount,
            prereqLinks,
            avgImpact,
            highestImpact != null ?
                String.format("**Môn ảnh hưởng nhất:** %s (%s) — %.1f/10",
                    highestImpact.name(), highestImpact.code(), highestImpact.impactScore())
                : ""
        );
        return new AiQueryResponse(answer, List.of(), "STATS");
    }

    private AiQueryResponse fallbackSearch(String question, KnowledgeGraphResponse graph) {
        // Extract words that might be course names
        Set<String> words = Set.of(question.split("\\s+"));

        List<GraphNodeInfo> matches = graph.nodes().stream()
            .filter(n -> {
                String name = n.name().toLowerCase();
                return words.stream().anyMatch(w -> name.contains(w) && w.length() > 2);
            })
            .map(n -> new GraphNodeInfo(n.id(), n.name(), n.code(), n.description(), n.level(), n.impactScore(), n.semester()))
            .toList();

        if (matches.isEmpty()) {
            return unknownQuery();
        }

        String names = matches.stream()
            .limit(5)
            .map(n -> String.format("- **%s** (%s)", n.name(), n.code()))
            .collect(Collectors.joining("\n"));

        String answer = String.format(
            "🔍 Tìm thấy **%d môn** liên quan đến câu hỏi của bạn:\n\n%s\n\n" +
            "💡 Bạn có thể hỏi cụ thể hơn: \"Môn [mã] học kỳ mấy?\", \"Môn [mã] tiên quyết gì?\"",
            matches.size(), names
        );

        List<Long> ids = matches.stream().map(GraphNodeInfo::id).toList();
        return new AiQueryResponse(answer, ids, "UNKNOWN");
    }

    private AiQueryResponse notFound(String code) {
        return new AiQueryResponse(
            String.format("❌ Không tìm thấy môn **%s** trong knowledge graph.", code),
            List.of(), "UNKNOWN"
        );
    }

    private AiQueryResponse unknownQuery() {
        return new AiQueryResponse(
            "🤔 **Xin lỗi, tôi chưa hiểu câu hỏi của bạn.**\n\n" +
            "Bạn có thể thử các câu hỏi sau:\n" +
            "- \"SE104 học kỳ mấy?\"\n" +
            "- \"Môn nào tiên quyết cho SE104?\"\n" +
            "- \"Tác động nếu trượt IT001?\"\n" +
            "- \"Học kỳ 1 có môn nào?\"\n" +
            "- \"Tổng quan về chương trình?\"",
            List.of(), "UNKNOWN"
        );
    }

    // --- Inner types ---

    private enum QueryType {
        PREREQUISITE_OF, PREREQUISITES_FOR,
        DOWNSTREAM_OF, DOWNSTREAM_FOR,
        IMPACT_IF_FAIL, SEMESTER_OF,
        COURSES_IN_SEMESTER, COURSE_INFO,
        TOTAL_STATS
    }

    private record QueryPattern(Pattern pattern, QueryType type) {
        QueryPattern(String regex, QueryType type) {
            this(Pattern.compile(regex), type);
        }
    }

    private record GraphNodeInfo(Long id, String name, String code, String description,
                                  int level, double impactScore, Integer semester) {}
}
