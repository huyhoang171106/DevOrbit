package vn.edu.uit.devorbit_api.service.knowledge;

import org.springframework.stereotype.Component;

/**
 * Rule-based intent classifier for AI Tutor questions.
 * Classifies user messages into intents without calling LLM.
 */
@Component
public class TutorIntentClassifier {

    /**
     * Classify a user message into a TutorIntent.
     */
    public TutorIntent classify(String message) {
        if (message == null || message.isBlank()) {
            return TutorIntent.GENERAL_RAG;
        }

        String lower = message.toLowerCase().trim();

        // FACT_QUERY: direct factual questions about course properties
        if (isFactQuery(lower)) {
            return TutorIntent.FACT_QUERY;
        }

        // LEARNING_ADVICE: study tips, how to learn, weak areas
        if (isLearningAdvice(lower)) {
            return TutorIntent.LEARNING_ADVICE;
        }

        // ROADMAP: learning path, prerequisites chain
        if (isRoadmap(lower)) {
            return TutorIntent.ROADMAP;
        }

        // GRAPH_IMPACT: what affects what, dependency graph
        if (isGraphImpact(lower)) {
            return TutorIntent.GRAPH_IMPACT;
        }

        // REPO_ADVICE: code project advice
        if (isRepoAdvice(lower)) {
            return TutorIntent.REPO_ADVICE;
        }

        return TutorIntent.GENERAL_RAG;
    }

    private boolean isFactQuery(String lower) {
        return lower.contains("mấy tín chỉ") || lower.contains("bao nhiêu tín")
            || lower.contains("tiên quyết") || lower.contains("học trước")
            || lower.contains("phần trăm") || lower.contains("bao nhiêu phần")
            || lower.contains("thực hành") || lower.contains("lý thuyết")
            || lower.contains("điểm") || lower.contains("đánh giá")
            || lower.contains("credits") || lower.contains("prerequisite")
            || lower.contains("assessment") || lower.contains("weight")
            || lower.contains("học gì") || lower.contains("nội dung")
            || lower.contains("môn nào") || lower.contains("kỳ vọng")
            || lower.contains("outcomes") || lower.contains("objectives");
    }

    private boolean isLearningAdvice(String lower) {
        return lower.contains("yếu") || lower.contains("khó")
            || lower.contains("học sao") || lower.contains("làm sao")
            || lower.contains("mẹo") || lower.contains("gợi ý")
            || lower.contains("study") || lower.contains("advice")
            || lower.contains("khuyên") || lower.contains("should");
    }

    private boolean isRoadmap(String lower) {
        return lower.contains("lộ trình") || lower.contains("roadmap")
            || lower.contains("học theo") || lower.contains("thứ tự")
            || lower.contains("trước") || lower.contains("sau");
    }

    private boolean isGraphImpact(String lower) {
        return lower.contains("ảnh hưởng") || lower.contains("impact")
            || lower.contains("phụ thuộc") || lower.contains("liên quan")
            || lower.contains("nếu rớt") || lower.contains("nếu trượt");
    }

    private boolean isRepoAdvice(String lower) {
        return lower.contains("project") || lower.contains("repo")
            || lower.contains("code") || lower.contains("implement")
            || lower.contains("làm bài tập") || lower.contains("assignment");
    }
}
