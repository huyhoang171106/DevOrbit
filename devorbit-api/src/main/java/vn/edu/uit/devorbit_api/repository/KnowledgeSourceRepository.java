package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import java.util.Optional;
import java.util.UUID;

/**
 * KNOWLEDGE SOURCE REPOSITORY = data access for imported document sources.
 *
 * Used by the knowledge ingestion pipeline to:
 *   1. Check for duplicate imports (via contentHash)
 *   2. Find previously imported files (via fileName + status)
 */
@Repository
public interface KnowledgeSourceRepository extends JpaRepository<KnowledgeSource, UUID> {

    /**
     * Find by content hash — prevents importing the same content twice.
     * The hash is computed from the file content at import time.
     */
    Optional<KnowledgeSource> findByContentHash(String contentHash);

    /**
     * Find the most recently updated source with a given filename and status.
     * Used during re-import to check if a file has already been processed.
     */
    Optional<KnowledgeSource> findFirstByFileNameAndStatusOrderByUpdatedAtDesc(String fileName, String status);
}
