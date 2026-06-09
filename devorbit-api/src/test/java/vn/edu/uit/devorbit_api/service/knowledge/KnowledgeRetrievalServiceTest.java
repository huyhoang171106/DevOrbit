package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchRequest;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchResponse;
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

    private KnowledgeRetrievalService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeRetrievalService(knowledgeChunkRepository, embeddingService);
        lenient().when(embeddingService.embed(anyString())).thenReturn(new float[]{0.1f, 0.2f, 0.3f});
    }

    @Test
    void search_throwsOnBlankQuery() {
        SearchRequest req = new SearchRequest(null, "  ", 5);

        assertThatThrownBy(() -> service.search(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("query is required");
    }

    @Test
    void search_mapsResultsCorrectly() {
        UUID chunkId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();
        Object[] row = {
            chunkId, sourceId, "IT001", 0,
            "Introduction", "Some content", null, 1, 5, null, null, 0.85
        };

        when(knowledgeChunkRepository.searchByVector(anyString(), isNull(), eq(5)))
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
        when(knowledgeChunkRepository.searchByVector(anyString(), eq("IT001"), eq(3)))
            .thenReturn(List.of());

        SearchRequest req = new SearchRequest("IT001", "exam format", 3);
        SearchResponse resp = service.search(req);

        assertThat(resp.courseCode()).isEqualTo("IT001");
        assertThat(resp.results()).isEmpty();
        verify(knowledgeChunkRepository).searchByVector(anyString(), eq("IT001"), eq(3));
    }

    @Test
    void search_vectorToPgString_format() {
        String pgStr = service.vectorToPgString(new float[]{0.1f, 0.5f, -0.3f});

        assertThat(pgStr).isEqualTo("[0.1,0.5,-0.3]");
    }

    @Test
    void search_emptyResults_returnsEmptyList() {
        when(knowledgeChunkRepository.searchByVector(anyString(), isNull(), eq(5)))
            .thenReturn(List.of());

        SearchResponse resp = service.search(new SearchRequest(null, "nothing here", 5));

        assertThat(resp.results()).isEmpty();
    }
}
