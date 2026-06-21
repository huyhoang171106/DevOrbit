package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI-powered candidate evaluation using OpenCode LLM.
 *
 * For each candidate, calls the LLM with repo context (README, file tree, description, course)
 * and asks it to:
 * 1. Score the repo 0-100 (usefulness for UIT students)
 * 2. Recommend: APPROVE / REVIEW / REJECT
 * 3. Provide a brief review note
 *
 * Falls back to heuristic scoring if LLM is unavailable.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateEvaluationService {

    private final OpenCodeAiService openCodeAiService;

    /** Anchored to "SCORE:" prefix so other numbers in the response don't corrupt the score. */
    private static final Pattern SCORE_PATTERN = Pattern.compile("SCORE:\\s*(\\d{1,3})\\b", Pattern.CASE_INSENSITIVE);
    /** Anchored to "RECOMMEND:" prefix so stray occurrences of APPROVE/REVIEW/REJECT don't match. */
    private static final Pattern RECOMMEND_PATTERN = Pattern.compile("RECOMMEND:\\s*(APPROVE|REVIEW|REJECT)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * Evaluate a single candidate using LLM. Falls back to heuristic on failure.
     */
    public void evaluate(RepoCandidate candidate) {
        if (candidate == null) return;

        try {
            String llmResult = evaluateWithLlm(candidate);
            if (llmResult != null && !llmResult.isBlank()) {
                applyLlmResult(candidate, llmResult);
                log.info("AI evaluated candidate {} ({}): score={}, recommend={}",
                    candidate.getId(), candidate.getGithubName(),
                    candidate.getAiScore(), candidate.getAiRecommendation());
                return;
            }
        } catch (Exception e) {
            log.warn("LLM evaluation failed for candidate {}, falling back to heuristic: {}",
                candidate.getId(), e.getMessage());
        }

        // Fallback heuristic
        evaluateHeuristic(candidate);
    }

    /**
     * Call OpenCode AI to evaluate the candidate.
     */
    String evaluateWithLlm(RepoCandidate candidate) {
        String systemPrompt = """
            Bạn là chuyên gia đánh giá repository GitHub dành cho sinh viên UIT.
            Nhiệm vụ: phân tích repo và cho điểm 0-100 dựa trên:
            - Tính hữu ích cho sinh viên UIT (có source code? README? hướng dẫn?)
            - Chất lượng (package.json, build config, có test?)
            - Mức độ liên quan đến môn học
            - Độ phổ biến (stars/forks)
            - Cập nhật gần đây không?
            
            Phản hồi theo format:
            SCORE: <số 0-100>
            RECOMMEND: <APPROVE|REVIEW|REJECT>
            NOTE: <1-2 câu nhận xét ngắn>
            
            Chỉ trả lời đúng format trên, không thêm gì khác.
            """;

        StringBuilder candidateInfo = new StringBuilder();
        candidateInfo.append("Tên repo: ").append(nullSafe(candidate.getGithubName())).append("\n");
        candidateInfo.append("Mô tả: ").append(nullSafe(candidate.getDescription())).append("\n");
        candidateInfo.append("Ngôn ngữ: ").append(nullSafe(candidate.getPrimaryLanguage())).append("\n");
        candidateInfo.append("Stars: ").append(candidate.getStars() != null ? candidate.getStars() : 0).append("\n");
        candidateInfo.append("Forks: ").append(candidate.getForks() != null ? candidate.getForks() : 0).append("\n");

        Course course = candidate.getCourse();
        if (course != null) {
            candidateInfo.append("Môn học: ").append(course.getMaMH()).append(" - ").append(nullSafe(course.getTenMH())).append("\n");
        }

        candidateInfo.append("Topics: ").append(nullSafe(candidate.getTopics())).append("\n");
        candidateInfo.append("Có README: ").append(candidate.getHasReadme() != null && candidate.getHasReadme() ? "Có" : "Không").append("\n");

        if (candidate.getReadmeExcerpt() != null && !candidate.getReadmeExcerpt().isBlank()) {
            String excerpt = candidate.getReadmeExcerpt();
            if (excerpt.length() > 1500) excerpt = excerpt.substring(0, 1500) + "...";
            candidateInfo.append("README excerpt:\n").append(excerpt).append("\n");
        }

        if (candidate.getFileTree() != null && !candidate.getFileTree().isBlank()) {
            String tree = candidate.getFileTree();
            if (tree.length() > 2000) tree = tree.substring(0, 2000) + "...";
            candidateInfo.append("File tree:\n").append(tree).append("\n");
        }

        log.debug("Evaluating candidate {} with LLM, input length: {} chars",
            candidate.getId(), candidateInfo.length());

        return openCodeAiService.generateCompletion(systemPrompt, candidateInfo.toString());
    }

    /**
     * Parse LLM response and set aiScore, aiRecommendation, reviewNote.
     */
    void applyLlmResult(RepoCandidate candidate, String llmResult) {
        // Extract score
        Matcher scoreMatcher = SCORE_PATTERN.matcher(llmResult);
        if (scoreMatcher.find()) {
            try {
                int score = Integer.parseInt(scoreMatcher.group(1));
                candidate.setAiScore(Math.max(0, Math.min(100, score)));
            } catch (NumberFormatException e) {
                candidate.setAiScore(50); // default middle
            }
        } else {
            candidate.setAiScore(50);
        }
        Matcher recMatcher = RECOMMEND_PATTERN.matcher(llmResult);
        // Extract recommendation
        if (recMatcher.find()) {
            candidate.setAiRecommendation(recMatcher.group(1));
        } else {
            // Infer from score
            candidate.setAiRecommendation(
                candidate.getAiScore() >= 60 ? "APPROVE" :
                candidate.getAiScore() >= 30 ? "REVIEW" : "REJECT"
            );
        }

        // Extract note
        int noteIdx = llmResult.indexOf("NOTE:");
        if (noteIdx >= 0) {
            String note = llmResult.substring(noteIdx + 5).trim();
            if (!note.isBlank()) {
                candidate.setReviewNote(note.length() > 500 ? note.substring(0, 500) : note);
            }
        }
    }

    /**
     * Fallback heuristic scoring when LLM is unavailable.
     */
    void evaluateHeuristic(RepoCandidate candidate) {
        int score = 0;

        // Popularity (max 20)
        int stars = candidate.getStars() != null ? candidate.getStars() : 0;
        int forks = candidate.getForks() != null ? candidate.getForks() : 0;
        if (stars >= 10) score += 15;
        else if (stars >= 5) score += 10;
        else if (stars >= 2) score += 6;
        else if (stars >= 1) score += 3;
        if (forks >= 5) score += 5;
        else if (forks >= 1) score += 2;

        // Documentation (max 20)
        if (candidate.getHasReadme() != null && candidate.getHasReadme()) score += 12;
        if (candidate.getDescription() != null && !candidate.getDescription().isBlank()) score += 8;

        // Technical quality (max 30)
        if (candidate.getPrimaryLanguage() != null && !candidate.getPrimaryLanguage().isBlank()) score += 8;
        if (candidate.getTopics() != null && !candidate.getTopics().isBlank()) score += 7;
        if (hasSourceCode(candidate)) score += 10;
        if (hasBuildConfig(candidate)) score += 5;

        // Activity (max 15)
        if (isRecentlyActive(candidate.getLastPushedAt())) score += 8;
        if (stars > 0 || forks > 0) score += 7;

        // Course relevance (max 15)
        Course course = candidate.getCourse();
        if (course != null && candidate.getGithubName() != null) {
            String lowerName = candidate.getGithubName().toLowerCase();
            String lowerCourse = course.getMaMH().toLowerCase();
            if (lowerName.contains(lowerCourse)) score += 10;
        }

        score = Math.max(0, Math.min(100, score));
        candidate.setAiScore(score);
        candidate.setAiRecommendation(
            score >= 60 ? "APPROVE" :
            score >= 30 ? "REVIEW" : "REJECT"
        );
    }

    private boolean hasSourceCode(RepoCandidate candidate) {
        if (candidate.getFileTree() == null) return false;
        Pattern sourcePattern = Pattern.compile(
            "\\.(java|kt|dart|py|js|ts|jsx|tsx|go|rs|cs|cpp|c|rb|php|swift|vue|svelte)$",
            Pattern.CASE_INSENSITIVE
        );
        return candidate.getFileTree().lines()
            .anyMatch(line -> sourcePattern.matcher(line.trim()).find());
    }

    private boolean hasBuildConfig(RepoCandidate candidate) {
        if (candidate.getFileTree() == null) return false;
        Pattern buildPattern = Pattern.compile(
            "(pom\\.xml|build\\.gradle|package\\.json|requirements\\.txt|go\\.mod|cargo\\.toml|pubspec\\.yaml|cmakelists\\.txt|dockerfile)$",
            Pattern.CASE_INSENSITIVE
        );
        return candidate.getFileTree().lines()
            .anyMatch(line -> buildPattern.matcher(line.trim()).find());
    }

    private boolean isRecentlyActive(String lastPushedAt) {
        if (lastPushedAt == null || lastPushedAt.isBlank()) return false;
        try {
            java.time.Instant pushed = java.time.Instant.parse(lastPushedAt);
            java.time.Instant cutoff = java.time.Instant.now().minus(java.time.Duration.ofDays(365));
            return pushed.isAfter(cutoff);
        } catch (Exception e) {
            return false;
        }
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
