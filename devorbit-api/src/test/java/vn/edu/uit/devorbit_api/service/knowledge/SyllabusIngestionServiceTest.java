package vn.edu.uit.devorbit_api.service.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.config.KnowledgeConfig;
import vn.edu.uit.devorbit_api.dto.knowledge.ExtractedSyllabusFacts;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyllabusIngestionServiceTest {

    @Mock private KnowledgeConfig knowledgeConfig;
    @Mock private MarkerMarkdownLoader markerMarkdownLoader;
    @Mock private KnowledgeSourceService knowledgeSourceService;
    @Mock private SyllabusFactExtractor syllabusFactExtractor;
    @Mock private SyllabusValidator syllabusValidator;
    @Mock private CourseKnowledgeIndexer courseKnowledgeIndexer;
    @Mock private CourseSyllabusRepository courseSyllabusRepository;
    @Mock private CourseObjectiveRepository courseObjectiveRepository;
    @Mock private CourseOutcomeRepository courseOutcomeRepository;
    @Mock private CourseSessionRepository courseSessionRepository;
    @Mock private CourseAssessmentRepository courseAssessmentRepository;
    @Mock private CourseReferenceRepository courseReferenceRepository;
    @Mock private CourseToolRepository courseToolRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SyllabusIngestionService createService() {
        return new SyllabusIngestionService(
                knowledgeConfig, markerMarkdownLoader, knowledgeSourceService,
                syllabusFactExtractor, syllabusValidator, courseKnowledgeIndexer,
                courseSyllabusRepository, courseObjectiveRepository, courseOutcomeRepository,
                courseSessionRepository, courseAssessmentRepository, courseReferenceRepository,
                courseToolRepository, objectMapper
        );
    }

    private ExtractedSyllabusFacts minimalFacts() {
        return new ExtractedSyllabusFacts(
                "IT001", "Course A", "Course A EN",
                3, 30, 15, 15, null, null, "CS", "Desc",
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of()
        );
    }

    @Test
    void ingestFolder_disabled_returnsEmptySummary() throws Exception {
        when(knowledgeConfig.isIngestionEnabled()).thenReturn(false);
        var service = createService();

        var summary = service.ingestFolder();

        assertThat(summary.totalFiles()).isZero();
        assertThat(summary.courseCodes()).isEmpty();
    }

    @Test
    void ingestOneMarksSourceFailed_onExtractionError() throws Exception {
        var service = createService();
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setFileName("IT001.md");

        when(markerMarkdownLoader.loadOne(any()))
                .thenReturn(new MarkerMarkdownLoader.LoadedMarkdown(
                        Path.of("IT001.md"), "IT001.md", "raw", "hash123"));
        when(knowledgeSourceService.findByContentHash("hash123")).thenReturn(Optional.empty());
        when(knowledgeSourceService.createSource(any(), any(), any(), any(), any(), any()))
                .thenReturn(source);
        // ExtractionResult(facts, errorMessage) — facts=null means failure
        when(syllabusFactExtractor.extract("raw"))
                .thenReturn(new SyllabusFactExtractor.ExtractionResult(null, "LLM error"));

        var report = service.ingestOneMarkdown(Path.of("IT001.md"));

        assertThat(report.status()).isEqualTo("FAILED");
        verify(knowledgeSourceService).markFailed(source.getId(), "LLM error");
    }

    @Test
    void ingestOneMarksSourceFailed_onValidationError() throws Exception {
        var service = createService();
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());

        when(markerMarkdownLoader.loadOne(any()))
                .thenReturn(new MarkerMarkdownLoader.LoadedMarkdown(
                        Path.of("IT001.md"), "IT001.md", "raw", "hash123"));
        when(knowledgeSourceService.findByContentHash("hash123")).thenReturn(Optional.empty());
        when(knowledgeSourceService.createSource(any(), any(), any(), any(), any(), any()))
                .thenReturn(source);
        when(syllabusFactExtractor.extract("raw"))
                .thenReturn(SyllabusFactExtractor.ExtractionResult.success(minimalFacts()));
        when(syllabusValidator.validate(any()))
                .thenReturn(new SyllabusValidator.ValidationResult(
                        List.of("courseCode is required"), false));

        var report = service.ingestOneMarkdown(Path.of("IT001.md"));

        assertThat(report.status()).isEqualTo("FAILED");
        verify(knowledgeSourceService).markFailed(eq(source.getId()), contains("courseCode"));
    }

    @Test
    void ingestOne_usesDeleteBySourceId_forAllTables() throws Exception {
        var service = createService();
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());

        when(markerMarkdownLoader.loadOne(any()))
                .thenReturn(new MarkerMarkdownLoader.LoadedMarkdown(
                        Path.of("IT001.md"), "IT001.md", "raw", "hash123"));
        when(knowledgeSourceService.findByContentHash("hash123")).thenReturn(Optional.empty());
        when(knowledgeSourceService.createSource(any(), any(), any(), any(), any(), any()))
                .thenReturn(source);
        when(syllabusFactExtractor.extract("raw"))
                .thenReturn(SyllabusFactExtractor.ExtractionResult.success(minimalFacts()));
        when(syllabusValidator.validate(any()))
                .thenReturn(new SyllabusValidator.ValidationResult(List.of(), true));

        service.ingestOneMarkdown(Path.of("IT001.md"));

        // Verify deleteBySourceId is called, NOT deleteByCourseCode
        verify(courseSyllabusRepository).deleteBySourceId(source.getId());
        verify(courseObjectiveRepository).deleteBySourceId(source.getId());
        verify(courseOutcomeRepository).deleteBySourceId(source.getId());
        verify(courseSessionRepository).deleteBySourceId(source.getId());
        verify(courseAssessmentRepository).deleteBySourceId(source.getId());
        verify(courseReferenceRepository).deleteBySourceId(source.getId());
        verify(courseToolRepository).deleteBySourceId(source.getId());

        // Verify deleteByCourseCode is NEVER called on child repos
        verify(courseObjectiveRepository, never()).deleteByCourseCode(anyString());
        verify(courseOutcomeRepository, never()).deleteByCourseCode(anyString());
    }

    @Test
    void ingestOne_skipsUnchangedCompletedSource() throws Exception {
        var service = createService();
        KnowledgeSource source = new KnowledgeSource();
        source.setId(UUID.randomUUID());
        source.setStatus("COMPLETED");

        when(markerMarkdownLoader.loadOne(any()))
                .thenReturn(new MarkerMarkdownLoader.LoadedMarkdown(
                        Path.of("IT001.md"), "IT001.md", "raw", "hash123"));
        when(knowledgeSourceService.findByContentHash("hash123")).thenReturn(Optional.of(source));

        var report = service.ingestOneMarkdown(Path.of("IT001.md"));

        assertThat(report.status()).isEqualTo("SKIPPED");
        verify(syllabusFactExtractor, never()).extract(anyString());
    }
}
