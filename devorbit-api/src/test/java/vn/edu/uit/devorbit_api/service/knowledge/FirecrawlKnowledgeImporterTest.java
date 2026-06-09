package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.knowledge.WebImportRequest;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirecrawlKnowledgeImporterTest {

    @Mock
    private WebKnowledgeIngestionService webKnowledgeIngestionService;
    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    private FirecrawlKnowledgeImporter importer;

    @BeforeEach
    void setUp() {
        importer = new FirecrawlKnowledgeImporter(webKnowledgeIngestionService);
    }

    @Test
    void importUrl_validRequest_delegatesToIngestionService() {
        WebImportRequest request = new WebImportRequest(
            "https://example.com/course/IT003", "IT003", "OFFICIAL", true);
        KnowledgeSource mockSource = new KnowledgeSource();
        mockSource.setId(UUID.randomUUID());
        mockSource.setStatus("COMPLETED");

        when(webKnowledgeIngestionService.importUrl(any())).thenReturn(mockSource);

        KnowledgeSource result = importer.importUrl(request);

        assertThat(result).isNotNull();
        verify(webKnowledgeIngestionService).importUrl(request);
    }

    @Test
    void importUrl_nullUrl_throwsBadRequest() {
        WebImportRequest request = new WebImportRequest(
            null, "IT003", "OFFICIAL", true);

        assertThatThrownBy(() -> importer.importUrl(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("URL is required");
    }

    @Test
    void importUrl_blankUrl_throwsBadRequest() {
        WebImportRequest request = new WebImportRequest(
            "  ", "IT003", "OFFICIAL", true);

        assertThatThrownBy(() -> importer.importUrl(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("URL is required");
    }

    @Test
    void importUrl_invalidUrl_throwsBadRequest() {
        WebImportRequest request = new WebImportRequest(
            "not-a-url", "IT003", "OFFICIAL", true);

        assertThatThrownBy(() -> importer.importUrl(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Invalid URL");
    }
}
