package vn.edu.uit.devorbit_api.service.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseKnowledgeIndexerTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CourseKnowledgeIndexer indexer;

    private KnowledgeSource mockSource() {
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setFileName("IT001.md");
        return source;
    }

    @Test
    void chunkMarkdown_splitsByHeadings() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        // Two large sections that exceed TARGET_CHUNK_SIZE to force a split
        String bigContent = "X".repeat(3000);
        String md = "# Section 1\n" + bigContent + "\n\n# Section 2\n" + bigContent + "\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).sectionTitle()).isEqualTo("Section 1");
        assertThat(chunks.get(1).sectionTitle()).isEqualTo("Section 2");
    }

    @Test
    void chunkMarkdown_mergesSmallSections() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        String md = "# S1\nShort\n\n# S2\nShort\n\n# S3\nShort\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        // Small sections should be merged into one chunk
        assertThat(chunks).hasSize(1);
    }

    @Test
    void chunkMarkdown_extractsPageMarkers() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        String md = "# Section [Page 1-3]\nContent\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).pageFrom()).isEqualTo(1);
        assertThat(chunks.get(0).pageTo()).isEqualTo(3);
    }

    @Test
    void indexMarkdown_deletesBySourceId() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        KnowledgeSource source = mockSource();
        String md = "# Hello\nWorld\n";

        indexer.indexMarkdown(source, "IT001", md);

        verify(knowledgeChunkRepository).deleteBySourceId(source.getId());
        verify(knowledgeChunkRepository, never()).deleteByCourseCode(anyString());
    }

    @Test
    void indexMetadata_includesCourseCodeAndChunkIndex() throws Exception {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        KnowledgeSource source = mockSource();
        String md = "# Section\nContent\n";

        indexer.indexMarkdown(source, "IT001", md);

        ArgumentCaptor<String> metadataCaptor = ArgumentCaptor.forClass(String.class);
        verify(knowledgeChunkRepository).insertChunkWithoutEmbedding(
            any(UUID.class),
            eq(source.getId()),
            eq("IT001"),
            eq(0),
            eq("Section"),
            eq("# Section\nContent"),
            metadataCaptor.capture(),
            isNull(),
            isNull()
        );

        var metadata = objectMapper.readTree(metadataCaptor.getValue());
        assertThat(metadata.get("courseCode").asText()).isEqualTo("IT001");
        assertThat(metadata.get("chunkIndex").asInt()).isEqualTo(0);
        assertThat(metadata.get("sourceFile").asText()).isEqualTo("IT001.md");
    }

    @Test
    void indexMetadata_includesPageFieldsWhenPresent() throws Exception {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        KnowledgeSource source = mockSource();
        String md = "# Section [Page 5-10]\nContent\n";

        indexer.indexMarkdown(source, "IT001", md);

        ArgumentCaptor<String> metadataCaptor = ArgumentCaptor.forClass(String.class);
        verify(knowledgeChunkRepository).insertChunkWithoutEmbedding(
            any(UUID.class),
            eq(source.getId()),
            eq("IT001"),
            eq(0),
            eq("Section [Page 5-10]"),
            eq("# Section [Page 5-10]\nContent"),
            metadataCaptor.capture(),
            eq(5),
            eq(10)
        );

        var metadata = objectMapper.readTree(metadataCaptor.getValue());
        assertThat(metadata.get("pageFrom").asInt()).isEqualTo(5);
        assertThat(metadata.get("pageTo").asInt()).isEqualTo(10);
    }
}
