package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.dto.knowledge.Citation;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CitationBuilderTest {

    private CitationBuilder citationBuilder;

    @BeforeEach
    void setUp() {
        citationBuilder = new CitationBuilder();
    }

    @Test
    void buildCitations_withChunks_returnsCitations() {
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setFileName("IT003_syllabus.md");
        source.setUrl("https://example.com/IT003");

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setId(UUID.randomUUID());
        chunk.setSource(source);
        chunk.setSectionTitle("Đánh giá");
        chunk.setPageFrom(6);
        chunk.setPageTo(7);
        chunk.setChunkIndex(3);

        KnowledgeRetrievalService.ChunkResult result = new KnowledgeRetrievalService.ChunkResult(
            chunk, 0.85);

        List<Citation> citations = citationBuilder.buildCitations(List.of(result), "IT003");

        assertThat(citations).hasSize(1);
        Citation c = citations.get(0);
        assertThat(c.sourceId()).isEqualTo(source.getId());
        assertThat(c.fileName()).isEqualTo("IT003_syllabus.md");
        assertThat(c.url()).isEqualTo("https://example.com/IT003");
        assertThat(c.sectionTitle()).isEqualTo("Đánh giá");
        assertThat(c.pageFrom()).isEqualTo(6);
        assertThat(c.pageTo()).isEqualTo(7);
        assertThat(c.chunkIndex()).isEqualTo(3);
    }

    @Test
    void buildCitations_emptyList_returnsEmpty() {
        List<Citation> citations = citationBuilder.buildCitations(List.of(), "IT003");

        assertThat(citations).isEmpty();
    }

    @Test
    void buildCitations_nullList_returnsEmpty() {
        List<Citation> citations = citationBuilder.buildCitations(null, "IT003");

        assertThat(citations).isEmpty();
    }
}
