package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeSourceRepository extends JpaRepository<KnowledgeSource, UUID> {
    Optional<KnowledgeSource> findByContentHash(String contentHash);
    Optional<KnowledgeSource> findFirstByFileNameAndStatusOrderByUpdatedAtDesc(String fileName, String status);
}
