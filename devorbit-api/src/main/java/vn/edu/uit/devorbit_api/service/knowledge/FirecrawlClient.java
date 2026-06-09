package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Abstraction over the Firecrawl API for scraping/crawling web pages.
 * Scrapes a URL and returns markdown content.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "firecrawl.enabled", havingValue = "true", matchIfMissing = false)
public class FirecrawlClient {

    private final WebClient webClient;
    private final FirecrawlProperties properties;

    public FirecrawlClient(WebClient.Builder webClientBuilder, FirecrawlProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.getApiUrl()).build();
        this.properties = properties;
    }

    /**
     * Result from Firecrawl scrape/crawl.
     */
    public record FirecrawlResult(String markdown, String contentHash, String title) {
        public boolean isSuccess() {
            return markdown != null && !markdown.isBlank();
        }
    }

    /**
     * Scrape a single URL and return markdown content.
     */
    public FirecrawlResult scrape(String url) {
        try {
            Map<String, Object> body = Map.of(
                "url", url,
                "formats", List.of("markdown"),
                "onlyMainContent", true
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri("/v1/scrape")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

            return extractResult(response);
        } catch (Exception e) {
            log.error("Firecrawl scrape failed for {}: {}", url, e.getMessage());
            return new FirecrawlResult(null, null, null);
        }
    }

    /**
     * Crawl a site and return multiple page results.
     */
    public List<FirecrawlResult> crawl(String url, int maxPages) {
        try {
            Map<String, Object> body = Map.of(
                "url", url,
                "limit", Math.min(maxPages, properties.getMaxPages()),
                "formats", List.of("markdown"),
                "onlyMainContent", true
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri("/v1/crawl")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds() * 2L))
                .block();

            return extractCrawlResults(response);
        } catch (Exception e) {
            log.error("Firecrawl crawl failed for {}: {}", url, e.getMessage());
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private FirecrawlResult extractResult(Map<String, Object> response) {
        if (response == null) {
            return new FirecrawlResult(null, null, null);
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            return new FirecrawlResult(null, null, null);
        }

        String markdown = (String) data.get("markdown");
        String title = (String) data.get("title");

        String contentHash = computeHash(markdown);
        return new FirecrawlResult(markdown, contentHash, title);
    }

    @SuppressWarnings("unchecked")
    private List<FirecrawlResult> extractCrawlResults(Map<String, Object> response) {
        if (response == null) {
            return List.of();
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            return List.of();
        }

        List<Map<String, Object>> pages = (List<Map<String, Object>>) data.get("data");
        if (pages == null) {
            return List.of();
        }

        return pages.stream()
            .map(page -> {
                String markdown = (String) page.get("markdown");
                String title = (String) page.get("title");
                String hash = computeHash(markdown);
                return new FirecrawlResult(markdown, hash, title);
            })
            .toList();
    }

    private String computeHash(String content) {
        if (content == null) return null;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("SHA-256 hash computation failed: {}", e.getMessage());
            return String.valueOf(content.hashCode());
        }
    }
}
