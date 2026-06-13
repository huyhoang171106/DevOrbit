package vn.edu.uit.devorbit_api.service.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.config.ExaProperties;
import vn.edu.uit.devorbit_api.dto.publicapi.WebSearchResponse;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlClient;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlProperties;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSearchServiceTest {

    @Mock
    private ExaWebSearchClient exaWebSearchClient;

    @Mock
    private FirecrawlClient firecrawlClient;

    private ExaProperties exaProperties;
    private FirecrawlProperties firecrawlProperties;
    private WebSearchService service;

    @BeforeEach
    void setUp() {
        exaProperties = new ExaProperties();
        exaProperties.setEnabled(true);
        exaProperties.setApiKey("exa-test-key");
        exaProperties.setSearchType("auto");
        exaProperties.setNumResults(10);
        exaProperties.setMaxAgeHours(24);
        exaProperties.setTimeoutSeconds(15);

        firecrawlProperties = new FirecrawlProperties();
        firecrawlProperties.setEnabled(true);
        firecrawlProperties.setApiKey("firecrawl-test-key");
        firecrawlProperties.setTimeoutSeconds(15);
        firecrawlProperties.setMaxPages(10);

        service = new WebSearchService(exaWebSearchClient, exaProperties, firecrawlClient, firecrawlProperties);
    }

    @Test
    void search_prefersExaAndMapsHighlightsWithTrustedDomains() {
        when(exaWebSearchClient.search(anyMap())).thenReturn(Map.of(
            "results", List.of(
                Map.of(
                    "url", "https://www.studocu.vn/vn/course/truong-dai-hoc-cong-nghe-thong-tin-dai-hoc-quoc-gia-thanh-pho-ho-chi-minh/nhap-mon-cong-nghe-phan-mem/5268006",
                    "title", "Studocu Vietnam",
                    "snippet", "Generic aggregated notes should be ranked after local UIT sources.",
                    "highlights", List.of("Studocu note about SE104."),
                    "publishedDate", "2026-05-15",
                    "author", "Studocu"
                ),
                Map.of(
                    "url", "https://forum.uit.edu.vn/t/se104-study-guide",
                    "title", "SE104 Study Guide",
                    "snippet", "Useful study guide for SE104",
                    "highlights", List.of(
                        "This is a detailed highlight about SE104 and how to study effectively.",
                        "Another helpful highlight with enough context to show the parser works."
                    ),
                    "publishedDate", "2026-06-01",
                    "author", "UIT Forum"
                )
            )
        ));

        ArgumentCaptor<Map<String, Object>> requestCaptor = ArgumentCaptor.forClass(Map.class);

        WebSearchResponse response = service.search("làm sao học tốt SE104");

        verify(exaWebSearchClient).search(requestCaptor.capture());
        verify(firecrawlClient, never()).search(anyString(), org.mockito.ArgumentMatchers.anyInt());

        Map<String, Object> request = requestCaptor.getValue();
        assertThat(request.get("query")).isEqualTo("làm sao học tốt SE104");
        assertThat(request.get("type")).isEqualTo("auto");
        assertThat(request.get("numResults")).isEqualTo(10);
        assertThat((List<String>) request.get("includeDomains")).contains("forum.uit.edu.vn", "github.com");
        assertThat((List<String>) request.get("excludeDomains")).contains("youtube.com", "tiktok.com");

        @SuppressWarnings("unchecked")
        Map<String, Object> contents = (Map<String, Object>) request.get("contents");
        assertThat(contents.get("highlights")).isEqualTo(true);
        assertThat(request.get("maxAgeHours")).isEqualTo(24);

        assertThat(response.status()).isEqualTo("success");
        assertThat(response.web()).hasSize(2);
        assertThat(response.web().get(0).url()).isEqualTo("https://forum.uit.edu.vn/t/se104-study-guide");
        assertThat(response.web().get(0).position()).isEqualTo(1);
        assertThat(response.web().get(0).sourceProvider()).isEqualTo("exa");
        assertThat(response.web().get(1).url()).contains("studocu.vn");
        assertThat(response.web().get(1).position()).isEqualTo(2);
        assertThat(response.web().get(0).hasHighlights()).isTrue();
        assertThat(response.web().get(0).highlights()).hasSize(2);
        assertThat(response.web().get(0).publishedDate()).isEqualTo("2026-06-01");
    }

    @Test
    void search_fallsBackToFirecrawlWhenExaDisabled() {
        exaProperties.setEnabled(false);
        when(firecrawlClient.search("làm sao học tốt SE104", 10)).thenReturn(List.of(
            new FirecrawlClient.FirecrawlSearchResult(
                "https://example.com/firecrawl-result",
                "Firecrawl Result",
                "A fallback result from Firecrawl",
                "Markdown body from Firecrawl",
                "https://example.com/firecrawl-result"
            )
        ));

        WebSearchResponse response = service.search("làm sao học tốt SE104");

        verify(exaWebSearchClient, never()).search(anyMap());
        verify(firecrawlClient).search("làm sao học tốt SE104", 10);

        assertThat(response.status()).isEqualTo("fallback");
        assertThat(response.web()).hasSize(1);
        assertThat(response.web().get(0).sourceProvider()).isEqualTo("firecrawl");
        assertThat(response.web().get(0).highlights()).isNotEmpty();
        assertThat(response.web().get(0).url()).isEqualTo("https://example.com/firecrawl-result");
    }

    @Test
    void search_fallsBackToFirecrawlWhenExaReturnsNoResults() {
        // Exa enabled but returns empty results
        when(exaWebSearchClient.search(anyMap())).thenReturn(Map.of(
            "results", List.of()
        ));
        when(firecrawlClient.search("làm sao học tốt SE104", 10)).thenReturn(List.of(
            new FirecrawlClient.FirecrawlSearchResult(
                "https://example.com/firecrawl-fallback",
                "Firecrawl Fallback",
                "A fallback result from Firecrawl after Exa empty",
                "Markdown body from Firecrawl",
                "https://example.com/firecrawl-fallback"
            )
        ));

        WebSearchResponse response = service.search("làm sao học tốt SE104");

        assertThat(response.status()).isEqualTo("fallback");
        assertThat(response.web()).hasSize(1);
        assertThat(response.web().get(0).sourceProvider()).isEqualTo("firecrawl");
        verify(exaWebSearchClient).search(anyMap());
        verify(firecrawlClient).search("làm sao học tốt SE104", 10);
    }

    @Test
    void search_returnsEmptyWhenBothProvidersUnavailable() {
        exaProperties.setEnabled(false);
        firecrawlProperties.setEnabled(false);

        WebSearchResponse response = service.search("làm sao học tốt SE104");

        assertThat(response.status()).isEqualTo("empty");
        assertThat(response.web()).isEmpty();
        verifyNoInteractions(exaWebSearchClient, firecrawlClient);
    }

    @Test
    void search_filtersNoiseDomainsFromFirecrawl() {
        exaProperties.setEnabled(false);
        firecrawlProperties.setEnabled(true);
        // Firecrawl returns noise domains + UIT domain
        when(firecrawlClient.search("SE104 tài liệu", 10)).thenReturn(List.of(
            new FirecrawlClient.FirecrawlSearchResult(
                "https://youtube.com/watch?v=se104",
                "YouTube Video",
                "A video about SE104",
                "Markdown from YouTube",
                "https://youtube.com/watch?v=se104"
            ),
            new FirecrawlClient.FirecrawlSearchResult(
                "https://forum.uit.edu.vn/t/se104-study-guide",
                "UIT Forum SE104",
                "SE104 study guide on UIT forum",
                "Markdown from UIT",
                "https://forum.uit.edu.vn/t/se104-study-guide"
            )
        ));

        WebSearchResponse response = service.search("SE104 tài liệu");

        // Should filter out YouTube, keep UIT result only
        assertThat(response.status()).isEqualTo("fallback");
        assertThat(response.web()).hasSize(1);
        assertThat(response.web().get(0).url()).isEqualTo("https://forum.uit.edu.vn/t/se104-study-guide");
    }
}
