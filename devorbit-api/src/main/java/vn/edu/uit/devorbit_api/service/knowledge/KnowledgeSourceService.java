package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeSourceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages knowledge source documents.
 * Handles creation, lookup, and status updates for ingested sources.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeSourceService {

    private final KnowledgeSourceRepository knowledgeSourceRepository;

    /**
     * Find existing source by content hash.
     */
    public Optional<KnowledgeSource> findByContentHash(String contentHash) {
        return knowledgeSourceRepository.findByContentHash(contentHash);
    }

    /**
     * Create a new knowledge source.
     */
    @Transactional
    public KnowledgeSource createSource(String sourceType, String fileName, String filePath,
                                         String title, String contentHash, String rawText) {
        KnowledgeSource source = KnowledgeSource.builder()
                .sourceType(sourceType)
                .fileName(fileName)
                .filePath(filePath)
                .title(title)
                .contentHash(contentHash)
                .rawText(rawText)
                .status("PENDING")
                .build();
        KnowledgeSource saved = knowledgeSourceRepository.save(source);
        log.info("Created knowledge source: {} (id: {})", fileName, saved.getId());
        return saved;
    }

    /**
     * Update source status to FAILED with error message.
     */
    @Transactional
    public void markFailed(UUID sourceId, String errorMessage) {
        knowledgeSourceRepository.findById(sourceId).ifPresent(source -> {
            source.setStatus("FAILED");
            source.setErrorMessage(errorMessage);
            knowledgeSourceRepository.save(source);
            log.warn("Source marked FAILED: {} - {}", source.getFileName(), errorMessage);
        });
    }

    /**
     * List all knowledge sources.
     */
    public List<KnowledgeSource> findAll() {
        return knowledgeSourceRepository.findAll();
    }

    /**
     * Find source by ID.
     */
    public Optional<KnowledgeSource> findById(UUID id) {
        return knowledgeSourceRepository.findById(id);
    }
}
