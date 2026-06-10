package vn.edu.uit.devorbit_api.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.config.AiConfig;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Embedding service using OpenAI-compatible /v1/embeddings API.
 * Works with OpenAI, OpenCode Go, and any compatible provider.
 */
@Slf4j
@Service
@ConditionalOnExpression("'${app.embedding.offline:false}' == 'false' && '${app.embedding.provider:fireworks}' != 'fireworks'")
public class OpenAiCompatibleEmbeddingService implements EmbeddingService {

    private final WebClient webClient;
    private final AiConfig aiConfig;

    private static final int DEFAULT_DIMENSIONS = 1536;
    private static final String EMBEDDING_MODEL = "text-embedding-3-small";

    public OpenAiCompatibleEmbeddingService(
            @Qualifier("aiWebClient") WebClient webClient,
            AiConfig aiConfig) {
        this.webClient = webClient;
        this.aiConfig = aiConfig;
    }

    @Override
    public float[] embed(String text) {
        if (!isEnabled()) {
            throw new IllegalStateException("Embedding service not enabled — no API key configured");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "model", EMBEDDING_MODEL,
                "input", text
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri(aiConfig.getApiUrl() + "/embeddings")
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            return extractEmbedding(response);
        } catch (Exception e) {
            log.error("Embedding API call failed: {}", e.getMessage());
            throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (!isEnabled()) {
            throw new IllegalStateException("Embedding service not enabled — no API key configured");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "model", EMBEDDING_MODEL,
                "input", texts
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri(aiConfig.getApiUrl() + "/embeddings")
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(60))
                .block();

            return extractEmbeddings(response, texts.size());
        } catch (Exception e) {
            log.error("Batch embedding API call failed: {}", e.getMessage());
            throw new RuntimeException("Batch embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int dimensions() {
        return DEFAULT_DIMENSIONS;
    }

    @Override
    public boolean isEnabled() {
        return aiConfig.isLlmEnabled();
    }

    @SuppressWarnings("unchecked")
    private float[] extractEmbedding(Map<String, Object> response) {
        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Invalid embedding response: missing 'data'");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        if (data.isEmpty()) {
            throw new RuntimeException("Invalid embedding response: empty 'data'");
        }

        List<Double> embedding = (List<Double>) data.get(0).get("embedding");
        if (embedding == null) {
            throw new RuntimeException("Invalid embedding response: missing 'embedding'");
        }

        return toFloatArray(embedding.stream().mapToDouble(Double::doubleValue).toArray());
    }

    @SuppressWarnings("unchecked")
    private List<float[]> extractEmbeddings(Map<String, Object> response, int expectedSize) {
        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Invalid embedding response: missing 'data'");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        if (data.size() != expectedSize) {
            throw new RuntimeException("Expected " + expectedSize + " embeddings, got " + data.size());
        }

        return data.stream()
            .map(item -> {
                List<Double> embedding = (List<Double>) item.get("embedding");
                return toFloatArray(embedding.stream().mapToDouble(Double::doubleValue).toArray());
            })
            .toList();
    }

    private float[] toFloatArray(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }
}
