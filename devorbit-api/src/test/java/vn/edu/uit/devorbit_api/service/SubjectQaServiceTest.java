package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.publicapi.SubjectQaRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.SubjectQaResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.WebSearchResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.repository.ChatMessageRepository;
import vn.edu.uit.devorbit_api.repository.ChatSessionRepository;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.service.ai.CrawlerService;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;
import vn.edu.uit.devorbit_api.service.ai.WebSearchService;
import vn.edu.uit.devorbit_api.service.knowledge.CourseKnowledgeBootstrapService;
import vn.edu.uit.devorbit_api.service.knowledge.FirecrawlClient;
import vn.edu.uit.devorbit_api.service.knowledge.KnowledgeRetrievalService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectQaServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private GithubRepoRepository githubRepoRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private WebSearchService webSearchService;

    @Mock
    private CrawlerService crawlerService;

    @Mock
    private OpenCodeAiService openCodeAiService;

    @Mock
    private FirecrawlClient firecrawlClient;

    @Mock
    private CourseKnowledgeBootstrapService courseKnowledgeBootstrapService;

    @Mock
    private KnowledgeRetrievalService knowledgeRetrievalService;

    private SubjectQaService service;

    @BeforeEach
    void setUp() {
        service = new SubjectQaService(
            courseRepository,
            githubRepoRepository,
            chatSessionRepository,
            chatMessageRepository,
            webSearchService,
            crawlerService,
            openCodeAiService,
            firecrawlClient,
            courseKnowledgeBootstrapService,
            knowledgeRetrievalService,
            new ObjectMapper(),
            Runnable::run
        );
    }

    @Test
    void processQuery_returnsFallbackAnswerWhenPersistenceAndSearchFail() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        String query = "lam sao hoc tot";
        when(webSearchService.search(query)).thenThrow(new RuntimeException("search unavailable"));
        when(openCodeAiService.generateCompletion(any(), eq(query))).thenReturn("Xin chào!");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest(query, null));

        assertThat(response.answer()).isEqualTo("Xin chào!");
        assertThat(response.sources()).isEmpty();
        assertThat(response.type()).isEqualTo("SEARCH");
        verify(webSearchService).search(query);
        verify(openCodeAiService).generateCompletion(any(), eq(query));
    }

    @Test
    void processQuery_searchQuestionUsesSemanticRetrievalAndFirecrawlBeforeLlm() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        String query = "lam sao hoc tot SE104";
        Course course = Course.builder()
            .id(104L)
            .maMH("SE104")
            .tenMH("Nhap mon cong nghe phan mem")
            .loaiMonHoc("CO_SO_NGANH")
            .build();
        GithubRepo repo = GithubRepo.builder()
            .repoName("se104-project")
            .githubUrl("https://github.com/example/se104-project")
            .description("A linked SE104 project")
            .stars(3)
            .build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));
        when(githubRepoRepository.findByCourseIdAndActiveTrue(104L)).thenReturn(List.of(repo));
        when(webSearchService.search(query)).thenReturn(new WebSearchResponse(
            "success",
            List.of(new WebSearchResponse.WebSearchResult(
                "https://example.com/se104",
                "SE104 material",
                "Example",
                1,
                List.of("A short excerpt"),
                null,
                null,
                "exa"
            ))
        ));
        when(firecrawlClient.scrape("https://example.com/se104"))
            .thenReturn(new FirecrawlClient.FirecrawlResult("# Firecrawl SE104 notes", "hash", "SE104 material"));

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setCourseCode("SE104");
        chunk.setSectionTitle("Objectives");
        chunk.setChunkText("SE104 teaches software engineering project practices.");
        when(knowledgeRetrievalService.search("SE104", query, 5))
            .thenReturn(new KnowledgeRetrievalService.SearchResult(
                "SE104",
                query,
                List.of(new KnowledgeRetrievalService.ChunkResult(chunk, 0.91))
            ));
        when(openCodeAiService.generateCompletion(any(), eq(query))).thenReturn("Answer");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest(query, null));

        assertThat(response.answer()).isEqualTo("Answer");
        assertThat(response.sources()).containsExactly("https://example.com/se104");
        assertThat(response.searchResults()).hasSize(1);
        assertThat(response.searchResults().get(0).sourceProvider()).isEqualTo("exa");
        assertThat(response.type()).isEqualTo("SEARCH");
        verify(courseKnowledgeBootstrapService).ensureCourseIndexed(course, List.of(repo));
        verify(knowledgeRetrievalService).search("SE104", query, 5);
        verify(webSearchService).search(query);
        verify(firecrawlClient).scrape("https://example.com/se104");
        verify(openCodeAiService).generateCompletion(any(), eq(query));
    }

    @Test
    void processQuery_greetingReturnsGroundedIntroWithoutSearchOrLlm() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest("Helo", null));

        assertThat(response.answer()).contains("repository GitHub").contains("SE104");
        assertThat(response.relevantNodeIds()).isEmpty();
        assertThat(response.sources()).isEmpty();
        assertThat(response.type()).isEqualTo("DIRECT");
        verifyNoInteractions(webSearchService, crawlerService, openCodeAiService, firecrawlClient, knowledgeRetrievalService);
    }

    @Test
    void processQuery_internalResourceQuestionWithoutCourseCodeAsksForSpecificCourse() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));

        SubjectQaResponse response = service.processQuery(
            new SubjectQaRequest("goi y do an va tai lieu", null)
        );

        assertThat(response.answer())
            .contains("SE104")
            .contains("MA006")
            .contains("IS201");
        assertThat(response.sources()).isEmpty();
        assertThat(response.type()).isEqualTo("DIRECT");
        verifyNoInteractions(webSearchService, crawlerService, openCodeAiService, firecrawlClient, knowledgeRetrievalService);
    }

    @Test
    void processQuery_searchQuestionWithRichHighlightsSkipsFirecrawl() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        String query = "lam sao hoc tot SE104";
        Course course = Course.builder()
            .id(104L)
            .maMH("SE104")
            .tenMH("Nhap mon cong nghe phan mem")
            .loaiMonHoc("CO_SO_NGANH")
            .build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));
        when(githubRepoRepository.findByCourseIdAndActiveTrue(104L)).thenReturn(List.of());
        when(webSearchService.search(query)).thenReturn(new WebSearchResponse(
            "success",
            List.of(new WebSearchResponse.WebSearchResult(
                "https://example.com/se104-rich",
                "SE104 Study Guide",
                "A detailed guide for SE104 students",
                1,
                List.of(
                    "This highlight is intentionally long enough to count as rich context for the model and should avoid Firecrawl."
                        + " It includes concrete advice, study structure, and links to the same page.",
                    "Second rich highlight with enough text to satisfy the threshold and keep Firecrawl unnecessary."
                ),
                null,
                null,
                "exa"
            ))
        ));
        when(knowledgeRetrievalService.search("SE104", query, 5))
            .thenReturn(new KnowledgeRetrievalService.SearchResult(
                "SE104",
                query,
                List.of()
            ));
        when(openCodeAiService.generateCompletion(any(), eq(query))).thenReturn("Answer");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest(query, null));

        assertThat(response.sources()).containsExactly("https://example.com/se104-rich");
        assertThat(response.searchResults()).hasSize(1);
        assertThat(response.answer()).isEqualTo("Answer");
        assertThat(response.type()).isEqualTo("SEARCH");
        verify(webSearchService).search(query);
        verifyNoInteractions(firecrawlClient);
        verify(openCodeAiService).generateCompletion(any(), eq(query));
    }

    @Test
    void processQuery_courseQuestionWithNoRagChunksUsesWebFallback() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        String query = "SE104 hoc phan nay co tai lieu gi";
        Course course = Course.builder()
            .id(104L)
            .maMH("SE104")
            .tenMH("Nhap mon cong nghe phan mem")
            .loaiMonHoc("CO_SO_NGANH")
            .build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));
        when(githubRepoRepository.findByCourseIdAndActiveTrue(104L)).thenReturn(List.of());
        // RAG returns empty
        when(knowledgeRetrievalService.search("SE104", query, 5))
            .thenReturn(new KnowledgeRetrievalService.SearchResult("SE104", query, List.of()));
        // Web should be called because no local chunks and course detected
        when(webSearchService.search(query)).thenReturn(new WebSearchResponse(
            "success",
            List.of(new WebSearchResponse.WebSearchResult(
                "https://example.com/se104-material",
                "SE104 Material",
                "External SE104 study guide",
                1,
                List.of("A short excerpt"),
                null,
                null,
                "exa"
            ))
        ));
        when(openCodeAiService.generateCompletion(any(), eq(query))).thenReturn("Answer from web");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest(query, null));

        assertThat(response.answer()).isEqualTo("Answer from web");
        assertThat(response.type()).isEqualTo("SEARCH");
        assertThat(response.searchResults()).hasSize(1);
        assertThat(response.searchResults().get(0).sourceProvider()).isEqualTo("exa");
        verify(knowledgeRetrievalService).search("SE104", query, 5);
        verify(webSearchService).search(query);
    }

    @Test
    void processQuery_courseQuestionWithGoodRagAndNoSearchIntentSkipsWeb() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        String query = "SE104 muc tieu mon hoc";
        Course course = Course.builder()
            .id(104L)
            .maMH("SE104")
            .tenMH("Nhap mon cong nghe phan mem")
            .loaiMonHoc("CO_SO_NGANH")
            .build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));
        when(githubRepoRepository.findByCourseIdAndActiveTrue(104L)).thenReturn(List.of());
        // RAG returns a high-score chunk
        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setCourseCode("SE104");
        chunk.setSectionTitle("Muc tieu");
        chunk.setChunkText("Mon hoc gioi thieu ve cong nghe phan mem.");
        when(knowledgeRetrievalService.search("SE104", query, 5))
            .thenReturn(new KnowledgeRetrievalService.SearchResult("SE104", query,
                List.of(new KnowledgeRetrievalService.ChunkResult(chunk, 0.85))));
        // No search intent in query
        when(openCodeAiService.generateCompletion(any(), eq(query))).thenReturn("Answer from RAG");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest(query, null));

        assertThat(response.answer()).isEqualTo("Answer from RAG");
        assertThat(response.type()).isEqualTo("DIRECT");
        verify(knowledgeRetrievalService).search("SE104", query, 5);
        verifyNoInteractions(webSearchService);
        // LLM should get RAG context in prompt
        verify(openCodeAiService).generateCompletion(argThat(prompt ->
            prompt.contains("Chunk RAG") && prompt.contains("score=0.85")), eq(query));
    }
}