package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Service to connect to OpenCode Go API for chat completions.
 * Employs standard OpenAI API format and provides offline fallbacks.
 */
@Slf4j
@Service
public class OpenCodeAiService {

    private final WebClient webClient;
    private final vn.edu.uit.devorbit_api.config.AiConfig aiConfig;
    private final ObjectMapper objectMapper;

    public OpenCodeAiService(
            @Qualifier("aiWebClient") WebClient webClient,
            vn.edu.uit.devorbit_api.config.AiConfig aiConfig,
            ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.aiConfig = aiConfig;
        this.objectMapper = objectMapper;
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

        int maxRetries = 2;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Map<String, Object> requestBody = Map.of(
                    "model", aiConfig.getModel(),
                    "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                    ),
                    "temperature", 0.3,
                    "thinking", Map.of("type", "disabled")
                );

                Map<String, Object> response = webClient.post()
                    .uri(aiConfig.getApiUrl() + "/chat/completions")
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(aiConfig.getTimeoutSeconds()))
                    .block();

                if (response != null && response.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        if (firstChoice.containsKey("message")) {
                            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                            String content = (String) message.get("content");
                            if (content != null && !content.isBlank()) {
                                log.debug("LLM response received on attempt {}, length: {}", attempt, content.length());
                                return content;
                            }
                        }
                    }
                }
                log.warn("LLM returned empty response on attempt {}/{}", attempt, maxRetries);
            } catch (Exception e) {
                log.warn("LLM call failed on attempt {}/{}: {}", attempt, maxRetries, e.getMessage());
            }

            // Backoff before retry: 2s, 4s
            if (attempt < maxRetries) {
                try {
                    long backoffMs = attempt * 2000L;
                    log.info("LLM retry in {}ms...", backoffMs);
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return generateOfflineFallback(userMessage, true);
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
            "temperature", 0.3,
            "thinking", Map.of("type", "disabled")
        );

        return webClient.post()
            .uri(aiConfig.getApiUrl() + "/chat/completions")
            .header("Authorization", "Bearer " + aiConfig.getApiKey())
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(aiConfig.getTimeoutSeconds()))
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
                return generateOfflineFallback(userMessage, true);
            })
            .onErrorResume(e -> {
                log.warn("Async LLM call failed: {}", e.getMessage());
                return Mono.just(generateOfflineFallback(userMessage, true));
            });
    }

    /**
     * Check if LLM is enabled.
     */
    public boolean isLlmEnabled() {
        return aiConfig.isLlmEnabled();
    }

    /**
     * Stream completion from the LLM using OpenAI-compatible SSE format.
     * Returns a Flux of delta content strings as they arrive from the server.
     * Falls back to one-shot generateCompletion on errors before the first delta.
     */
    public Flux<String> streamCompletion(String systemPrompt, String userMessage) {
        if (!aiConfig.isLlmEnabled()) {
            log.debug("LLM not enabled, returning offline fallback as single delta");
            return Flux.just(generateOfflineFallback(userMessage));
        }
        Map<String, Object> requestBody = Map.of(
            "model", aiConfig.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.3,
            "stream", true,
            "thinking", Map.of("type", "disabled")
        );

        // Streaming needs longer timeout — use 3x the base timeout for total stream
        int streamTimeoutSeconds = aiConfig.getTimeoutSeconds() * 3;

        AtomicBoolean emittedAnyDelta = new AtomicBoolean(false);
        AtomicInteger retryCount = new AtomicInteger(0);
        int maxStreamRetries = 2;

        Flux<String> stream = webClient.post()
            .uri(aiConfig.getApiUrl() + "/chat/completions")
            .header("Authorization", "Bearer " + aiConfig.getApiKey())
            .header("Accept", "text/event-stream")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
            .timeout(Duration.ofSeconds(streamTimeoutSeconds))
            .flatMap(event -> {
                String data = event.data();
                List<String> deltas = extractDeltaContents(data == null ? null : "data: " + data);
                if (!deltas.isEmpty()) {
                    emittedAnyDelta.set(true);
                }
                return Flux.fromIterable(deltas);
            })
            .onErrorResume(e -> {
                if (!emittedAnyDelta.get() && retryCount.getAndIncrement() < maxStreamRetries) {
                    log.warn("Streaming LLM failed before first token (attempt {}), retrying: {}",
                        retryCount.get(), e.getMessage());
                    // Retry the entire stream
                    return streamCompletion(systemPrompt, userMessage);
                }
                if (!emittedAnyDelta.get()) {
                    log.warn("Streaming LLM call failed after retries: {}", e.getMessage());
                    return Flux.just(generateOfflineFallback(userMessage, true));
                }
                log.error("Streaming LLM call failed after at least one delta: {}", e.getMessage());
                return Flux.error(e);
            });

        return stream.switchIfEmpty(Flux.defer(() -> {
            log.warn("Streaming LLM returned no deltas, falling back to one-shot");
            return Flux.just(generateOfflineFallback(userMessage, true));
        }));
    }

    /**
     * Extract delta content strings from an SSE chunk received from the LLM.
     * Handles OpenAI-compatible SSE format with data: lines.
     */
    private List<String> extractDeltaContents(String rawChunk) {
        List<String> deltas = new ArrayList<>();
        if (rawChunk == null || rawChunk.isBlank()) {
            return deltas;
        }

        String[] lines = rawChunk.split("\n");
        for (String line : lines) {
            if (line == null || !line.startsWith("data:")) {
                continue;
            }
            String payload = line.substring("data:".length()).trim();
            if (payload.isEmpty() || "[DONE]".equals(payload)) {
                continue;
            }

            try {
                JsonNode node = objectMapper.readTree(payload);
                JsonNode choices = node.get("choices");
                if (choices == null || !choices.isArray() || choices.isEmpty()) {
                    continue;
                }
                JsonNode firstChoice = choices.get(0);
                JsonNode delta = firstChoice.get("delta");
                if (delta != null) {
                    JsonNode content = delta.get("content");
                    if (content != null && content.isTextual() && !content.asText().isBlank()) {
                        deltas.add(content.asText());
                        continue;
                    }
                }
                // Fallback: some providers put content directly in message
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null && content.isTextual() && !content.asText().isBlank()) {
                        deltas.add(content.asText());
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to parse SSE delta chunk: {}", e.getMessage());
            }
        }

        return deltas;
    }

    public boolean isOfflineFallbackResponse(String response) {
        return response != null && (
            response.startsWith("Dịch vụ AI đang phản hồi không ổn định")
                || response.startsWith("DevOrbit chưa được cấu hình dịch vụ AI")
        );
    }

    private String generateOfflineFallback(String userMessage) {
        return generateOfflineFallback(userMessage, false);
    }

    private String generateOfflineFallback(String userMessage, boolean providerFailed) {
        if (providerFailed) {
            return "Dịch vụ AI đang phản hồi không ổn định. "
                + "DevOrbit chưa nhận được câu trả lời từ mô hình trong thời gian cho phép; "
                + "bạn hãy thử lại sau hoặc hỏi câu ngắn hơn, kèm mã môn cụ thể.";
        }
        String normalized = userMessage.toLowerCase();
        if (normalized.contains("giải tích") || normalized.contains("calculus") || normalized.contains("ma006")) {
            return "### Kinh nghiệm học tốt môn Đại cương Giải tích tại UIT:\n\n" +
                   "1. **Tài liệu nên dùng:** Bạn nên tìm đọc cuốn **\"Giải tích 2\" của thầy Đỗ Công Khanh (Bách Khoa)**. Sách có nhiều bài tập giải chi tiết giúp bạn tự học rất tốt. Ngoài ra, tham khảo lý thuyết tóm tắt tại trang SVUIT-MMTT.\n" +
                   "2. **Phương pháp học:**\n" +
                   "   - Đừng quá sa đà vào các định nghĩa trừu tượng khó hiểu. Hãy bắt đầu từ việc làm quen với các công thức và giải bài tập mẫu.\n" +
                   "   - Học chắc các khái niệm giới hạn (limits), đạo hàm (derivatives) và tích phân (integrals) trước khi chuyển sang chuỗi hàm.\n" +
                   "3. **Ôn thi:** Truy cập **Giasuplus** để tải đề thi UIT các năm trước. Hãy giải ít nhất 3 đề thi gần nhất để biết dạng cấu trúc ra đề thi của UIT. Bạn có thể tự học qua các video trên **Khan Academy** hoặc giải bài tập khó bằng **Microsoft Math Solver**.";
        }
        return "DevOrbit chưa được cấu hình dịch vụ AI. "
            + "Vui lòng cấu hình API key để sử dụng đầy đủ tính năng hỏi đáp.";
    }
}
