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
        this(buildWebClient(properties), properties);
    }

    private static WebClient buildWebClient(ExaProperties properties) {
        int timeoutSec = Math.max(1, properties.getTimeoutSeconds());
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
            .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSec * 1000)
            .doOnConnected(conn -> conn
                .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(timeoutSec))
                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(timeoutSec)));

        return WebClient.builder()
            .baseUrl(normalizeBaseUrl(properties.getApiUrl()))
            .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
            .build();
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
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.warn("Exa search failed with response status {}: {} - Body: {}",
                e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString());
            return Map.of();
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
