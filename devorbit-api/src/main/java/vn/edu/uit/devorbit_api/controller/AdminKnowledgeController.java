package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.knowledge.*;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.service.knowledge.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin endpoints for knowledge ingestion, embedding, and search.
 * All endpoints require ROLE_ADMIN.
 * Delegates to Query/Command services — no direct repository access.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/knowledge")
@RequiredArgsConstructor
public class AdminKnowledgeController {

    private final AdminKnowledgeQueryService queryService;
    private final AdminKnowledgeCommandService commandService;
    private final KnowledgeEmbeddingService embeddingService;
    private final KnowledgeRetrievalService retrievalService;
    private final FirecrawlKnowledgeImporter firecrawlKnowledgeImporter;
    private final WebKnowledgeIngestionService webKnowledgeIngestionService;

    @PostMapping("/ingest-folder")
    public FolderIngestionSummary ingestFolder() {
        return commandService.ingestFolder();
    }

    @PostMapping("/ingest-file")
    public IngestionReport ingestFile(@RequestBody IngestFileRequest request) {
        return commandService.ingestFile(request.filePath());
    }

    @GetMapping("/sources")
    public List<KnowledgeSourceResponse> listSources() {
        return queryService.listSources();
    }

    @GetMapping("/courses/{courseCode}")
    public SyllabusDetailsResponse getCourseDetails(@PathVariable String courseCode) {
        return queryService.getCourseDetails(courseCode);
    }

    @GetMapping("/courses/{courseCode}/chunks")
    public List<KnowledgeChunk> getCourseChunks(@PathVariable String courseCode) {
        return queryService.getCourseChunks(courseCode);
    }

    @PostMapping("/courses/{courseCode}/embed")
    public EmbedResponse embedCourse(
            @PathVariable String courseCode,
            @RequestParam(defaultValue = "false") boolean force) {
        List<KnowledgeChunk> chunks = queryService.getCourseChunks(courseCode);
        int total = chunks.size();
        int embedded = embeddingService.embedChunksForCourse(courseCode, force);
        return new EmbedResponse(
            "OK",
            embedded,
            total,
            force ? "Force re-embedded " + embedded + " chunks" : "Embedded " + embedded + " new chunks (skipped " + (total - embedded) + " already embedded)"
        );
    }

    @PostMapping("/sources/{sourceId}/embed")
    public EmbedResponse embedSource(
            @PathVariable UUID sourceId,
            @RequestParam(defaultValue = "false") boolean force) {
        int embedded = embeddingService.embedChunksForSource(sourceId, force);
        return new EmbedResponse(
            "OK",
            embedded,
            embedded,
            force ? "Force re-embedded " + embedded + " chunks" : "Embedded " + embedded + " new chunks"
        );
    }

    @PostMapping("/search")
    public SearchResponse search(@RequestBody SearchRequest request) {
        return retrievalService.search(request);
    }

    @PostMapping("/import-url")
    public KnowledgeSource importUrl(@RequestBody WebImportRequest request) {
        return firecrawlKnowledgeImporter.importUrl(request);
    }

    @PostMapping("/crawl-url")
    public List<KnowledgeSource> crawlUrl(@RequestBody CrawlRequest request) {
        return webKnowledgeIngestionService.crawlUrl(request);
    }

    @PostMapping("/sources/{sourceId}/recrawl")
    public KnowledgeSource recrawl(@PathVariable UUID sourceId) {
        return webKnowledgeIngestionService.recrawl(sourceId);
    }

    @PostMapping("/rag-preview")
    public RagPreviewResponse ragPreview(@RequestBody RagPreviewRequest request) {
        KnowledgeRetrievalService.SearchResult searchResult =
            retrievalService.search(request.courseCode(), request.query(), request.topK());
        List<SearchResponse.SearchResult> chunkResults = searchResult.chunks().stream()
            .map(cr -> {
                KnowledgeChunk chunk = cr.chunk();
                return new SearchResponse.SearchResult(
                    chunk.getId() != null ? chunk.getId().toString() : null,
                    null,
                    chunk.getCourseCode(),
                    chunk.getSectionTitle(),
                    chunk.getPageFrom(),
                    chunk.getPageTo(),
                    cr.score(),
                    chunk.getChunkText());
            })
            .toList();
        String prompt = String.format(
            "Câu hỏi: %s\nMôn học: %s\nSố chunk liên quan: %d\n\nDữ liệu:\n%s",
            request.query(), request.courseCode(), chunkResults.size(),
            chunkResults.stream().map(SearchResponse.SearchResult::text).reduce(
                "", (a, b) -> a + "\n---\n" + b));
        return new RagPreviewResponse(
            request.courseCode(), request.query(), request.topK(),
            chunkResults, prompt);
    }
}
