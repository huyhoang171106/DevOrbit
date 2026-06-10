package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;
import vn.edu.uit.devorbit_api.service.ai.EmbeddingService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeEmbeddingServiceTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Mock
    private EmbeddingService embeddingService;

    private KnowledgeEmbeddingService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeEmbeddingService(knowledgeChunkRepository, embeddingService);
        lenient().when(embeddingService.dimensions()).thenReturn(1536);
    }

    @Test
    void embedChunk_skipsAlreadyEmbedded_unlessForced() {
        KnowledgeChunk chunk = makeChunk("Already embedded");
        chunk.setEmbedding(new float[1536]);

        when(knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(any()))
            .thenReturn(List.of(chunk));

        int embedded = service.embedChunksForSource(chunk.getSource().getId(), false);

        assertThat(embedded).isZero();
        verify(embeddingService, never()).embedBatch(anyList());
    }

    @Test
    void embedChunk_forceReembeds() {
        KnowledgeChunk chunk = makeChunk("Force re-embed");
        chunk.setEmbedding(new float[1536]);

        when(knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(any()))
            .thenReturn(List.of(chunk));
        when(embeddingService.embedBatch(anyList()))
            .thenReturn(List.of(new float[1536]));

        int embedded = service.embedChunksForSource(chunk.getSource().getId(), true);

        assertThat(embedded).isEqualTo(1);
        verify(knowledgeChunkRepository).updateEmbeddingVector(eq(chunk.getId()), anyString());
    }

    @Test
    void embedChunksForSource_embedsNewChunks() {
        KnowledgeChunk c1 = makeChunk("Chunk one");
        KnowledgeChunk c2 = makeChunk("Chunk two");

        when(knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(any()))
            .thenReturn(List.of(c1, c2));
        when(embeddingService.embedBatch(anyList()))
            .thenReturn(List.of(new float[1536], new float[1536]));

        int embedded = service.embedChunksForSource(c1.getSource().getId(), false);

        assertThat(embedded).isEqualTo(2);
        verify(knowledgeChunkRepository, times(2)).updateEmbeddingVector(any(UUID.class), anyString());
    }

    @Test
    void embedChunksForCourse_embedsForCourseCode() {
        KnowledgeChunk c1 = makeChunk("Chunk A");
        c1.setCourseCode("IT001");

        when(knowledgeChunkRepository.findByCourseCodeOrderByChunkIndexAsc("IT001"))
            .thenReturn(List.of(c1));
        when(embeddingService.embedBatch(anyList()))
            .thenReturn(List.of(new float[1536]));

        int embedded = service.embedChunksForCourse("IT001", false);

        assertThat(embedded).isEqualTo(1);
        verify(knowledgeChunkRepository).updateEmbeddingVector(eq(c1.getId()), anyString());
    }

    @Test
    void embedChunksForCourse_skipsAlreadyEmbedded() {
        KnowledgeChunk c1 = makeChunk("Already done");
        c1.setCourseCode("IT001");
        c1.setEmbedding(new float[1536]);

        when(knowledgeChunkRepository.findByCourseCodeOrderByChunkIndexAsc("IT001"))
            .thenReturn(List.of(c1));

        int embedded = service.embedChunksForCourse("IT001", false);

        assertThat(embedded).isZero();
        verify(embeddingService, never()).embedBatch(anyList());
    }

    @Test
    void embedChunksForSource_skipsEmptyText() {
        KnowledgeChunk c1 = makeChunk("   ");
        c1.setChunkText("   ");

        when(knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(any()))
            .thenReturn(List.of(c1));

        int embedded = service.embedChunksForSource(c1.getSource().getId(), false);

        assertThat(embedded).isZero();
        verify(embeddingService, never()).embedBatch(anyList());
    }

    private KnowledgeChunk makeChunk(String text) {
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setId(UUID.randomUUID());
        chunk.setSource(source);
        chunk.setChunkText(text);
        chunk.setCourseCode("IT001");
        chunk.setChunkIndex(0);
        return chunk;
    }
}
