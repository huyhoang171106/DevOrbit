package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Embedding service using Fireworks AI /v1/embeddings API.
 * Uses Java 21+ HttpClient (no WebClient dependency).
 * Model: qwen3-embedding-8b (768 dimensions).
 */
@Slf4j
@Service
@ConditionalOnExpression("'${app.embedding.offline:false}' == 'false' && '${app.embedding.provider:fireworks}' == 'fireworks'")
public class FireworksEmbeddingService implements EmbeddingService {

    private static final String API_URL = "https://api.fireworks.ai/inference/v1/embeddings";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final int dimensions;

    public FireworksEmbeddingService(
            @Value("${app.embedding.fireworks.api-key:}") String apiKey,
            @Value("${app.embedding.model:accounts/fireworks/models/qwen3-embedding-8b}") String model,
            @Value("${app.embedding.dimensions:4096}") int dimensions) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimensions = dimensions;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public float[] embed(String text) {
        if (!isEnabled()) {
            throw new IllegalStateException("Fireworks Embedding not enabled — set FIREWORKS_API_KEY");
        }

        try {
            log.info("Fireworks embedding request: model={}, expectedDimensions={}, textLength={}",
                    model, dimensions, text != null ? text.length() : 0);
            String requestBody = buildRequestBody(model, text);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Fireworks API returned " + response.statusCode()
                        + ": " + response.body());
            }

            return validateDimensions(extractEmbedding(objectMapper.readTree(response.body())));
        } catch (Exception e) {
            log.error("Fireworks embedding call failed: {}", e.getMessage());
            throw new RuntimeException("Fireworks embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<float[]> embedBatch(List<String> texts) {
        if (!isEnabled()) {
            throw new IllegalStateException("Fireworks Embedding not enabled — set FIREWORKS_API_KEY");
        }

        try {
            log.info("Fireworks batch embedding request: model={}, expectedDimensions={}, batchSize={}",
                    model, dimensions, texts.size());
            String requestBody = buildRequestBody(model, texts);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Fireworks API returned " + response.statusCode()
                        + ": " + response.body());
            }

            return extractEmbeddings(objectMapper.readTree(response.body())).stream()
                    .map(this::validateDimensions)
                    .toList();
        } catch (Exception e) {
            log.error("Fireworks batch embedding call failed: {}", e.getMessage());
            throw new RuntimeException("Fireworks batch embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    @Override
    public boolean isEnabled() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    private float[] extractEmbedding(JsonNode root) {
        JsonNode data = root.get("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            throw new RuntimeException("Invalid Fireworks embedding response: missing 'data'");
        }

        JsonNode embedding = data.get(0).get("embedding");
        if (embedding == null || !embedding.isArray()) {
            throw new RuntimeException("Invalid Fireworks embedding response: missing 'embedding'");
        }

        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            result[i] = embedding.get(i).floatValue();
        }
        return result;
    }

    private List<float[]> extractEmbeddings(JsonNode root) {
        JsonNode data = root.get("data");
        if (data == null || !data.isArray()) {
            throw new RuntimeException("Invalid Fireworks embedding response: missing 'data'");
        }

        return java.util.stream.StreamSupport.stream(data.spliterator(), false)
                .map(item -> {
                    JsonNode emb = item.get("embedding");
                    float[] vec = new float[emb.size()];
                    for (int i = 0; i < emb.size(); i++) {
                        vec[i] = emb.get(i).floatValue();
                    }
                    return vec;
                })
                .toList();
    }

    private float[] validateDimensions(float[] embedding) {
        if (embedding.length != dimensions) {
            throw new RuntimeException("Fireworks embedding dimension mismatch: expected "
                    + dimensions + ", got " + embedding.length
                    + ". Set EMBEDDING_DIMENSIONS to match the configured model.");
        }
        return embedding;
    }

    // ── Request DTO builder ───────────────────────────────────

    private String buildRequestBody(String model, Object input) throws Exception {
        var root = objectMapper.createObjectNode();
        root.put("model", model);
        if (input instanceof String s) {
            root.put("input", s);
        } else if (input instanceof List<?> list) {
            var arr = root.putArray("input");
            for (var item : list) {
                arr.add(item.toString());
            }
        }
        return objectMapper.writeValueAsString(root);
    }
}
