package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

    /**
     * Vector similarity search using cosine distance.
     * Returns chunks ordered by relevance (lowest distance = most similar).
     */
    @Query(value = """
        SELECT *, 1 - (embedding <=> :queryVector::vector) AS similarity
        FROM knowledge_chunks
        WHERE embedding IS NOT NULL
        AND (:courseCode IS NULL OR course_code = :courseCode)
        ORDER BY embedding <=> :queryVector::vector
        LIMIT :topK
        """, nativeQuery = true)
    List<Object[]> searchByVector(
        @Param("queryVector") String queryVector,
        @Param("courseCode") String courseCode,
        @Param("topK") int topK
    );
}
