package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.knowledge.WebImportRequest;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.net.URI;

@Slf4j
@Service
public class FirecrawlKnowledgeImporter {

    private final WebKnowledgeIngestionService webKnowledgeIngestionService;

    public FirecrawlKnowledgeImporter(WebKnowledgeIngestionService webKnowledgeIngestionService) {
        this.webKnowledgeIngestionService = webKnowledgeIngestionService;
    }

    /**
     * Import a single URL through Firecrawl → ingestion pipeline.
     */
    public KnowledgeSource importUrl(WebImportRequest request) {
        validateRequest(request);
        return webKnowledgeIngestionService.importUrl(request);
    }

    private void validateRequest(WebImportRequest request) {
        if (request.url() == null || request.url().isBlank()) {
            throw new BadRequestException("URL is required");
        }
        try {
            URI.create(request.url()).toURL();
        } catch (Exception e) {
            throw new BadRequestException("Invalid URL: " + request.url());
        }
    }
}
