package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.knowledge.FolderIngestionSummary;
import vn.edu.uit.devorbit_api.dto.knowledge.IngestionReport;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.nio.file.Path;

/**
 * Write commands for knowledge ingestion.
 */
@Service
@RequiredArgsConstructor
public class AdminKnowledgeCommandService {

    private final SyllabusIngestionService ingestionService;

    public FolderIngestionSummary ingestFolder() {
        return ingestionService.ingestFolder();
    }

    public IngestionReport ingestFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new BadRequestException("filePath is required");
        }
        return ingestionService.ingestOneMarkdown(Path.of(filePath));
    }
}
