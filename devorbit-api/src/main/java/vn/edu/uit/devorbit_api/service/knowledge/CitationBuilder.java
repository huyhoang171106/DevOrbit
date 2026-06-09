package vn.edu.uit.devorbit_api.service.knowledge;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.knowledge.Citation;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;

import java.util.List;

/**
 * Builds Citation DTOs from search results.
 */
@Component
public class CitationBuilder {

    /**
     * Build citations from chunk search results.
     */
    public List<Citation> buildCitations(List<KnowledgeRetrievalService.ChunkResult> chunkResults, String courseCode) {
        if (chunkResults == null || chunkResults.isEmpty()) {
            return List.of();
        }

        return chunkResults.stream()
            .map(r -> {
                KnowledgeChunk chunk = r.chunk();
                KnowledgeChunk source = chunk; // chunk has source reference
                return new Citation(
                    chunk.getSource() != null ? chunk.getSource().getId() : null,
                    chunk.getSource() != null ? chunk.getSource().getFileName() : null,
                    chunk.getSource() != null ? chunk.getSource().getUrl() : null,
                    chunk.getSectionTitle(),
                    chunk.getPageFrom(),
                    chunk.getPageTo(),
                    chunk.getChunkIndex()
                );
            })
            .toList();
    }
}
