package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchRequest;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchResponse;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;
import vn.edu.uit.devorbit_api.service.ai.EmbeddingService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeRetrievalServiceTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private RagQueryPlanner ragQueryPlanner;

    private final RagResultReranker realReranker = new RagResultReranker();

    private KnowledgeRetrievalService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeRetrievalService(
                knowledgeChunkRepository, embeddingService, ragQueryPlanner, realReranker);
        lenient().when(embeddingService.embed(anyString())).thenReturn(new float[]{0.1f, 0.2f, 0.3f});
    }

    private Object[] makeRow(UUID chunkId, UUID sourceId, String courseCode, String sectionTitle, String text, double score) {
        return new Object[]{
                chunkId, sourceId, courseCode, 0,
                sectionTitle, text, null, 1, 5, null, null,
                "file.pdf", "http://example.com", score
        };
    }

    @Test
    void search_throwsOnBlankQuery() {
        lenient().when(ragQueryPlanner.plan(anyString(), any())).thenReturn(
                new RagQueryPlan("  ", "primary", "text", List.of("test"), java.util.Set.of()));

        SearchRequest req = new SearchRequest(null, "  ", 5);

        assertThatThrownBy(() -> service.search(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("query is required");
    }

    @Test
    void search_mapsResultsCorrectly() {
        UUID chunkId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();
        Object[] row = makeRow(chunkId, sourceId, "IT001", "Introduction", "Some content", 0.85);

        when(ragQueryPlanner.plan(anyString(), isNull())).thenReturn(
                new RagQueryPlan("what is IT001", "primary", "text", List.of("what is IT001"), java.util.Set.of()));
        when(knowledgeChunkRepository.searchHybrid(anyString(), anyString(), isNull(), anyInt(), anyInt()))
                .thenReturn(List.<Object[]>of(row));

        SearchRequest req = new SearchRequest(null, "what is IT001", 5);
        SearchResponse resp = service.search(req);

        assertThat(resp.query()).isEqualTo("what is IT001");
        assertThat(resp.courseCode()).isNull();
        assertThat(resp.results()).hasSize(1);

        SearchResponse.SearchResult result = resp.results().get(0);
        assertThat(result.chunkId()).isEqualTo(chunkId.toString());
        assertThat(result.sourceId()).isEqualTo(sourceId.toString());
        assertThat(result.courseCode()).isEqualTo("IT001");
        assertThat(result.sectionTitle()).isEqualTo("Introduction");
        assertThat(result.pageFrom()).isEqualTo(1);
        assertThat(result.pageTo()).isEqualTo(5);
        assertThat(result.score()).isCloseTo(0.85, org.assertj.core.data.Offset.offset(0.001));
        assertThat(result.text()).isEqualTo("Some content");
    }

    @Test
    void search_passesCourseCodeFilter() {
        when(ragQueryPlanner.plan("exam format", "IT001")).thenReturn(
                new RagQueryPlan("exam format", "primary", "text", List.of("exam format"), java.util.Set.of("IT001")));
        lenient().when(knowledgeChunkRepository.searchByVector(anyString(), eq("IT001"), anyInt()))
                .thenReturn(List.of());
        when(knowledgeChunkRepository.searchHybrid(anyString(), anyString(), eq("IT001"), anyInt(), anyInt()))
                .thenReturn(List.of());

        SearchRequest req = new SearchRequest("IT001", "exam format", 3);
        SearchResponse resp = service.search(req);

        assertThat(resp.courseCode()).isEqualTo("IT001");
        assertThat(resp.results()).isEmpty();
        verify(knowledgeChunkRepository).searchHybrid(anyString(), anyString(), eq("IT001"), anyInt(), anyInt());
    }

    @Test
    void search_vectorToPgString_format() {
        String pgStr = service.vectorToPgString(new float[]{0.1f, 0.5f, -0.3f});

        assertThat(pgStr).isEqualTo("[0.1,0.5,-0.3]");
    }

    @Test
    void search_emptyResults_returnsEmptyList() {
        when(ragQueryPlanner.plan("nothing here", null)).thenReturn(
                new RagQueryPlan("nothing here", "primary", "text", List.of("nothing here"), java.util.Set.of()));
        when(knowledgeChunkRepository.searchHybrid(anyString(), anyString(), isNull(), anyInt(), anyInt()))
                .thenReturn(List.of());

        SearchResponse resp = service.search(new SearchRequest(null, "nothing here", 5));

        assertThat(resp.results()).isEmpty();
    }

    @Test
    void search_fallsBackToVectorWhenHybridFails() {
        UUID chunkId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();
        Object[] row = makeRow(chunkId, sourceId, "IT001", "Fallback", "Fallback content", 0.75);

        when(ragQueryPlanner.plan("test query", "IT001")).thenReturn(
                new RagQueryPlan("test query", "primary", "text", List.of("test query"), java.util.Set.of("IT001")));
        when(knowledgeChunkRepository.searchHybrid(anyString(), anyString(), eq("IT001"), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("search_text not available"));
        when(knowledgeChunkRepository.searchByVector(anyString(), eq("IT001"), anyInt()))
                .thenReturn(List.<Object[]>of(row));

        SearchResponse resp = service.search(new SearchRequest("IT001", "test query", 5));

        assertThat(resp.results()).hasSize(1);
        assertThat(resp.results().get(0).chunkId()).isEqualTo(chunkId.toString());
        verify(knowledgeChunkRepository).searchByVector(anyString(), eq("IT001"), anyInt());
    }

    @Test
    void search_usesExpandedQueriesAndDedupesHighestScore() {
        UUID chunkId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();

        Object[] rowLow = makeRow(chunkId, sourceId, "IT001", "Title", "Content", 0.02);
        Object[] rowHigh = makeRow(chunkId, sourceId, "IT001", "Title", "Content", 0.08);

        when(ragQueryPlanner.plan("query", null)).thenReturn(
                new RagQueryPlan("query", "primary expanded", "text expanded",
                        List.of("query", "primary expanded"), java.util.Set.of()));
        lenient().when(knowledgeChunkRepository.searchByVector(anyString(), isNull(), anyInt()))
                .thenReturn(List.of());
        when(knowledgeChunkRepository.searchHybrid(anyString(), anyString(), isNull(), anyInt(), anyInt()))
                .thenReturn(List.<Object[]>of(rowLow))
                .thenReturn(List.<Object[]>of(rowHigh));

        SearchResponse resp = service.search(new SearchRequest(null, "query", 5));

        // Should deduplicate by ID and keep higher score
        assertThat(resp.results()).hasSize(1);
        assertThat(resp.results().get(0).score()).isCloseTo(0.08, org.assertj.core.data.Offset.offset(0.01));
    }
}
