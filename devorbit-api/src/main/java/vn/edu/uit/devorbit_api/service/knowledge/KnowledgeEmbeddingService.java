package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;
import vn.edu.uit.devorbit_api.service.ai.EmbeddingService;

import java.util.List;
import java.util.UUID;

/**
 * Embeds knowledge chunks for semantic search.
 * Skips already-embedded chunks unless force=true.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeEmbeddingService {

    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final EmbeddingService embeddingService;

    /**
     * Embed a single chunk. Skips if already embedded (unless force=true).
     */
    @Transactional
    public boolean embedChunk(KnowledgeChunk chunk, boolean force) {
        if (!force && chunk.getEmbedding() != null) {
            log.debug("Skipping already-embedded chunk {}", chunk.getId());
            return false;
        }

        float[] embedding = embeddingService.embed(chunk.getChunkText());
        chunk.setEmbedding(embedding);
        knowledgeChunkRepository.save(chunk);

        log.info("Embedded chunk {} for course {}", chunk.getId(), chunk.getCourseCode());
        return true;
    }

    /**
     * Embed all chunks for a specific source. Skips already-embedded unless force=true.
     * @return number of chunks embedded
     */
    @Transactional
    public int embedChunksForSource(UUID sourceId, boolean force) {
        List<KnowledgeChunk> chunks = knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(sourceId);
        return embedChunks(chunks, force);
    }

    /**
     * Embed all chunks for a course. Skips already-embedded unless force=true.
     * @return number of chunks embedded
     */
    @Transactional
    public int embedChunksForCourse(String courseCode, boolean force) {
        List<KnowledgeChunk> chunks = knowledgeChunkRepository.findByCourseCodeOrderByChunkIndexAsc(courseCode);
        return embedChunks(chunks, force);
    }

    private int embedChunks(List<KnowledgeChunk> chunks, boolean force) {
        if (chunks.isEmpty()) {
            log.warn("No chunks found to embed");
            return 0;
        }

        // Filter to only chunks needing embedding (null embedding + non-blank text)
        List<KnowledgeChunk> toEmbed = force
            ? chunks.stream().filter(c -> c.getChunkText() != null && !c.getChunkText().isBlank()).toList()
            : chunks.stream().filter(c -> c.getEmbedding() == null && c.getChunkText() != null && !c.getChunkText().isBlank()).toList();

        if (toEmbed.isEmpty()) {
            log.info("All {} chunks already embedded, skipping", chunks.size());
            return 0;
        }

        log.info("Embedding {} of {} chunks", toEmbed.size(), chunks.size());

        // Batch embed for efficiency
        List<String> texts = toEmbed.stream().map(KnowledgeChunk::getChunkText).toList();
        List<float[]> embeddings = embeddingService.embedBatch(texts);

        for (int i = 0; i < toEmbed.size(); i++) {
            KnowledgeChunk chunk = toEmbed.get(i);
            chunk.setEmbedding(embeddings.get(i));
        }

        knowledgeChunkRepository.saveAll(toEmbed);
        log.info("Successfully embedded {} chunks", toEmbed.size());
        return toEmbed.size();
    }
}
