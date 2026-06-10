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
            metadata_json, page_from, page_to, created_at
        )
        VALUES (
            :id, :sourceId, :courseCode, :chunkIndex, :sectionTitle, :chunkText,
            CAST(:metadataJson AS jsonb), :pageFrom, :pageTo, CURRENT_TIMESTAMP
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
        @Param("pageTo") Integer pageTo
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
}
