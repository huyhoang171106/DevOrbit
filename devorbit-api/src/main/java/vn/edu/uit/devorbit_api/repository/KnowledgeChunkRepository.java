package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import java.util.List;
import java.util.UUID;

/**
 * KNOWLEDGE CHUNK REPOSITORY = data access for text chunks + vector search.
 *
 * This is the MOST TECHNICALLY COMPLEX repository because it supports:
 *   1. Vector similarity search (cosine distance on embeddings)
 *   2. Full-text search (PostgreSQL FTS via ts_query)
 *   3. Hybrid search (RRF score fusion combining both)
 *
 * The embedding field is a pgvector VECTOR(4096) — an array of 4096 floats
 * representing the semantic meaning of the chunk text.
 *
 * Hybrid search formula:
 *   similarity = 1/(60+vector_rank) + 1/(60+text_rank) + trust_boost + source_boost + kind_boost
 *
 * This is an advanced RAG (Retrieval-Augmented Generation) pattern —
 * the AI tutor uses this to find relevant context when answering questions.
 */
@Repository
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, UUID> {

    /** All chunks for a course, in order (for sequential display). */
    List<KnowledgeChunk> findByCourseCodeOrderByChunkIndexAsc(String courseCode);

    /** All chunks from one source document. */
    List<KnowledgeChunk> findBySourceIdOrderByChunkIndexAsc(UUID sourceId);

    /** Delete chunks when a course is removed. */
    void deleteByCourseCode(String courseCode);

    /** Delete chunks when their source document is removed. */
    void deleteBySourceId(UUID sourceId);

    /**
     * Insert a chunk WITHOUT its embedding vector (faster batch insert).
     * Uses nativeQuery = true because we need PostgreSQL-specific CAST and JSONB.
     * Embedding is computed separately and updated via updateEmbeddingVector().
     */
    @Modifying
    @Query(value = """
        INSERT INTO knowledge_chunks (
            id, source_id, course_code, chunk_index, section_title, chunk_text,
            metadata_json, page_from, page_to, chunk_kind, parent_chunk_id, created_at
        )
        VALUES (
            :id, :sourceId, :courseCode, :chunkIndex, :sectionTitle, :chunkText,
            CAST(:metadataJson AS jsonb), :pageFrom, :pageTo, :chunkKind, :parentChunkId, CURRENT_TIMESTAMP
        )
        """, nativeQuery = true)
    void insertChunkWithoutEmbedding(
        @Param("id") UUID id,
        @Param("sourceId") UUID sourceId,
        @Param("courseCode") String courseCode,
        @Param("chunkIndex") Integer chunkIndex,
        @Param("sectionTitle") String sectionTitle,
        @Param("chunkText") String chunkText,
        @Param("metadataJson") String metadataJson,
        @Param("pageFrom") Integer pageFrom,
        @Param("pageTo") Integer pageTo,
        @Param("chunkKind") String chunkKind,
        @Param("parentChunkId") UUID parentChunkId
    );

    /**
     * Update a chunk's embedding vector after it's been computed by the AI model.
     * CAST(:embedding AS vector) converts the string "[0.1,0.2,...]" to pgvector format.
     */
    @Modifying
    @Query(value = """
        UPDATE knowledge_chunks
        SET embedding = CAST(:embedding AS vector)
        WHERE id = :id
        """, nativeQuery = true)
    void updateEmbeddingVector(
        @Param("id") UUID id,
        @Param("embedding") String embedding
    );

    /**
     * PURE VECTOR SIMILARITY SEARCH.
     * Uses pgvector's <=> operator (cosine distance).
     * Lower distance = more semantically similar.
     * Similarity score = 1 - cosine_distance (so higher = better).
     *
     * @param queryVector the query embedding as a string
     * @param courseCode  filter to one course, or null for all courses
     * @param topK        how many results to return
     * @return list of Object[] rows with chunk data + similarity score
     */
    @Query(value = """
        SELECT c.id, c.source_id, c.course_code, c.chunk_index, c.section_title,
               c.chunk_text, c.metadata_json, c.page_from, c.page_to, c.created_at, c.embedding,
               s.file_name, s.url,
               1 - (c.embedding <=> CAST(:queryVector AS vector)) AS similarity
        FROM knowledge_chunks c
        JOIN knowledge_sources s ON s.id = c.source_id
        WHERE c.embedding IS NOT NULL
        AND (:courseCode IS NULL OR c.course_code = :courseCode)
        ORDER BY c.embedding <=> CAST(:queryVector AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<Object[]> searchByVector(
        @Param("queryVector") String queryVector,
        @Param("courseCode") String courseCode,
        @Param("topK") int topK
    );

    /**
     * HYBRID SEARCH = vector search + full-text search + metadata boost.
     *
     * Combines three signals:
     *   1. Vector rank: how semantically similar the chunk is to the query
     *   2. Text rank: how many query keywords match in the chunk text (PostgreSQL FTS)
     *   3. Metadata boost: official/trusted sources get a score boost
     *
     * Uses Reciprocal Rank Fusion (RRF): 1/(60 + rank) for each signal.
     * The constant 60 prevents any single signal from dominating.
     */
    @Query(value = """
        WITH vector_ranked AS (
            SELECT c.id,
                   row_number() OVER (ORDER BY c.embedding <=> CAST(:queryVector AS vector)) AS vector_rank
            FROM knowledge_chunks c
            WHERE c.embedding IS NOT NULL
              AND (:courseCode IS NULL OR c.course_code = :courseCode)
            ORDER BY c.embedding <=> CAST(:queryVector AS vector)
            LIMIT :candidateLimit
        ),
        text_ranked AS (
            SELECT c.id,
                   row_number() OVER (ORDER BY ts_rank_cd(c.search_text, plainto_tsquery('simple', :textQuery)) DESC) AS text_rank
            FROM knowledge_chunks c
            WHERE (:courseCode IS NULL OR c.course_code = :courseCode)
              AND c.search_text @@ plainto_tsquery('simple', :textQuery)
            ORDER BY ts_rank_cd(c.search_text, plainto_tsquery('simple', :textQuery)) DESC
            LIMIT :candidateLimit
        ),
        combined AS (
            SELECT id FROM vector_ranked
            UNION
            SELECT id FROM text_ranked
        )
        SELECT c.id, c.source_id, c.course_code, c.chunk_index, c.section_title,
               c.chunk_text, c.metadata_json, c.page_from, c.page_to, c.created_at, c.embedding,
               s.file_name, s.url,
               (
                 COALESCE(1.0 / (60 + vr.vector_rank), 0.0)
                 + COALESCE(1.0 / (60 + tr.text_rank), 0.0)
                 + CASE UPPER(COALESCE(s.trust_level, ''))
                       WHEN 'OFFICIAL' THEN 0.040
                       WHEN 'REFERENCE' THEN 0.020
                       WHEN 'COMMUNITY' THEN 0.005
                       ELSE 0.000
                   END
                 + CASE UPPER(COALESCE(s.source_type, ''))
                       WHEN 'SYLLABUS' THEN 0.025
                       WHEN 'WEB' THEN 0.010
                       ELSE 0.000
                   END
                 + CASE UPPER(COALESCE(c.chunk_kind, 'DETAIL'))
                       WHEN 'SECTION_SUMMARY' THEN 0.010
                       ELSE 0.000
                   END
               ) AS similarity
        FROM combined ids
        JOIN knowledge_chunks c ON c.id = ids.id
        JOIN knowledge_sources s ON s.id = c.source_id
        LEFT JOIN vector_ranked vr ON vr.id = c.id
        LEFT JOIN text_ranked tr ON tr.id = c.id
        ORDER BY similarity DESC, c.chunk_index ASC
        LIMIT :topK
        """, nativeQuery = true)
    List<Object[]> searchHybrid(
        @Param("queryVector") String queryVector,
        @Param("textQuery") String textQuery,
        @Param("courseCode") String courseCode,
        @Param("topK") int topK,
        @Param("candidateLimit") int candidateLimit
    );
}
