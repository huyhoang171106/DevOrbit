package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchRequest;
import vn.edu.uit.devorbit_api.dto.knowledge.SearchResponse;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;
import vn.edu.uit.devorbit_api.service.ai.EmbeddingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Semantic search over knowledge chunks using pgvector.
 * Embeds query, performs vector similarity search, returns ranked results.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeRetrievalService {

    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final EmbeddingService embeddingService;

    /**
     * Search knowledge chunks by semantic similarity.
     * @param request search parameters (courseCode, query, topK)
     * @return ranked search results with scores
     */
    public SearchResponse search(SearchRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException("query is required");
        }

        // Embed the query
        float[] queryEmbedding = embeddingService.embed(request.query());
        String queryVector = vectorToPgString(queryEmbedding);

        // Execute similarity search
        List<Object[]> rows = knowledgeChunkRepository.searchByVector(
            queryVector,
            request.courseCode(),
            request.topK()
        );

        // Map results
        List<SearchResponse.SearchResult> results = new ArrayList<>();
        for (Object[] row : rows) {
            KnowledgeChunk chunk = mapRowToChunk(row);
            double score = extractScore(row);

            results.add(new SearchResponse.SearchResult(
                chunk.getId().toString(),
                chunk.getSource().getId().toString(),
                chunk.getCourseCode(),
                chunk.getSectionTitle(),
                chunk.getPageFrom(),
                chunk.getPageTo(),
                score,
                chunk.getChunkText()
            ));
        }

        log.info("Search for '{}' in course {} returned {} results",
            request.query(), request.courseCode(), results.size());

        return new SearchResponse(
            request.query(),
            request.courseCode(),
            results
        );
    }

    /**
     * Convert float array to PostgreSQL vector string format.
     * Example: [0.1, 0.2, 0.3] → "[0.1,0.2,0.3]"
     */
    String vectorToPgString(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Extract KnowledgeChunk from native query result row.
     * The row contains all KnowledgeChunk columns + similarity score.
     */
    @SuppressWarnings("unchecked")
    private KnowledgeChunk mapRowToChunk(Object[] row) {
        // Native query returns: id, source_id, course_code, chunk_index, section_title,
        // chunk_text, metadata_json, page_from, page_to, created_at, embedding, similarity
        // We need to reconstruct a KnowledgeChunk from these fields
        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setId((UUID) row[0]);

        // Create a minimal KnowledgeSource reference with just the ID
        KnowledgeSource source = new KnowledgeSource();
        source.setId((UUID) row[1]);
        chunk.setSource(source);

        chunk.setCourseCode((String) row[2]);
        chunk.setChunkIndex((Integer) row[3]);
        chunk.setSectionTitle((String) row[4]);
        chunk.setChunkText((String) row[5]);
        // row[6] is metadata_json (JsonNode)
        chunk.setPageFrom((Integer) row[7]);
        chunk.setPageTo((Integer) row[8]);
        // row[9] is created_at, row[10] is embedding
        return chunk;
    }

    /**
     * Simplified chunk result for internal service use.
     */
    public record ChunkResult(KnowledgeChunk chunk, double score) {}

    /**
     * Simplified search result for internal service use.
     */
    public record SearchResult(String courseCode, String query, List<ChunkResult> chunks) {}

    /**
     * Search by courseCode, query, topK — convenience overload for internal services.
     */
    public SearchResult search(String courseCode, String query, int topK) {
        SearchRequest request = new SearchRequest(courseCode, query, topK);
        SearchResponse response = search(request);
        List<ChunkResult> chunkResults = response.results().stream()
            .map(r -> {
                KnowledgeChunk chunk = new KnowledgeChunk();
                chunk.setId(java.util.UUID.fromString(r.chunkId()));
                chunk.setCourseCode(r.courseCode());
                chunk.setSectionTitle(r.sectionTitle());
                chunk.setPageFrom(r.pageFrom());
                chunk.setPageTo(r.pageTo());
                chunk.setChunkText(r.text());
                KnowledgeSource source = new KnowledgeSource();
                source.setId(java.util.UUID.fromString(r.sourceId()));
                chunk.setSource(source);
                return new ChunkResult(chunk, r.score());
            })
            .toList();
        return new SearchResult(courseCode, query, chunkResults);
    }

    /**
     * Extract similarity score from native query result row.
     */
    private double extractScore(Object[] row) {
        // The similarity score is the last column (index 11)
        Object scoreObj = row[row.length - 1];
        if (scoreObj instanceof Number) {
            return ((Number) scoreObj).doubleValue();
        }
        return 0.0;
    }
}
