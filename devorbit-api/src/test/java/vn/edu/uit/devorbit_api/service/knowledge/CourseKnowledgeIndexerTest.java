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
        String bigContent = "X".repeat(3000);
        String md = "# Section 1\n" + bigContent + "\n\n# Section 2\n" + bigContent + "\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        // Each section gets a SECTION_SUMMARY + 1 DETAIL chunk (within MAX_CHUNK_SIZE)
        assertThat(chunks).hasSize(4);
        // First two should be summaries
        assertThat(chunks.get(0).chunkKind()).isEqualTo("SECTION_SUMMARY");
        assertThat(chunks.get(0).sectionTitle()).isEqualTo("Section 1");
        assertThat(chunks.get(1).chunkKind()).isEqualTo("DETAIL");
        assertThat(chunks.get(1).sectionTitle()).isEqualTo("Section 1");
        assertThat(chunks.get(2).chunkKind()).isEqualTo("SECTION_SUMMARY");
        assertThat(chunks.get(2).sectionTitle()).isEqualTo("Section 2");
        assertThat(chunks.get(3).chunkKind()).isEqualTo("DETAIL");
        assertThat(chunks.get(3).sectionTitle()).isEqualTo("Section 2");
    }

    @Test
    void chunkMarkdown_handlesUnheadedContent() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        String md = "Plain content without headings.\n\nMore text.";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        // No headings, so no SECTION_SUMMARY, just one DETAIL
        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).chunkKind()).isEqualTo("DETAIL");
        assertThat(chunks.get(0).sectionTitle()).isNull();
    }

    @Test
    void chunkMarkdown_extractsPageMarkers() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        String md = "# Section [Page 1-3]\nContent\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        // Summary + Detail
        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).pageFrom()).isEqualTo(1);
        assertThat(chunks.get(0).pageTo()).isEqualTo(3);
        assertThat(chunks.get(1).pageFrom()).isEqualTo(1);
        assertThat(chunks.get(1).pageTo()).isEqualTo(3);
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
        verify(knowledgeChunkRepository, atLeastOnce()).insertChunkWithoutEmbedding(
                any(UUID.class),
                eq(source.getId()),
                eq("IT001"),
                anyInt(),
                any(),
                anyString(),
                metadataCaptor.capture(),
                any(),
                any(),
                anyString(),
                any()
        );

        var metadata = objectMapper.readTree(metadataCaptor.getValue());
        assertThat(metadata.get("courseCode").asText()).isEqualTo("IT001");
        assertThat(metadata.get("sourceFile").asText()).isEqualTo("IT001.md");
        assertThat(metadata.get("chunkKind").asText()).isIn("SECTION_SUMMARY", "DETAIL");
    }

    @Test
    void chunkMarkdown_emitsSummaryBeforeDetailForHeadedSection() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        String md = "## Mục tiêu\nSinh viên hiểu kiểm thử phần mềm...\n";

        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).chunkKind()).isEqualTo("SECTION_SUMMARY");
        assertThat(chunks.get(0).sectionTitle()).isEqualTo("Mục tiêu");
        // Summary text should include title + content truncated to SECTION_SUMMARY_MAX_CHARS
        assertThat(chunks.get(0).text()).startsWith("Mục tiêu\n");
        assertThat(chunks.get(1).chunkKind()).isEqualTo("DETAIL");
        assertThat(chunks.get(1).sectionKey()).isEqualTo(chunks.get(0).sectionKey());
    }

    @Test
    void chunkMarkdown_overlapsLargeSections() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        // Build text long enough to split: ~3900 chars padding + overlap keyword + padding to exceed MAX_CHUNK_SIZE
        String padding1 = "X".repeat(3900);
        String overlapKeyword = " --- OVERLAP_MARKER_HERE --- ";
        String padding2 = "X".repeat(1650);
        String bigContent = padding1 + overlapKeyword + padding2;
        String md = "# Big Section\n" + bigContent + "\n";
        List<CourseKnowledgeIndexer.ChunkInfo> chunks = indexer.chunkMarkdown(md);

        // Summary + at least 2 detail chunks
        assertThat(chunks).hasSizeGreaterThanOrEqualTo(3);
        List<CourseKnowledgeIndexer.ChunkInfo> details = chunks.stream()
                .filter(c -> "DETAIL".equals(c.chunkKind()))
                .toList();
        assertThat(details).hasSizeGreaterThanOrEqualTo(2);

        // Second detail chunk should overlap with first (contain "OVERLAP" which is near the end of first)
        String firstDetail = details.get(0).text();
        String secondDetail = details.get(1).text();
        // The overlap region should share content
        boolean hasOverlap = secondDetail.contains("OVERLAP");
        assertThat(hasOverlap).as("Second detail chunk should share overlap content with first").isTrue();
    }

    @Test
    void indexMarkdown_setsDetailParentChunkId() {
        indexer = new CourseKnowledgeIndexer(knowledgeChunkRepository, objectMapper);
        KnowledgeSource source = mockSource();
        String md = "# Section\nContent\n";

        indexer.indexMarkdown(source, "IT001", md);

        ArgumentCaptor<UUID> parentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> kindCaptor = ArgumentCaptor.forClass(String.class);

        verify(knowledgeChunkRepository, times(2)).insertChunkWithoutEmbedding(
                any(UUID.class),
                eq(source.getId()),
                eq("IT001"),
                anyInt(),
                any(),
                anyString(),
                anyString(),
                any(),
                any(),
                kindCaptor.capture(),
                parentCaptor.capture()
        );

        List<String> kinds = kindCaptor.getAllValues();
        List<UUID> parents = parentCaptor.getAllValues();

        // First call is summary, second is detail
        assertThat(kinds.get(0)).isEqualTo("SECTION_SUMMARY");
        assertThat(parents.get(0)).isNull();

        assertThat(kinds.get(1)).isEqualTo("DETAIL");
        // Detail parent should equal summary UUID
        assertThat(parents.get(1)).isNotNull();
    }
}
