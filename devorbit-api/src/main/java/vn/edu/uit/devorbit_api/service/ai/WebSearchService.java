package vn.edu.uit.devorbit_api.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.config.ExaProperties;
import vn.edu.uit.devorbit_api.dto.publicapi.WebSearchResponse;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlClient;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlProperties;

import java.net.URI;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Production web search orchestrator for DevOrbit.
 *
 * Flow:
 * Exa first for discovery and highlights, Firecrawl as fallback when Exa is unavailable
 * or returns nothing useful.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSearchService {

    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{3,4})\\b");
    private static final List<String> DEFAULT_NOISE_DOMAINS = List.of(
        "youtube.com",
        "youtu.be",
        "tiktok.com",
        "vm.tiktok.com",
        "facebook.com",
        "m.facebook.com",
        "instagram.com",
        "x.com",
        "twitter.com"
    );
    private static final List<String> TRUSTED_UIT_DOMAINS = List.of(
        "uit.edu.vn",
        "forum.uit.edu.vn",
        "daa.uit.edu.vn",
        "svuit.org",
        "github.com",
        "raw.githubusercontent.com",
        "docs.google.com",
        "drive.google.com"
    );

    private final ExaWebSearchClient exaWebSearchClient;
    private final ExaProperties exaProperties;
    private final FirecrawlClient firecrawlClient;
    private final FirecrawlProperties firecrawlProperties;

    public WebSearchResponse search(String query) {
        return search(query, exaProperties.getNumResults());
    }

    public WebSearchResponse search(String query, int requestedLimit) {
        String normalizedQuery = normalizeForSearch(query);
        if (normalizedQuery.isBlank()) {
            return new WebSearchResponse("empty", List.of());
        }

        int limit = sanitizeLimit(requestedLimit > 0 ? requestedLimit : exaProperties.getNumResults());

        if (isExaConfigured()) {
            List<WebSearchResponse.WebSearchResult> exaResults = searchWithExa(query, limit);
            if (!exaResults.isEmpty()) {
                return new WebSearchResponse("success", exaResults);
            }
            log.info("Exa returned no usable results for query '{}', trying Firecrawl fallback", query);
        } else {
            log.info("Exa is disabled or missing API key. Using Firecrawl fallback for query '{}'", query);
        }

        List<WebSearchResponse.WebSearchResult> fallbackResults = searchWithFirecrawl(query, limit);
        if (!fallbackResults.isEmpty()) {
            return new WebSearchResponse("fallback", fallbackResults);
        }

        return new WebSearchResponse("empty", List.of());
    }

    private List<WebSearchResponse.WebSearchResult> searchWithExa(String query, int limit) {
        Map<String, Object> response = exaWebSearchClient.search(buildExaRequest(query, limit));
        List<WebSearchResponse.WebSearchResult> results = parseExaResults(response, limit);
        if (!results.isEmpty()) {
            log.info("Exa returned {} results for query '{}'", results.size(), query);
        }
        return results;
    }

    private List<WebSearchResponse.WebSearchResult> searchWithFirecrawl(String query, int limit) {
        if (!isFirecrawlConfigured()) {
            return List.of();
        }

        List<FirecrawlClient.FirecrawlSearchResult> results = firecrawlClient.search(query, limit);
        if (results.isEmpty()) {
            return List.of();
        }

        List<WebSearchResponse.WebSearchResult> mapped = new ArrayList<>();
        int position = 1;
        for (FirecrawlClient.FirecrawlSearchResult result : results) {
            if (result == null || result.url() == null || result.url().isBlank()) {
                continue;
            }
            if (!isAllowedUrl(result.url())) {
                continue;
            }

            List<String> highlights = new ArrayList<>();
            if (result.description() != null && !result.description().isBlank()) {
                highlights.add(trimText(result.description(), 700));
            } else if (result.markdown() != null && !result.markdown().isBlank()) {
                highlights.add(trimText(result.markdown(), 700));
            }

            mapped.add(new WebSearchResponse.WebSearchResult(
                result.url(),
                result.title(),
                firstNonBlank(result.description(), trimText(result.markdown(), 350)),
                position++,
                highlights,
                null,
                null,
                "firecrawl"
            ));
            if (mapped.size() >= limit) {
                break;
            }
        }
        return prioritizeAndRenumberResults(mapped);
    }

    private Map<String, Object> buildExaRequest(String query, int limit) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("query", query);
        request.put("type", firstNonBlank(exaProperties.getSearchType(), "auto"));
        request.put("numResults", limit);
        request.put("contents", Map.of(
            "highlights", true,
            "maxAgeHours", exaProperties.getMaxAgeHours()
        ));

        if (shouldUseTrustedDomains(query)) {
            request.put("includeDomains", TRUSTED_UIT_DOMAINS);
        }

        request.put("excludeDomains", DEFAULT_NOISE_DOMAINS);
        return request;
    }

    private List<WebSearchResponse.WebSearchResult> parseExaResults(Map<String, Object> response, int limit) {
        List<Map<String, Object>> items = extractResultItems(response);
        if (items.isEmpty()) {
            return List.of();
        }

        Map<String, WebSearchResponse.WebSearchResult> deduped = new LinkedHashMap<>();
        int position = 1;
        for (Map<String, Object> item : items) {
            WebSearchResponse.WebSearchResult result = mapExaResult(item, position++);
            if (result == null) {
                continue;
            }
            if (!isAllowedUrl(result.url())) {
                continue;
            }

            String key = normalizeUrlKey(result.url());
            deduped.putIfAbsent(key, result);
            if (deduped.size() >= limit) {
                break;
            }
        }

        return prioritizeAndRenumberResults(new ArrayList<>(deduped.values()));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractResultItems(Map<String, Object> response) {
        if (response == null || response.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> direct = toMapList(response.get("results"));
        if (!direct.isEmpty()) {
            return direct;
        }

        Map<String, Object> data = response.get("data") instanceof Map<?, ?> map
            ? (Map<String, Object>) map
            : Map.of();
        List<Map<String, Object>> nested = toMapList(data.get("results"));
        if (!nested.isEmpty()) {
            return nested;
        }

        return toMapList(data.get("web"));
    }

    private List<Map<String, Object>> toMapList(Object value) {
        if (!(value instanceof Collection<?> collection) || collection.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (Object entry : collection) {
            if (entry instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cast = (Map<String, Object>) map;
                items.add(cast);
            }
        }
        return items;
    }

    private WebSearchResponse.WebSearchResult mapExaResult(Map<String, Object> item, int position) {
        String url = firstNonBlank(
            stringValue(item.get("url")),
            stringValue(item.get("sourceURL")),
            stringValue(item.get("sourceUrl")),
            stringValue(item.get("canonicalUrl")),
            stringValue(item.get("link"))
        );
        if (url == null || url.isBlank()) {
            return null;
        }

        List<String> highlights = extractHighlights(item.get("highlights"));
        String description = firstNonBlank(
            stringValue(item.get("snippet")),
            stringValue(item.get("summary")),
            highlightPreview(highlights),
            trimText(stringValue(item.get("text")), 400)
        );

        return new WebSearchResponse.WebSearchResult(
            url,
            firstNonBlank(stringValue(item.get("title")), stringValue(item.get("name"))),
            description,
            readPosition(item, position),
            highlights,
            firstNonBlank(
                stringValue(item.get("publishedDate")),
                stringValue(item.get("published_date")),
                stringValue(item.get("publishedAt")),
                stringValue(item.get("date"))
            ),
            firstNonBlank(stringValue(item.get("author")), firstCollectionValue(item.get("authors"))),
            "exa"
        );
    }

    private List<String> extractHighlights(Object rawHighlights) {
        if (rawHighlights == null) {
            return List.of();
        }

        List<String> highlights = new ArrayList<>();
        if (rawHighlights instanceof String highlight) {
            addIfPresent(highlights, trimText(highlight, 900));
            return List.copyOf(highlights);
        }

        if (rawHighlights instanceof Collection<?> collection) {
            for (Object entry : collection) {
                if (entry instanceof String highlight) {
                    addIfPresent(highlights, trimText(highlight, 900));
                    continue;
                }
                if (entry instanceof Map<?, ?> map) {
                    addIfPresent(highlights, trimText(firstNonBlank(
                        stringValue(map.get("text")),
                        stringValue(map.get("highlight")),
                        stringValue(map.get("content")),
                        stringValue(map.get("snippet"))
                    ), 900));
                    continue;
                }
                addIfPresent(highlights, trimText(stringValue(entry), 900));
            }
            return List.copyOf(highlights);
        }

        if (rawHighlights instanceof Map<?, ?> map) {
            addIfPresent(highlights, trimText(firstNonBlank(
                stringValue(map.get("text")),
                stringValue(map.get("highlight")),
                stringValue(map.get("content")),
                stringValue(map.get("snippet"))
            ), 900));
            return List.copyOf(highlights);
        }

        addIfPresent(highlights, trimText(stringValue(rawHighlights), 900));
        return List.copyOf(highlights);
    }

    private String highlightPreview(List<String> highlights) {
        if (highlights == null || highlights.isEmpty()) {
            return null;
        }

        StringBuilder preview = new StringBuilder();
        for (String highlight : highlights) {
            if (highlight == null || highlight.isBlank()) {
                continue;
            }
            if (preview.length() > 0) {
                preview.append(" · ");
            }
            preview.append(trimText(highlight, 220));
            if (preview.length() >= 320) {
                break;
            }
        }
        return trimText(preview.toString(), 360);
    }

    private int readPosition(Map<String, Object> item, int fallback) {
        Object value = item.get("position");
        if (value instanceof Number number) {
            return Math.max(1, number.intValue());
        }
        return fallback;
    }

    private boolean isExaConfigured() {
        return exaProperties.isEnabled()
            && exaProperties.getApiKey() != null
            && !exaProperties.getApiKey().isBlank();
    }

    private boolean isFirecrawlConfigured() {
        return firecrawlProperties.isEnabled()
            && firecrawlProperties.getApiKey() != null
            && !firecrawlProperties.getApiKey().isBlank();
    }

    private boolean shouldUseTrustedDomains(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }

        if (hasCourseCode(query)) {
            return true;
        }

        String normalized = normalizeForSearch(query);
        return normalized.contains("uit")
            || normalized.contains("tai lieu")
            || normalized.contains("de cuong")
            || normalized.contains("de thi")
            || normalized.contains("giao trinh")
            || normalized.contains("project")
            || normalized.contains("repo")
            || normalized.contains("github");
    }

    private boolean hasCourseCode(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }

        Matcher matcher = COURSE_CODE_PATTERN.matcher(query.toUpperCase(Locale.ROOT));
        return matcher.find();
    }

    private boolean isAllowedUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        String host = extractHost(url);
        if (host == null) {
            return true;
        }

        for (String domain : DEFAULT_NOISE_DOMAINS) {
            if (matchesDomain(host, domain)) {
                return false;
            }
        }
        return true;
    }

    private String extractHost(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost() != null ? uri.getHost().toLowerCase(Locale.ROOT) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean matchesDomain(String host, String domain) {
        return host.equals(domain) || host.endsWith("." + domain);
    }

    private List<WebSearchResponse.WebSearchResult> prioritizeAndRenumberResults(List<WebSearchResponse.WebSearchResult> results) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        List<WebSearchResponse.WebSearchResult> sorted = new ArrayList<>(results);
        sorted.sort((left, right) -> {
            int leftPriority = domainPriority(left.url());
            int rightPriority = domainPriority(right.url());
            if (leftPriority != rightPriority) {
                return Integer.compare(leftPriority, rightPriority);
            }

            int leftPosition = left.position() > 0 ? left.position() : Integer.MAX_VALUE;
            int rightPosition = right.position() > 0 ? right.position() : Integer.MAX_VALUE;
            if (leftPosition != rightPosition) {
                return Integer.compare(leftPosition, rightPosition);
            }

            String leftTitle = firstNonBlank(left.title(), left.url());
            String rightTitle = firstNonBlank(right.title(), right.url());
            return leftTitle.compareToIgnoreCase(rightTitle);
        });

        List<WebSearchResponse.WebSearchResult> renumbered = new ArrayList<>(sorted.size());
        int position = 1;
        for (WebSearchResponse.WebSearchResult result : sorted) {
            renumbered.add(new WebSearchResponse.WebSearchResult(
                result.url(),
                result.title(),
                result.description(),
                position++,
                result.highlights(),
                result.publishedDate(),
                result.author(),
                result.sourceProvider()
            ));
        }
        return List.copyOf(renumbered);
    }

    private int domainPriority(String url) {
        String host = extractHost(url);
        if (host == null || host.isBlank()) {
            return 4;
        }

        if (isTrustedUitDomain(host)) {
            return 0;
        }

        if (host.endsWith(".edu") || host.endsWith(".edu.vn") || host.endsWith(".ac.vn")) {
            return 1;
        }

        if (matchesDomain(host, "github.com")
            || matchesDomain(host, "raw.githubusercontent.com")
            || matchesDomain(host, "docs.google.com")
            || matchesDomain(host, "drive.google.com")) {
            return 2;
        }

        return 3;
    }

    private boolean isTrustedUitDomain(String host) {
        for (String domain : TRUSTED_UIT_DOMAINS) {
            if (matchesDomain(host, domain)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeUrlKey(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = firstNonBlank(uri.getScheme(), "https").toLowerCase(Locale.ROOT);
            String host = uri.getHost() != null ? uri.getHost().toLowerCase(Locale.ROOT) : "";
            String path = firstNonBlank(uri.getPath(), "");
            String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";
            return scheme + "://" + host + path.replaceAll("/+$", "") + query;
        } catch (Exception e) {
            return url.trim().toLowerCase(Locale.ROOT);
        }
    }

    private void addIfPresent(List<String> list, String value) {
        if (value != null && !value.isBlank()) {
            list.add(value);
        }
    }

    private String firstCollectionValue(Object value) {
        if (value instanceof Collection<?> collection) {
            for (Object entry : collection) {
                String string = stringValue(entry);
                if (string != null && !string.isBlank()) {
                    return string;
                }
            }
        }
        return stringValue(value);
    }

    private String trimText(String content, int maxChars) {
        if (content == null) {
            return null;
        }
        String trimmed = content.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() <= maxChars ? trimmed : trimmed.substring(0, maxChars);
    }

    private String normalizeForSearch(String message) {
        if (message == null) {
            return "";
        }
        return Normalizer.normalize(message, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9\\s]", " ")
            .replaceAll("\\s+", " ")
            .trim();
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
        String string = Objects.toString(value, null);
        return string == null ? null : string;
    }

    private int sanitizeLimit(int requestedLimit) {
        return Math.max(1, Math.min(requestedLimit, 10));
    }
}
