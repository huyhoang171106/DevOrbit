package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RagResultRerankerTest {

    private final RagResultReranker reranker = new RagResultReranker();

    private KnowledgeChunk chunk(String id, String sourceId, String courseCode, String sectionTitle, String text) {
        KnowledgeChunk c = new KnowledgeChunk();
        c.setId(UUID.fromString(id));
        KnowledgeSource s = new KnowledgeSource();
        s.setId(UUID.fromString(sourceId));
        c.setSource(s);
        c.setCourseCode(courseCode);
        c.setSectionTitle(sectionTitle);
        c.setChunkText(text);
        return c;
    }

    @Test
    void rerank_returnsEmptyForNullCandidates() {
        assertThat(reranker.rerank("query", null, 5)).isEmpty();
    }

    @Test
    void rerank_returnsEmptyForEmptyCandidates() {
        assertThat(reranker.rerank("query", List.of(), 5)).isEmpty();
    }

    @Test
    void rerank_returnsEmptyForLimitZero() {
        KnowledgeChunk c = chunk(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            "IT001", "Intro", "Some content");
        var candidates = List.of(new KnowledgeRetrievalService.ChunkResult(c, 0.5));

        assertThat(reranker.rerank("query", candidates, 0)).isEmpty();
    }

    @Test
    void rerank_boostsLexicalAndSectionMatches() {
        UUID sourceId = UUID.randomUUID();
        KnowledgeChunk matching = chunk(
            UUID.randomUUID().toString(), sourceId.toString(),
            "IT001", "Kinh nghiem hoc tap", "phuong phap hoc tap hieu qua cho sinh vien");
        KnowledgeChunk nonMatching = chunk(
            UUID.randomUUID().toString(), sourceId.toString(),
            "IT001", "Danh sach mon hoc", "cac mon hoc trong chuong trinh dao tao");

        var candidates = List.of(
            new KnowledgeRetrievalService.ChunkResult(nonMatching, 0.02),
            new KnowledgeRetrievalService.ChunkResult(matching, 0.02)
        );

        List<KnowledgeRetrievalService.ChunkResult> result = reranker.rerank("kinh nghiem hoc tap", candidates, 5);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).chunk().getSectionTitle()).isEqualTo("Kinh nghiem hoc tap");
        assertThat(result.get(0).score()).isGreaterThan(0.02);
    }

    @Test
    void rerank_deduplicatesSameChunkId() {
        String chunkId = UUID.randomUUID().toString();
        String sourceId = UUID.randomUUID().toString();
        KnowledgeChunk chunk = chunk(chunkId, sourceId, "IT001", "Title", "Content");

        var candidates = List.of(
            new KnowledgeRetrievalService.ChunkResult(chunk, 0.5),
            new KnowledgeRetrievalService.ChunkResult(chunk, 0.5)
        );

        List<KnowledgeRetrievalService.ChunkResult> result = reranker.rerank("query", candidates, 5);

        assertThat(result).hasSize(1);
    }

    @Test
    void rerank_appliesSourceDiversityBeforeThirdSameSource() {
        UUID sourceA = UUID.randomUUID();
        UUID sourceB = UUID.randomUUID();

        KnowledgeChunk a1 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A1", "Content A1");
        KnowledgeChunk a2 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A2", "Content A2");
        KnowledgeChunk a3 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A3", "Content A3");
        KnowledgeChunk b1 = chunk(UUID.randomUUID().toString(), sourceB.toString(), "IT001", "B1", "Content B1");

        var candidates = List.of(
            new KnowledgeRetrievalService.ChunkResult(a1, 0.10),
            new KnowledgeRetrievalService.ChunkResult(a2, 0.09),
            new KnowledgeRetrievalService.ChunkResult(a3, 0.08),
            new KnowledgeRetrievalService.ChunkResult(b1, 0.07)
        );

        List<KnowledgeRetrievalService.ChunkResult> result = reranker.rerank("query", candidates, 3);

        assertThat(result).hasSize(3);
        // Source B should be in the top 3 (diversity ensures B1 is included despite lower score)
        boolean sourceBFound = result.stream()
            .anyMatch(r -> r.chunk().getSource().getId().equals(sourceB));
        assertThat(sourceBFound).isTrue();
    }

    @Test
    void rerank_fillsLimitFromSingleSource() {
        UUID sourceA = UUID.randomUUID();
        KnowledgeChunk a1 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A1", "Content A1");
        KnowledgeChunk a2 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A2", "Content A2");
        KnowledgeChunk a3 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A3", "Content A3");

        var candidates = List.of(
            new KnowledgeRetrievalService.ChunkResult(a1, 0.10),
            new KnowledgeRetrievalService.ChunkResult(a2, 0.09),
            new KnowledgeRetrievalService.ChunkResult(a3, 0.08)
        );

        List<KnowledgeRetrievalService.ChunkResult> result = reranker.rerank("query", candidates, 3);

        assertThat(result).hasSize(3);
    }

    @Test
    void rerank_respectsLimit() {
        UUID sourceA = UUID.randomUUID();
        KnowledgeChunk a1 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A1", "Content A1");
        KnowledgeChunk a2 = chunk(UUID.randomUUID().toString(), sourceA.toString(), "IT001", "A2", "Content A2");

        var candidates = List.of(
            new KnowledgeRetrievalService.ChunkResult(a1, 0.10),
            new KnowledgeRetrievalService.ChunkResult(a2, 0.09)
        );

        assertThat(reranker.rerank("query", candidates, 1)).hasSize(1);
    }
}
