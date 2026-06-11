package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import java.util.List;
import java.util.UUID;

@Repository
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, UUID> {
    List<KnowledgeChunk> findByCourseCodeOrderByChunkIndexAsc(String courseCode);
    List<KnowledgeChunk> findBySourceIdOrderByChunkIndexAsc(UUID sourceId);
    void deleteByCourseCode(String courseCode);
    void deleteBySourceId(UUID sourceId);

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
     * Vector similarity search using cosine distance.
     * Returns chunks ordered by relevance (lowest distance = most similar).
     */
    @Query(value = """
        SELECT *, 1 - (embedding <=> CAST(:queryVector AS vector)) AS similarity
        FROM knowledge_chunks
        WHERE embedding IS NOT NULL
        AND (:courseCode IS NULL OR course_code = :courseCode)
        ORDER BY embedding <=> CAST(:queryVector AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<Object[]> searchByVector(
        @Param("queryVector") String queryVector,
        @Param("courseCode") String courseCode,
        @Param("topK") int topK
    );

    /**
     * Hybrid search combining vector similarity and PostgreSQL FTS with RRF-style score fusion.
     * Metadata boosts for trust level, source type, and chunk kind are applied in SQL.
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
