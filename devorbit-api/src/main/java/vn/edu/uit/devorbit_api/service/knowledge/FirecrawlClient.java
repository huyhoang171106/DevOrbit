package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstraction over the Firecrawl API for scraping, crawling, and search fallback.
 */
@Slf4j
@Component
public class FirecrawlClient {

    private static final List<String> DEFAULT_MARKDOWN_FORMAT = List.of("markdown");

    private final WebClient webClient;
    private final FirecrawlProperties properties;

    @Autowired
    public FirecrawlClient(FirecrawlProperties properties) {
        this(WebClient.builder()
            .baseUrl(normalizeBaseUrl(properties.getApiUrl()))
            .build(), properties);
    }

    FirecrawlClient(WebClient webClient, FirecrawlProperties properties) {
        this.webClient = webClient;
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
     * Result from Firecrawl search.
     */
    public record FirecrawlSearchResult(
        String url,
        String title,
        String description,
        String markdown,
        String sourceUrl
    ) {
        public boolean isSuccess() {
            return url != null && !url.isBlank();
        }
    }

    /**
     * Scrape a single URL and return markdown content.
     */
    public FirecrawlResult scrape(String url) {
        if (!isConfigured()) {
            log.warn("Firecrawl is disabled or missing API key. Skipping scrape for {}", url);
            return new FirecrawlResult(null, null, null);
        }
        try {
            Map<String, Object> body = Map.of(
                "url", url,
                "formats", DEFAULT_MARKDOWN_FORMAT,
                "onlyMainContent", true,
                "removeBase64Images", true,
                "blockAds", true,
                "storeInCache", true
            );

            Map<String, Object> response = postJson("/v2/scrape", body, properties.getTimeoutSeconds());
            return extractResult(response);
        } catch (Exception e) {
            log.error("Firecrawl scrape failed for {}: {}", url, e.getMessage());
            return new FirecrawlResult(null, null, null);
        }
    }

    /**
     * Search the web and optionally scrape results.
     */
    public List<FirecrawlSearchResult> search(String query, int maxResults) {
        if (!isConfigured()) {
            log.warn("Firecrawl is disabled or missing API key. Skipping search for {}", query);
            return List.of();
        }
        try {
            Map<String, Object> scrapeOptions = Map.of(
                "formats", DEFAULT_MARKDOWN_FORMAT,
                "onlyMainContent", true,
                "removeBase64Images", true,
                "blockAds", true,
                "storeInCache", true
            );

            Map<String, Object> body = Map.of(
                "query", query,
                "limit", Math.max(1, Math.min(maxResults, properties.getMaxPages())),
                "sources", List.of("web"),
                "ignoreInvalidURLs", true,
                "scrapeOptions", scrapeOptions
            );

            Map<String, Object> response = postJson("/v2/search", body, properties.getTimeoutSeconds());
            return extractSearchResults(response);
        } catch (Exception e) {
            log.error("Firecrawl search failed for {}: {}", query, e.getMessage());
            return List.of();
        }
    }

    /**
     * Crawl a site and return multiple page results.
     */
    public List<FirecrawlResult> crawl(String url, int maxPages) {
        if (!isConfigured()) {
            log.warn("Firecrawl is disabled or missing API key. Skipping crawl for {}", url);
            return List.of();
        }
        try {
            Map<String, Object> scrapeOptions = Map.of(
                "formats", DEFAULT_MARKDOWN_FORMAT,
                "onlyMainContent", true,
                "removeBase64Images", true,
                "blockAds", true,
                "storeInCache", true
            );

            Map<String, Object> body = Map.of(
                "url", url,
                "limit", Math.max(1, Math.min(maxPages, properties.getMaxPages())),
                "scrapeOptions", scrapeOptions
            );

            Map<String, Object> startResponse = postJson("/v2/crawl", body, properties.getTimeoutSeconds());
            String crawlId = stringValue(startResponse.get("id"));
            if (crawlId == null || crawlId.isBlank()) {
                return extractCrawlResults(startResponse);
            }

            Map<String, Object> finalResponse = pollCrawl(crawlId);
            return extractCrawlResults(finalResponse);
        } catch (Exception e) {
            log.error("Firecrawl crawl failed for {}: {}", url, e.getMessage());
            return List.of();
        }
    }

    private boolean isConfigured() {
        return properties.isEnabled()
            && properties.getApiKey() != null
            && !properties.getApiKey().isBlank();
    }

    private Map<String, Object> postJson(String path, Map<String, Object> body, int timeoutSeconds) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.post()
            .uri(path)
            .header("Authorization", "Bearer " + properties.getApiKey())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
            .block();
        return response != null ? response : Map.of();
    }

    private Map<String, Object> getJson(String path, int timeoutSeconds) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.get()
            .uri(path)
            .header("Authorization", "Bearer " + properties.getApiKey())
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
            .block();
        return response != null ? response : Map.of();
    }

    private Map<String, Object> getJsonFromAbsoluteUrl(String url, int timeoutSeconds) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.get()
            .uri(url)
            .header("Authorization", "Bearer " + properties.getApiKey())
            .retrieve()
            .bodyToMono(Map.class)
            .timeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
            .block();
        return response != null ? response : Map.of();
    }

    private Map<String, Object> pollCrawl(String crawlId) throws InterruptedException {
        long deadlineNanos = System.nanoTime() + Duration.ofSeconds(Math.max(30L, properties.getTimeoutSeconds() * 2L)).toNanos();
        Map<String, Object> response = Map.of();

        while (System.nanoTime() < deadlineNanos) {
            response = getJson("/v2/crawl/" + crawlId, properties.getTimeoutSeconds());
            if (response.isEmpty()) {
                return response;
            }

            String status = stringValue(response.get("status"));
            if (status == null || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("failed")) {
                return response;
            }

            Thread.sleep(1500L);
        }

        return response;
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

        String markdown = stringValue(data.get("markdown"));
        String title = firstNonBlank(
            stringValue(data.get("title")),
            stringValue(data.get("metadata") instanceof Map<?, ?> metadata ? ((Map<String, Object>) metadata).get("title") : null)
        );

        String contentHash = computeHash(markdown);
        return new FirecrawlResult(markdown, contentHash, title);
    }

    @SuppressWarnings("unchecked")
    private List<FirecrawlSearchResult> extractSearchResults(Map<String, Object> response) {
        if (response == null) {
            return List.of();
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            return List.of();
        }

        List<Map<String, Object>> webResults = (List<Map<String, Object>>) data.get("web");
        if (webResults == null || webResults.isEmpty()) {
            return List.of();
        }

        List<FirecrawlSearchResult> results = new ArrayList<>();
        for (Map<String, Object> item : webResults) {
            String url = firstNonBlank(
                stringValue(item.get("url")),
                stringValue(item.get("sourceURL"))
            );
            Map<String, Object> metadata = item.get("metadata") instanceof Map<?, ?> meta
                ? (Map<String, Object>) meta
                : Map.of();
            String title = firstNonBlank(
                stringValue(item.get("title")),
                stringValue(metadata.get("title"))
            );
            String description = firstNonBlank(
                stringValue(item.get("description")),
                stringValue(metadata.get("description")),
                trimText(stringValue(item.get("markdown")), 320)
            );
            String markdown = stringValue(item.get("markdown"));
            String sourceUrl = firstNonBlank(
                stringValue(metadata.get("sourceURL")),
                stringValue(metadata.get("url")),
                url
            );

            FirecrawlSearchResult result = new FirecrawlSearchResult(url, title, description, markdown, sourceUrl);
            if (result.isSuccess()) {
                results.add(result);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private List<FirecrawlResult> extractCrawlResults(Map<String, Object> response) {
        if (response == null || response.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> pages = new ArrayList<>();
        Map<String, Object> current = response;
        while (current != null && !current.isEmpty()) {
            List<Map<String, Object>> currentPages = (List<Map<String, Object>>) current.get("data");
            if (currentPages != null) {
                pages.addAll(currentPages);
            }

            String next = stringValue(current.get("next"));
            if (next == null || next.isBlank()) {
                break;
            }
            current = getJsonFromAbsoluteUrl(next, properties.getTimeoutSeconds());
        }

        if (pages.isEmpty()) {
            return List.of();
        }

        return pages.stream()
            .map(page -> {
                String markdown = stringValue(page.get("markdown"));
                Map<String, Object> metadata = page.get("metadata") instanceof Map<?, ?> meta
                    ? (Map<String, Object>) meta
                    : Map.of();
                String title = firstNonBlank(
                    stringValue(page.get("title")),
                    stringValue(metadata.get("title"))
                );
                String hash = computeHash(markdown);
                return new FirecrawlResult(markdown, hash, title);
            })
            .toList();
    }

    private String trimText(String content, int maxChars) {
        if (content == null || content.isBlank()) {
            return null;
        }
        String trimmed = content.trim();
        return trimmed.length() <= maxChars ? trimmed : trimmed.substring(0, maxChars);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private static String normalizeBaseUrl(String apiUrl) {
        if (apiUrl == null || apiUrl.isBlank()) {
            return "https://api.firecrawl.dev";
        }
        return apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
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
