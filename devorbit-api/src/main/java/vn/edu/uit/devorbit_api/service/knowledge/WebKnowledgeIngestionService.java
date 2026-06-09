package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.knowledge.CrawlRequest;
import vn.edu.uit.devorbit_api.dto.knowledge.WebImportRequest;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Orchestrates web knowledge ingestion via Firecrawl.
 * Flow: URL → scrape → dedup by hash → save source → chunk → optional embed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebKnowledgeIngestionService {

    private final FirecrawlClient firecrawlClient;
    private final KnowledgeSourceService knowledgeSourceService;
    private final CourseKnowledgeIndexer courseKnowledgeIndexer;
    private final KnowledgeEmbeddingService knowledgeEmbeddingService;
    private final SyllabusFactExtractor syllabusFactExtractor;

    /**
     * Import a single URL through the Firecrawl → ingestion pipeline.
     * Skips if content hash unchanged and status is COMPLETED.
     */
    @Transactional
    public KnowledgeSource importUrl(WebImportRequest request) {
        // 1. Scrape via Firecrawl
        FirecrawlClient.FirecrawlResult result = firecrawlClient.scrape(request.url());
        if (!result.isSuccess()) {
            throw new BadRequestException("Failed to fetch URL: " + request.url());
        }

        String markdown = result.markdown();
        String hash = result.contentHash();
        String title = result.title();

        // 2. Check for existing source with same hash
        Optional<KnowledgeSource> existing = knowledgeSourceService.findByContentHash(hash);
        if (existing.isPresent() && "COMPLETED".equals(existing.get().getStatus())) {
            log.info("Source unchanged (hash match), skipping: {}", request.url());
            return existing.get();
        }

        // 3. Create new source
        String courseCode = request.courseCode();
        String trustLevel = request.trustLevel() != null ? request.trustLevel() : "OFFICIAL";

        KnowledgeSource source = knowledgeSourceService.createSource(
            "WEB", request.url(), request.url(), title, hash, markdown);
        source.setUrl(request.url());
        source.setTrustLevel(trustLevel);
        source.setStatus("COMPLETED");

        // Extract course code from title if not provided
        if (courseCode == null || courseCode.isBlank()) {
            courseCode = extractCourseCodeFromTitle(title);
        }

        // Index chunks
        courseKnowledgeIndexer.indexMarkdown(source, courseCode, markdown);

        // Optional embedding
        if (request.embedAfterImport()) {
            knowledgeEmbeddingService.embedChunksForSource(source.getId(), true);
        }

        log.info("Imported web source: {} (course: {}, {} chunks)",
            request.url(), courseCode,
            courseKnowledgeIndexer.getChunks(courseCode).size());

        return source;
    }

    /**
     * Extract course code from page title using common patterns.
     * Examples: "IT003 - Introduction to Programming", "CHƯƠNG TRÌNH IT003"
     */
    private String extractCourseCodeFromTitle(String title) {
        if (title == null) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("\\b([A-Z]{2}[0-9]{3})\\b")
            .matcher(title);
        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Crawl a website and ingest multiple pages.
     * Each page is processed independently: dedup → save → chunk → optional embed.
     */
    @Transactional
    public List<KnowledgeSource> crawlUrl(CrawlRequest request) {
        List<FirecrawlClient.FirecrawlResult> pages =
            firecrawlClient.crawl(request.url(), request.maxPages());

        List<KnowledgeSource> sources = new ArrayList<>();
        String trustLevel = request.trustLevel() != null ? request.trustLevel() : "OFFICIAL";

        for (FirecrawlClient.FirecrawlResult page : pages) {
            if (!page.isSuccess()) continue;

            String hash = page.contentHash();
            Optional<KnowledgeSource> existing = knowledgeSourceService.findByContentHash(hash);
            if (existing.isPresent() && "COMPLETED".equals(existing.get().getStatus())) {
                log.info("Crawl page unchanged (hash match), skipping: {}", page.title());
                sources.add(existing.get());
                continue;
            }

            String courseCode = request.courseCode();
            if (courseCode == null || courseCode.isBlank()) {
                courseCode = extractCourseCodeFromTitle(page.title());
            }

            KnowledgeSource source = knowledgeSourceService.createSource(
                "WEB", request.url(), request.url(), page.title(), hash, page.markdown());
            source.setTrustLevel(trustLevel);
            source.setStatus("COMPLETED");

            courseKnowledgeIndexer.indexMarkdown(source, courseCode, page.markdown());

            if (request.embedAfterImport()) {
                knowledgeEmbeddingService.embedChunksForSource(source.getId(), true);
            }

            sources.add(source);
        }

        log.info("Crawl completed: {} pages from {}", sources.size(), request.url());
        return sources;
    }

    /**
     * Re-crawl a URL by source ID. Compares content hash to detect changes.
     * If unchanged → returns existing source (SKIPPED).
     * If changed → creates new source with updated content.
     */
    @Transactional
    public KnowledgeSource recrawl(UUID sourceId) {
        KnowledgeSource existing = knowledgeSourceService.findById(sourceId)
            .orElseThrow(() -> new BadRequestException("Source not found: " + sourceId));

        FirecrawlClient.FirecrawlResult result = firecrawlClient.scrape(existing.getUrl());
        if (!result.isSuccess()) {
            throw new BadRequestException("Failed to re-fetch URL: " + existing.getUrl());
        }

        String newHash = result.contentHash();

        // Check if content is the same
        Optional<KnowledgeSource> existingByHash = knowledgeSourceService.findByContentHash(newHash);
        if (existingByHash.isPresent() && "COMPLETED".equals(existingByHash.get().getStatus())) {
            log.info("Recrawl unchanged (hash match), skipping: {}", existing.getUrl());
            return existingByHash.get();
        }

        // Content changed → create new source
        KnowledgeSource newSource = knowledgeSourceService.createSource(
            "WEB", existing.getUrl(), existing.getUrl(),
            result.title(), newHash, result.markdown());
        newSource.setTrustLevel(existing.getTrustLevel());
        newSource.setStatus("COMPLETED");

        courseKnowledgeIndexer.indexMarkdown(newSource, null, result.markdown());

        log.info("Recrawl completed: {} (changed from source {})", existing.getUrl(), sourceId);
        return newSource;
    }
}
