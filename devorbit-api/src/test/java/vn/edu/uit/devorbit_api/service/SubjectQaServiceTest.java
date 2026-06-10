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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
            new ObjectMapper()
        );
    }

    @Test
    void processQuery_returnsFallbackAnswerWhenPersistenceAndSearchFail() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
        when(webSearchService.search("làm sao học tốt")).thenThrow(new RuntimeException("search unavailable"));
        when(openCodeAiService.generateCompletion(any(), eq("làm sao học tốt"))).thenReturn("Xin chào!");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest("làm sao học tốt", null));

        assertThat(response.answer()).isEqualTo("Xin chào!");
        assertThat(response.sessionId()).isNotNull();
        assertThat(response.relevantNodeIds()).isEmpty();
        assertThat(response.sources()).isEmpty();
        assertThat(response.type()).isEqualTo("SEARCH");
        verify(openCodeAiService).generateCompletion(any(), eq("làm sao học tốt"));
    }

    @Test
    void processQuery_searchQuestionUsesSemanticRetrievalAndFirecrawlBeforeLlm() {
        when(chatSessionRepository.save(any())).thenThrow(new RuntimeException("database unavailable"));
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
        when(webSearchService.search("làm sao học tốt SE104")).thenReturn(new WebSearchResponse(
            "success",
            List.of(new WebSearchResponse.WebSearchResult(
                "https://example.com/se104",
                "SE104 material",
                "Example",
                1
            ))
        ));
        when(firecrawlClient.scrape("https://example.com/se104"))
            .thenReturn(new FirecrawlClient.FirecrawlResult("# Firecrawl SE104 notes", "hash", "SE104 material"));

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setCourseCode("SE104");
        chunk.setSectionTitle("Objectives");
        chunk.setChunkText("SE104 teaches software engineering project practices.");
        when(knowledgeRetrievalService.search("SE104", "làm sao học tốt SE104", 5))
            .thenReturn(new KnowledgeRetrievalService.SearchResult(
                "SE104",
                "làm sao học tốt SE104",
                List.of(new KnowledgeRetrievalService.ChunkResult(chunk, 0.91))
            ));
        when(openCodeAiService.generateCompletion(any(), eq("làm sao học tốt SE104"))).thenReturn("Answer");

        SubjectQaResponse response = service.processQuery(new SubjectQaRequest("làm sao học tốt SE104", null));

        assertThat(response.answer()).isEqualTo("Answer");
        assertThat(response.sources()).containsExactly("https://example.com/se104");
        assertThat(response.type()).isEqualTo("SEARCH");
        verify(courseKnowledgeBootstrapService).ensureCourseIndexed(course, List.of(repo));
        verify(knowledgeRetrievalService).search("SE104", "làm sao học tốt SE104", 5);
        verify(firecrawlClient).scrape("https://example.com/se104");
        verify(openCodeAiService).generateCompletion(any(), eq("làm sao học tốt SE104"));
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
            new SubjectQaRequest("Gợi ý đồ án và tài liệu", null)
        );

        assertThat(response.answer())
            .contains("chưa có dữ liệu đủ")
            .contains("mã môn học cụ thể");
        assertThat(response.sources()).isEmpty();
        assertThat(response.type()).isEqualTo("DIRECT");
        verifyNoInteractions(webSearchService, crawlerService, openCodeAiService, firecrawlClient, knowledgeRetrievalService);
    }
}
