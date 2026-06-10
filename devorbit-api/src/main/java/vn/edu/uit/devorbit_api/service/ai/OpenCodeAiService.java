package vn.edu.uit.devorbit_api.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service to connect to OpenCode Go API for chat completions.
 * Employs standard OpenAI API format and provides offline fallbacks.
 */
@Slf4j
@Service
public class OpenCodeAiService {

    private final WebClient webClient;
    private final vn.edu.uit.devorbit_api.config.AiConfig aiConfig;

    public OpenCodeAiService(
            @Qualifier("aiWebClient") WebClient webClient,
            vn.edu.uit.devorbit_api.config.AiConfig aiConfig) {
        this.webClient = webClient;
        this.aiConfig = aiConfig;
    }

    /**
     * Generate completion synchronously with timeout.
     * Falls back to offline stub if LLM unavailable or fails.
     */
    @SuppressWarnings("unchecked")
    public String generateCompletion(String systemPrompt, String userMessage) {
        if (!aiConfig.isLlmEnabled()) {
            log.debug("LLM not enabled, using offline fallback");
            return generateOfflineFallback(userMessage);
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "model", aiConfig.getModel(),
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.3
            );

            Map<String, Object> response = webClient.post()
                .uri(aiConfig.getApiUrl() + "/chat/completions")
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(90))
                .block();

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        String content = (String) message.get("content");
                        log.debug("LLM response received, length: {}", content != null ? content.length() : 0);
                        return content;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("LLM call failed, falling back to offline stub: {}", e.getMessage());
        }

        return generateOfflineFallback(userMessage);
    }

    /**
     * Generate completion asynchronously.
     * Returns Mono<String> for reactive use.
     */
    @SuppressWarnings("unchecked")
    public Mono<String> generateCompletionAsync(String systemPrompt, String userMessage) {
        if (!aiConfig.isLlmEnabled()) {
            return Mono.just(generateOfflineFallback(userMessage));
        }

        Map<String, Object> requestBody = Map.of(
            "model", aiConfig.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.3
        );

        return webClient.post()
            .uri(aiConfig.getApiUrl() + "/chat/completions")
            .header("Authorization", "Bearer " + aiConfig.getApiKey())
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(90))
            .map(response -> {
                if (response != null && response.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        if (firstChoice.containsKey("message")) {
                            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                            return (String) message.get("content");
                        }
                    }
                }
                return generateOfflineFallback(userMessage);
            })
            .onErrorResume(e -> {
                log.warn("Async LLM call failed: {}", e.getMessage());
                return Mono.just(generateOfflineFallback(userMessage));
            });
    }

    /**
     * Check if LLM is enabled.
     */
    public boolean isLlmEnabled() {
        return aiConfig.isLlmEnabled();
    }

    private String generateOfflineFallback(String userMessage) {
        String normalized = userMessage.toLowerCase();
        if (normalized.contains("giải tích") || normalized.contains("calculus") || normalized.contains("ma006")) {
            return "### Kinh nghiệm học tốt môn Đại cương Giải tích tại UIT:\n\n" +
                   "1. **Tài liệu nên dùng:** Bạn nên tìm đọc cuốn **\"Giải tích 2\" của thầy Đỗ Công Khanh (Bách Khoa)**. Sách có nhiều bài tập giải chi tiết giúp bạn tự học rất tốt. Ngoài ra, tham khảo lý thuyết tóm tắt tại trang SVUIT-MMTT.\n" +
                   "2. **Phương pháp học:**\n" +
                   "   - Đừng quá sa đà vào các định nghĩa trừu tượng khó hiểu. Hãy bắt đầu từ việc làm quen với các công thức và giải bài tập mẫu.\n" +
                   "   - Học chắc các khái niệm giới hạn (limits), đạo hàm (derivatives) và tích phân (integrals) trước khi chuyển sang chuỗi hàm.\n" +
                   "3. **Ôn thi:** Truy cập **Giasuplus** để tải đề thi UIT các năm trước. Hãy giải ít nhất 3 đề thi gần nhất để biết dạng cấu trúc ra đề thi của UIT. Bạn có thể tự học qua các video trên **Khan Academy** hoặc giải bài tập khó bằng **Microsoft Math Solver**.";
        }
        return "Chào bạn! Mình là cố vấn học tập AI của DevOrbit. Hiện tại hệ thống đang chạy ở chế độ offline, vui lòng cấu hình API Key OpenCode Go để mở khóa toàn bộ tính năng hỏi đáp thông minh.";
    }
}
