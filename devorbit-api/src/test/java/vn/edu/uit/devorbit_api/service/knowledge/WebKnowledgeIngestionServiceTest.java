package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.knowledge.CrawlRequest;
import vn.edu.uit.devorbit_api.dto.knowledge.WebImportRequest;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebKnowledgeIngestionServiceTest {

    @Mock
    private FirecrawlClient firecrawlClient;
    @Mock
    private KnowledgeSourceService knowledgeSourceService;
    @Mock
    private CourseKnowledgeIndexer courseKnowledgeIndexer;
    @Mock
    private KnowledgeEmbeddingService knowledgeEmbeddingService;
    @Mock
    private SyllabusFactExtractor syllabusFactExtractor;

    private WebKnowledgeIngestionService service;

    @BeforeEach
    void setUp() {
        service = new WebKnowledgeIngestionService(
            firecrawlClient, knowledgeSourceService,
            courseKnowledgeIndexer, knowledgeEmbeddingService,
            syllabusFactExtractor);
    }

    @Test
    void importUrl_newUrl_createsSourceAndChunks() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", false);
        String markdown = "# IT003 Course\nContent here";
        String hash = "abc123";

        when(firecrawlClient.scrape("https://example.com/course/IT003"))
            .thenReturn(new FirecrawlClient.FirecrawlResult(markdown, hash, "IT003 Course"));
        when(knowledgeSourceService.findByContentHash(hash))
            .thenReturn(Optional.empty());

        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setSourceType("WEB");
        when(knowledgeSourceService.createSource(
            eq("WEB"), any(), any(), any(), eq(hash), eq(markdown)))
            .thenReturn(source);

        KnowledgeSource result = service.importUrl(request);

        assertThat(result).isNotNull();
        verify(courseKnowledgeIndexer).indexMarkdown(source, "IT003", markdown);
        verify(knowledgeSourceService).createSource(
            eq("WEB"), any(), any(), any(), eq(hash), eq(markdown));
    }

    @Test
    void importUrl_existingHashSameStatus_skips() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", false);
        String markdown = "# IT003 Course\nContent here";
        String hash = "abc123";

        when(firecrawlClient.scrape("https://example.com/course/IT003"))
            .thenReturn(new FirecrawlClient.FirecrawlResult(markdown, hash, "IT003 Course"));

        KnowledgeSource existingSource = new KnowledgeSource();
        existingSource.setId(UUID.randomUUID());
        existingSource.setContentHash(hash);
        existingSource.setStatus("COMPLETED");
        when(knowledgeSourceService.findByContentHash(hash))
            .thenReturn(Optional.of(existingSource));

        KnowledgeSource result = service.importUrl(request);

        assertThat(result).isEqualTo(existingSource);
        verify(courseKnowledgeIndexer, never()).indexMarkdown(any(), any(), any());
        verify(knowledgeSourceService, never()).createSource(any(), any(), any(), any(), any(), any());
    }

    @Test
    void importUrl_changedContent_updatesSource() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", false);
        String markdown = "# IT003 Updated\nNew content";
        String hash = "newhash";

        when(firecrawlClient.scrape("https://example.com/course/IT003"))
            .thenReturn(new FirecrawlClient.FirecrawlResult(markdown, hash, "IT003 Updated"));

        // New hash not found → creates new source
        when(knowledgeSourceService.findByContentHash(hash))
            .thenReturn(Optional.empty());

        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setSourceType("WEB");
        when(knowledgeSourceService.createSource(
            eq("WEB"), any(), any(), any(), eq(hash), eq(markdown)))
            .thenReturn(source);

        KnowledgeSource result = service.importUrl(request);

        assertThat(result).isNotNull();
        verify(courseKnowledgeIndexer).indexMarkdown(source, "IT003", markdown);
    }

    @Test
    void importUrl_firecrawlFailure_marksSourceFailed() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", false);

        when(firecrawlClient.scrape("https://example.com/course/IT003"))
            .thenReturn(new FirecrawlClient.FirecrawlResult(null, null, null));

        assertThatThrownBy(() -> service.importUrl(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Failed to fetch URL");
    }

    @Test
    void importUrl_embedAfterImport_callsEmbedding() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", true);
        String markdown = "# IT003\nContent";
        String hash = "abc123";

        when(firecrawlClient.scrape("https://example.com/course/IT003"))
            .thenReturn(new FirecrawlClient.FirecrawlResult(markdown, hash, "IT003"));
        when(knowledgeSourceService.findByContentHash(hash))
            .thenReturn(Optional.empty());

        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setSourceType("WEB");
        when(knowledgeSourceService.createSource(
            eq("WEB"), any(), any(), any(), eq(hash), eq(markdown)))
            .thenReturn(source);

        KnowledgeSource result = service.importUrl(request);

        verify(knowledgeEmbeddingService).embedChunksForSource(source.getId(), true);
    }

    // ===== crawl tests =====

    @Test
    void crawlUrl_multiPage_createsMultipleSources() {
        CrawlRequest request = new CrawlRequest(
            "https://example.com/course", "IT003", "OFFICIAL", 3, false);

        FirecrawlClient.FirecrawlResult page1 =
            new FirecrawlClient.FirecrawlResult("# Page 1", "hash1", "Page 1");
        FirecrawlClient.FirecrawlResult page2 =
            new FirecrawlClient.FirecrawlResult("# Page 2", "hash2", "Page 2");

        when(firecrawlClient.crawl("https://example.com/course", 3))
            .thenReturn(List.of(page1, page2));
        when(knowledgeSourceService.findByContentHash("hash1")).thenReturn(Optional.empty());
        when(knowledgeSourceService.findByContentHash("hash2")).thenReturn(Optional.empty());

        KnowledgeSource src1 = new KnowledgeSource();
        src1.setId(UUID.randomUUID());
        src1.setSourceType("WEB");
        KnowledgeSource src2 = new KnowledgeSource();
        src2.setId(UUID.randomUUID());
        src2.setSourceType("WEB");

        when(knowledgeSourceService.createSource(
            eq("WEB"), any(), any(), any(), eq("hash1"), eq("# Page 1")))
            .thenReturn(src1);
        when(knowledgeSourceService.createSource(
            eq("WEB"), any(), any(), any(), eq("hash2"), eq("# Page 2")))
            .thenReturn(src2);

        List<KnowledgeSource> results = service.crawlUrl(request);

        assertThat(results).hasSize(2);
        verify(courseKnowledgeIndexer).indexMarkdown(src1, "IT003", "# Page 1");
        verify(courseKnowledgeIndexer).indexMarkdown(src2, "IT003", "# Page 2");
    }

    @Test
    void crawlUrl_skipsUnchangedPages() {
        CrawlRequest request = new CrawlRequest(
            "https://example.com/course", "IT003", "OFFICIAL", 2, false);

        FirecrawlClient.FirecrawlResult page =
            new FirecrawlClient.FirecrawlResult("# Cached", "existingHash", "Cached");

        when(firecrawlClient.crawl("https://example.com/course", 2))
            .thenReturn(List.of(page));

        KnowledgeSource existing = new KnowledgeSource();
        existing.setId(UUID.randomUUID());
        existing.setContentHash("existingHash");
        existing.setStatus("COMPLETED");
        when(knowledgeSourceService.findByContentHash("existingHash"))
            .thenReturn(Optional.of(existing));

        List<KnowledgeSource> results = service.crawlUrl(request);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(existing);
        verify(courseKnowledgeIndexer, never()).indexMarkdown(any(), any(), any());
    }

    @Test
    void crawlUrl_emptyResult_returnsEmptyList() {
        CrawlRequest request = new CrawlRequest(
            "https://example.com/empty", "IT003", "OFFICIAL", 5, false);

        when(firecrawlClient.crawl("https://example.com/empty", 5))
            .thenReturn(List.of());

        List<KnowledgeSource> results = service.crawlUrl(request);

        assertThat(results).isEmpty();
    }

    // ===== recrawl tests =====

    @Test
    void recrawl_changedContent_createsNewSource() {
        UUID sourceId = UUID.randomUUID();
        String url = "https://example.com/IT003";
        String newHash = "newHash";

        KnowledgeSource existing = new KnowledgeSource();
        existing.setId(sourceId);
        existing.setUrl(url);
        existing.setContentHash("oldHash");
        existing.setStatus("COMPLETED");
        when(knowledgeSourceService.findById(sourceId))
            .thenReturn(Optional.of(existing));

        FirecrawlClient.FirecrawlResult result =
            new FirecrawlClient.FirecrawlResult("# Updated Content", newHash, "Updated");
        when(firecrawlClient.scrape(url)).thenReturn(result);
        when(knowledgeSourceService.findByContentHash(newHash)).thenReturn(Optional.empty());

        KnowledgeSource newSource = new KnowledgeSource();
        newSource.setId(UUID.randomUUID());
        newSource.setSourceType("WEB");
        when(knowledgeSourceService.createSource(
            eq("WEB"), eq(url), eq(url), eq("Updated"), eq(newHash), eq("# Updated Content")))
            .thenReturn(newSource);

        KnowledgeSource resultSource = service.recrawl(sourceId);

        assertThat(resultSource).isEqualTo(newSource);
        verify(courseKnowledgeIndexer).indexMarkdown(newSource, null, "# Updated Content");
    }

    @Test
    void recrawl_unchangedContent_returnsExisting() {
        UUID sourceId = UUID.randomUUID();
        String url = "https://example.com/IT003";
        String hash = "sameHash";

        KnowledgeSource existing = new KnowledgeSource();
        existing.setId(sourceId);
        existing.setUrl(url);
        existing.setContentHash(hash);
        existing.setStatus("COMPLETED");
        when(knowledgeSourceService.findById(sourceId))
            .thenReturn(Optional.of(existing));

        FirecrawlClient.FirecrawlResult result =
            new FirecrawlClient.FirecrawlResult("# Same Content", hash, "Same");
        when(firecrawlClient.scrape(url)).thenReturn(result);
        when(knowledgeSourceService.findByContentHash(hash))
            .thenReturn(Optional.of(existing));

        KnowledgeSource resultSource = service.recrawl(sourceId);

        assertThat(resultSource).isEqualTo(existing);
        verify(courseKnowledgeIndexer, never()).indexMarkdown(any(), any(), any());
        verify(knowledgeSourceService, never()).createSource(any(), any(), any(), any(), any(), any());
    }

    @Test
    void recrawl_sourceNotFound_throwsException() {
        UUID sourceId = UUID.randomUUID();
        when(knowledgeSourceService.findById(sourceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recrawl(sourceId))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Source not found");
    }
}
