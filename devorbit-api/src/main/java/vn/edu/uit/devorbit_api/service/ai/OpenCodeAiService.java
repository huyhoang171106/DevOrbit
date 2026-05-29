package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

/**
 * Service to connect to OpenCode Go API for chat completions.
 * Employs standard OpenAI API format and provides offline fallbacks.
 */
@Service
public class OpenCodeAiService {

    @Value("${app.opencode.api-url}")
    private String apiUrl;

    @Value("${app.opencode.api-key:}")
    private String apiKey;

    @Value("${app.opencode.model:deepseek-v4-flash}")
    private String model;

    private final WebClient webClient;

    public OpenCodeAiService() {
        this.webClient = WebClient.builder().build();
    }

    @SuppressWarnings("unchecked")
    public String generateCompletion(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateOfflineFallback(userMessage);
        }

        try {
            // Build OpenAI compatible request payload
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.3
            );

            Map<String, Object> response = webClient.post()
                .uri(apiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

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
        } catch (Exception e) {
            // Graceful degradation: fall back to offline stub
        }

        return generateOfflineFallback(userMessage);
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
