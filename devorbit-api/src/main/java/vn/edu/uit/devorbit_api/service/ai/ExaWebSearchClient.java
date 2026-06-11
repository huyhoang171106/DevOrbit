package vn.edu.uit.devorbit_api.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import vn.edu.uit.devorbit_api.config.ExaProperties;

import java.time.Duration;
import java.util.Map;

/**
 * Thin transport client for the Exa search API.
 */
@Slf4j
@Component
public class ExaWebSearchClient {

    private final WebClient webClient;
    private final ExaProperties properties;

    @Autowired
    public ExaWebSearchClient(ExaProperties properties) {
        this(WebClient.builder()
            .baseUrl(normalizeBaseUrl(properties.getApiUrl()))
            .build(), properties);
    }

    ExaWebSearchClient(WebClient webClient, ExaProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public Map<String, Object> search(Map<String, Object> requestBody) {
        if (!isConfigured()) {
            log.warn("Exa is disabled or missing API key. Skipping search request.");
            return Map.of();
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri("/search")
                .header("x-api-key", properties.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(Math.max(1, properties.getTimeoutSeconds())))
                .block();

            return response != null ? response : Map.of();
        } catch (Exception e) {
            log.warn("Exa search failed: {}", e.getMessage());
            return Map.of();
        }
    }

    private boolean isConfigured() {
        return properties.isEnabled() && properties.getApiKey() != null && !properties.getApiKey().isBlank();
    }

    private static String normalizeBaseUrl(String apiUrl) {
        if (apiUrl == null || apiUrl.isBlank()) {
            return "https://api.exa.ai";
        }
        return apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
    }
}
