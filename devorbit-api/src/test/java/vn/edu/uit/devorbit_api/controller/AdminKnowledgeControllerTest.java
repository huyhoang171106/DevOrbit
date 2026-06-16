package vn.edu.uit.devorbit_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.uit.devorbit_api.dto.knowledge.*;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;
import vn.edu.uit.devorbit_api.service.knowledge.*;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminKnowledgeController.class)
class AdminKnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminKnowledgeQueryService queryService;
    @MockitoBean
    private AdminKnowledgeCommandService commandService;
    @MockitoBean
    private KnowledgeEmbeddingService embeddingService;
    @MockitoBean
    private KnowledgeRetrievalService retrievalService;
    @MockitoBean
    private StudentUserRepository studentUserRepository;
    @MockitoBean
    private FirecrawlKnowledgeImporter firecrawlKnowledgeImporter;
    @MockitoBean
    private WebKnowledgeIngestionService webKnowledgeIngestionService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RevokedTokenStore revokedTokenStore;
    @MockitoBean
    private CacheManager cacheManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void importUrl_delegatesToImporter() throws Exception {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/IT003", "IT003", "OFFICIAL", false);
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setSourceType("WEB");
        when(firecrawlKnowledgeImporter.importUrl(any())).thenReturn(source);

        mockMvc.perform(post("/api/admin/knowledge/import-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sourceType").value("WEB"));
    }

    @Test
    void crawlUrl_delegatesToIngestionService() throws Exception {
        CrawlRequest request = new CrawlRequest(
            "https://example.com/course", "IT003", "OFFICIAL", 3, false);
        KnowledgeSource src1 = new KnowledgeSource();
        src1.setId(UUID.randomUUID());
        src1.setSourceType("WEB");
        when(webKnowledgeIngestionService.crawlUrl(any())).thenReturn(List.of(src1));

        mockMvc.perform(post("/api/admin/knowledge/crawl-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].sourceType").value("WEB"));
    }

    @Test
    void recrawl_delegatesToIngestionService() throws Exception {
        UUID sourceId = UUID.randomUUID();
        KnowledgeSource updated = new KnowledgeSource();
        updated.setId(sourceId);
        updated.setSourceType("WEB");
        updated.setUrl("https://example.com/IT003");
        when(webKnowledgeIngestionService.recrawl(sourceId)).thenReturn(updated);

        mockMvc.perform(post("/api/admin/knowledge/sources/" + sourceId + "/recrawl"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.com/IT003"));
    }

    @Test
    void ragPreview_returnsRetrievedChunksAndPrompt() throws Exception {
        RagPreviewRequest request = new RagPreviewRequest("IT003", "quy hoạch động", 5);

        SearchResponse.SearchResult chunk = new SearchResponse.SearchResult(
            "chunk-1", "src-1", "IT003", "Session 5", 10, 12, 0.85, "Quy hoạch động...", "file.pdf", "http://example.com");

        KnowledgeRetrievalService.SearchResult searchResult =
            new KnowledgeRetrievalService.SearchResult("IT003", "quy hoạch động", List.of());
        when(retrievalService.search(eq("IT003"), eq("quy hoạch động"), eq(5)))
            .thenReturn(searchResult);

        mockMvc.perform(post("/api/admin/knowledge/rag-preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courseCode").value("IT003"))
            .andExpect(jsonPath("$.query").value("quy hoạch động"))
            .andExpect(jsonPath("$.topK").value(5));
    }
}
