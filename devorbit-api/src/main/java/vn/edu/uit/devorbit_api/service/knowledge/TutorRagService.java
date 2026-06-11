package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.knowledge.Citation;
import vn.edu.uit.devorbit_api.dto.knowledge.TutorResponse;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;

import java.util.List;
import java.util.Optional;

/**
 * Orchestrates AI Tutor question answering with RAG.
 * Flow: detect course → classify intent → DB fact query OR semantic search + LLM.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TutorRagService {

    private final CourseCodeDetector courseCodeDetector;
    private final TutorIntentClassifier intentClassifier;
    private final CourseFactQueryService courseFactQueryService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final OpenCodeAiService openCodeAiService;
    private final CitationBuilder citationBuilder;

    private static final int DEFAULT_TOP_K = 5;

    private static final String TUTOR_SYSTEM_PROMPT = """
        Bạn là AI Tutor của DevOrbit — nền tảng học tập UIT.
        Trả lời bằng tiếng Việt, ngắn gọn, chính xác.
        Chỉ sử dụng thông tin từ context được cung cấp.
        Nếu context không đủ, nói rõ "Dữ liệu hiện tại chưa đủ để trả lời câu hỏi này."
        KHÔNG BAO GIỜ bịa đặt thông tin về chương trình học.
        Giữ nguyên tên môn học tiếng Việt.
        """;

    /**
     * Answer a user question using RAG pipeline.
     */
    public TutorResponse answer(String message) {
        // 1. Detect course code
        Optional<String> courseCodeOpt = courseCodeDetector.detect(message);
        String courseCode = courseCodeOpt.orElse(null);

        // 2. Classify intent
        TutorIntent intent = intentClassifier.classify(message);

        // 3. Route based on intent
        if (intent == TutorIntent.FACT_QUERY && courseCode != null) {
            return handleFactQuery(message, courseCode);
        }

        // 4. RAG path: semantic search + LLM
        return handleRagQuery(message, courseCode);
    }

    private TutorResponse handleFactQuery(String message, String courseCode) {
        // Try to match fact type from message
        String factType = inferFactType(message);

        Optional<String> fact = courseFactQueryService.getFact(courseCode, factType);
        if (fact.isPresent()) {
            log.info("FACT_QUERY resolved from DB: {} → {}", courseCode, factType);
            return new TutorResponse(fact.get(), List.of(), "HIGH");
        }

        // Fact not found in DB — fall back to RAG
        return handleRagQuery(message, courseCode);
    }

    private TutorResponse handleRagQuery(String message, String courseCode) {
        if (!openCodeAiService.isLlmEnabled()) {
            return new TutorResponse(
                "AI Tutor đang chạy ở chế độ offline. Vui lòng cấu hình API Key để sử dụng đầy đủ.",
                List.of(), "LOW");
        }

        // Semantic search with hybrid retrieval
        KnowledgeRetrievalService.SearchResult searchResult = null;
        if (courseCode != null) {
            searchResult = knowledgeRetrievalService.search(courseCode, message, DEFAULT_TOP_K);
        }

        // Build context for LLM with scores
        StringBuilder context = new StringBuilder();
        if (searchResult != null && !searchResult.chunks().isEmpty()) {
            context.append("Context từ tài liệu khóa học:\n\n");
            for (KnowledgeRetrievalService.ChunkResult chunk : searchResult.chunks()) {
                String sectionTitle = chunk.chunk().getSectionTitle() != null
                    ? chunk.chunk().getSectionTitle() : "Không có tiêu đề";
                context.append(String.format("[%s | score=%.3f]\n",
                    sectionTitle, chunk.score()));
                context.append(chunk.chunk().getChunkText()).append("\n\n");
            }
        }

        // Generate answer
        String prompt = context.length() > 0
            ? TUTOR_SYSTEM_PROMPT + "\n\n" + context.toString()
            : TUTOR_SYSTEM_PROMPT;

        String answer = openCodeAiService.generateCompletion(prompt, message);

        // Build citations
        List<Citation> citations = citationBuilder.buildCitations(
            searchResult != null ? searchResult.chunks() : null, courseCode);

        String confidence = !citations.isEmpty() ? "MEDIUM" : "LOW";

        return new TutorResponse(answer, citations, confidence);
    }

    /**
     * Infer fact type from user message for direct DB lookup.
     */
    private String inferFactType(String message) {
        String lower = message.toLowerCase();

        if (lower.contains("tín chỉ") || lower.contains("tín")) return "credits";
        if (lower.contains("tiên quyết") || lower.contains("học trước")) return "prerequisite";
        if (lower.contains("liên quan") || lower.contains("môn nào")) return "previousCourse";
        if (lower.contains("lý thuyết") || lower.contains("theory")) return "theoryHours";
        if (lower.contains("thực hành") || lower.contains("practice")) return "practiceHours";
        if (lower.contains("tự học") || lower.contains("self-study")) return "selfStudyHours";
        if (lower.contains("mục tiêu") || lower.contains("objective")) return "objectives";
        if (lower.contains("kết quả") || lower.contains("outcome")) return "outcomes";
        if (lower.contains("chương trình") || lower.contains("session")) return "sessions";
        if (lower.contains("đánh giá") || lower.contains("assessment")) return "assessments";
        if (lower.contains("mô tả") || lower.contains("description")) return "description";
        if (lower.contains("tên") || lower.contains("name")) return "courseName";

        // Try to extract assessment component code (e.g., "A2", "A3")
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\\b([A-Z]\\d+)\\b")
            .matcher(message);
        if (m.find()) {
            String code = m.group(1);
            if (lower.contains("phần trăm") || lower.contains("bao nhiêu")
                || lower.contains("weight") || lower.contains("điểm")) {
                return "assessment:" + code;
            }
        }

        return "unknown";
    }
}
