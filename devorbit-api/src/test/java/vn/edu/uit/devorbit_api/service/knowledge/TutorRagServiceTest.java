package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.knowledge.Citation;
import vn.edu.uit.devorbit_api.dto.knowledge.TutorResponse;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorRagServiceTest {

    @Mock private CourseCodeDetector courseCodeDetector;
    @Mock private TutorIntentClassifier intentClassifier;
    @Mock private CourseFactQueryService courseFactQueryService;
    @Mock private KnowledgeRetrievalService knowledgeRetrievalService;
    @Mock private OpenCodeAiService openCodeAiService;
    @Mock private CitationBuilder citationBuilder;

    private TutorRagService service;

    @BeforeEach
    void setUp() {
        service = new TutorRagService(
            courseCodeDetector, intentClassifier, courseFactQueryService,
            knowledgeRetrievalService, openCodeAiService, citationBuilder);
    }

    @Test
    void answer_factQuery_credits_doesNotCallLlm() {
        when(courseCodeDetector.detect("IT003 mấy tín chỉ?")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("IT003 mấy tín chỉ?")).thenReturn(TutorIntent.FACT_QUERY);
        when(courseFactQueryService.getFact("IT003", "credits")).thenReturn(Optional.of("4 tín chỉ"));

        TutorResponse response = service.answer("IT003 mấy tín chỉ?");

        assertThat(response.answer()).contains("4 tín chỉ");
        assertThat(response.confidence()).isEqualTo("HIGH");
        verifyNoInteractions(openCodeAiService);
    }

    @Test
    void answer_factQuery_prerequisite_doesNotCallLlm() {
        when(courseCodeDetector.detect("IT003 cần học trước môn nào?")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("IT003 cần học trước môn nào?")).thenReturn(TutorIntent.FACT_QUERY);
        when(courseFactQueryService.getFact("IT003", "prerequisite")).thenReturn(Optional.of("Nhập môn lập trình"));

        TutorResponse response = service.answer("IT003 cần học trước môn nào?");

        assertThat(response.answer()).contains("Nhập môn lập trình");
        verifyNoInteractions(openCodeAiService);
    }

    @Test
    void answer_ragQuery_retrievesChunksAndCallsLlm() {
        when(courseCodeDetector.detect("Quy hoạch động trong IT003 nằm phần nào?")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("Quy hoạch động trong IT003 nằm phần nào?")).thenReturn(TutorIntent.FACT_QUERY);
        when(courseFactQueryService.getFact("IT003", "unknown")).thenReturn(Optional.empty());

        // Fact not found in DB → fall back to RAG
        when(knowledgeRetrievalService.search(eq("IT003"), anyString(), eq(5)))
            .thenReturn(new KnowledgeRetrievalService.SearchResult("IT003", "query", List.of()));
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(openCodeAiService.generateCompletion(anyString(), anyString()))
            .thenReturn("Quy hoạch động nằm ở Session 5-6 trong phần Lý thuyết.");
        when(citationBuilder.buildCitations(any(), eq("IT003")))
            .thenReturn(List.of());

        TutorResponse response = service.answer("Quy hoạch động trong IT003 nằm phần nào?");

        assertThat(response.answer()).isNotBlank();
        verify(knowledgeRetrievalService).search(eq("IT003"), anyString(), eq(5));
    }

    @Test
    void answer_noCourseCode_generalQuery_callsLlm() {
        when(courseCodeDetector.detect("Hello")).thenReturn(Optional.empty());
        when(intentClassifier.classify("Hello")).thenReturn(TutorIntent.GENERAL_RAG);
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(knowledgeRetrievalService.search(isNull(), eq("Hello"), eq(5)))
            .thenReturn(new KnowledgeRetrievalService.SearchResult(null, "Hello", List.of()));
        when(openCodeAiService.generateCompletion(anyString(), anyString()))
            .thenReturn("Xin chào! Mình là DevOrbit AI Tutor.");
        when(citationBuilder.buildCitations(any(), isNull()))
            .thenReturn(List.of());

        TutorResponse response = service.answer("Hello");

        assertThat(response.answer()).contains("DevOrbit AI Tutor");
        verify(knowledgeRetrievalService).search(isNull(), eq("Hello"), eq(5));
    }

    @Test
    void answer_llmDisabled_returnsOfflineResponse() {
        when(courseCodeDetector.detect("Hello")).thenReturn(Optional.empty());
        when(intentClassifier.classify("Hello")).thenReturn(TutorIntent.GENERAL_RAG);
        when(openCodeAiService.isLlmEnabled()).thenReturn(false);

        TutorResponse response = service.answer("Hello");

        assertThat(response.answer()).isNotBlank();
        assertThat(response.confidence()).isEqualTo("LOW");
        verifyNoInteractions(knowledgeRetrievalService);
    }

    @Test
    void answer_factQuery_assessment_doesNotCallLlm() {
        when(courseCodeDetector.detect("A2 IT003 bao nhiêu phần trăm?")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("A2 IT003 bao nhiêu phần trăm?")).thenReturn(TutorIntent.FACT_QUERY);
        when(courseFactQueryService.getFact("IT003", "assessment:A2")).thenReturn(Optional.of("A2: 20%"));

        TutorResponse response = service.answer("A2 IT003 bao nhiêu phần trăm?");

        assertThat(response.answer()).contains("20%");
        verifyNoInteractions(openCodeAiService);
    }

    @Test
    void answer_ragQuery_emptyHybridResultsKeepsLowConfidenceAndCallsLlm() {
        when(courseCodeDetector.detect("IT003 something")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("IT003 something")).thenReturn(TutorIntent.GENERAL_RAG);
        when(knowledgeRetrievalService.search(eq("IT003"), anyString(), eq(5)))
            .thenReturn(new KnowledgeRetrievalService.SearchResult("IT003", "query", List.of()));
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(openCodeAiService.generateCompletion(anyString(), anyString()))
            .thenReturn("Not found in DB.");
        when(citationBuilder.buildCitations(any(), eq("IT003")))
            .thenReturn(List.of());

        TutorResponse response = service.answer("IT003 something");

        assertThat(response.answer()).isNotBlank();
        assertThat(response.citations()).isEmpty();
        assertThat(response.confidence()).isEqualTo("LOW");
        verify(openCodeAiService).generateCompletion(anyString(), anyString());
    }

    @Test
    void answer_ragQuery_containsScoreInContext() {
        when(courseCodeDetector.detect("IT003 nội dung môn học")).thenReturn(Optional.of("IT003"));
        when(intentClassifier.classify("IT003 nội dung môn học")).thenReturn(TutorIntent.FACT_QUERY);
        when(courseFactQueryService.getFact("IT003", "unknown")).thenReturn(Optional.empty());

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setCourseCode("IT003");
        chunk.setSectionTitle("Cấu trúc dữ liệu");
        chunk.setChunkText("Nội dung về cấu trúc dữ liệu và giải thuật.");

        when(knowledgeRetrievalService.search(eq("IT003"), anyString(), eq(5)))
            .thenReturn(new KnowledgeRetrievalService.SearchResult("IT003", "query",
                List.of(new KnowledgeRetrievalService.ChunkResult(chunk, 0.85))));
        when(openCodeAiService.isLlmEnabled()).thenReturn(true);
        when(openCodeAiService.generateCompletion(anyString(), anyString()))
            .thenReturn("Cấu trúc dữ liệu là một môn học quan trọng.");
        when(citationBuilder.buildCitations(any(), eq("IT003")))
            .thenReturn(List.of(new Citation(
                UUID.randomUUID(), "Syllabus", "url", "Cấu trúc dữ liệu", 1, 10, 0)));

        TutorResponse response = service.answer("IT003 nội dung môn học");

        assertThat(response.answer()).isNotBlank();
        verify(openCodeAiService).generateCompletion(argThat(prompt -> prompt.contains("score=0.85")), anyString());
    }
}
