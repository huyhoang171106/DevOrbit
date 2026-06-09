package vn.edu.uit.devorbit_api.service.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.config.KnowledgeConfig;
import vn.edu.uit.devorbit_api.dto.knowledge.ExtractedSyllabusFacts;
import vn.edu.uit.devorbit_api.dto.knowledge.FolderIngestionSummary;
import vn.edu.uit.devorbit_api.dto.knowledge.IngestionReport;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main orchestration service for syllabus ingestion.
 * Coordinates loading, extraction, validation, and storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyllabusIngestionService {

    private final KnowledgeConfig knowledgeConfig;
    private final MarkerMarkdownLoader markerMarkdownLoader;
    private final KnowledgeSourceService knowledgeSourceService;
    private final SyllabusFactExtractor syllabusFactExtractor;
    private final SyllabusValidator syllabusValidator;
    private final CourseKnowledgeIndexer courseKnowledgeIndexer;
    private final CourseSyllabusRepository courseSyllabusRepository;
    private final CourseObjectiveRepository courseObjectiveRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final CourseAssessmentRepository courseAssessmentRepository;
    private final CourseReferenceRepository courseReferenceRepository;
    private final CourseToolRepository courseToolRepository;
    private final ObjectMapper objectMapper;

    /**
     * Ingest all markdown files from the configured marker-md directory.
     */
    public FolderIngestionSummary ingestFolder() {
        if (!knowledgeConfig.isIngestionEnabled()) {
            log.warn("Knowledge ingestion is disabled");
            return new FolderIngestionSummary(0, 0, 0, 0, List.of());
        }

        Path dir = Path.of(knowledgeConfig.getMarkerMdDir());
        return ingestFolder(dir);
    }

    /**
     * Ingest all markdown files from a specific directory.
     */
    public FolderIngestionSummary ingestFolder(Path folder) {
        log.info("Starting folder ingestion: {}", folder);
        List<MarkerMarkdownLoader.LoadedMarkdown> files = markerMarkdownLoader.loadFromDirectory(folder);

        int total = files.size();
        int completed = 0;
        int skipped = 0;
        int failed = 0;
        List<String> courseCodes = new ArrayList<>();

        for (MarkerMarkdownLoader.LoadedMarkdown file : files) {
            try {
                IngestionReport report = ingestOneMarkdown(file.filePath());
                courseCodes.add(report.courseCode());
                switch (report.status()) {
                    case "COMPLETED" -> completed++;
                    case "SKIPPED" -> skipped++;
                    case "FAILED" -> failed++;
                }
            } catch (Exception e) {
                log.error("Failed to ingest file: {}", file.filePath(), e);
                failed++;
            }
        }

        FolderIngestionSummary summary = new FolderIngestionSummary(total, completed, skipped, failed, courseCodes);
        log.info("Folder ingestion complete: {}", summary);
        return summary;
    }

    /**
     * Ingest a single markdown file.
     */
    @Transactional
    public IngestionReport ingestOneMarkdown(Path mdFile) {
        try {
            // Load markdown
            MarkerMarkdownLoader.LoadedMarkdown loaded = markerMarkdownLoader.loadOne(mdFile);

            // Check for unchanged file (skip if already completed with same hash)
            Optional<KnowledgeSource> existing = knowledgeSourceService.findByContentHash(loaded.contentHash());
            if (existing.isPresent() && "COMPLETED".equals(existing.get().getStatus())) {
                log.info("Skipping unchanged file: {} (hash: {})", loaded.fileName(), loaded.contentHash());
                return new IngestionReport(
                    existing.get().getId(),
                    findCourseCodeForSource(existing.get()),
                    "SKIPPED",
                    List.of(),
                    null
                );
            }

            // Create or update knowledge source
            KnowledgeSource source = existing.orElseGet(() ->
                knowledgeSourceService.createSource(
                    "MARKER_MARKDOWN",
                    loaded.fileName(),
                    loaded.filePath().toString(),
                    loaded.fileName(),
                    loaded.contentHash(),
                    loaded.rawMarkdown()
                )
            );

            // Extract facts using LLM
            SyllabusFactExtractor.ExtractionResult extraction = syllabusFactExtractor.extract(loaded.rawMarkdown());

            if (!extraction.isSuccess()) {
                knowledgeSourceService.markFailed(source.getId(), extraction.errorMessage());
                return new IngestionReport(source.getId(), null, "FAILED", List.of(), extraction.errorMessage());
            }

            ExtractedSyllabusFacts facts = extraction.facts();

            // Validate
            SyllabusValidator.ValidationResult validation = syllabusValidator.validate(facts);
            if (!validation.valid()) {
                knowledgeSourceService.markFailed(source.getId(), validation.warnings().get(0));
                return new IngestionReport(source.getId(), facts.courseCode(), "FAILED", validation.warnings(), validation.warnings().get(0));
            }

            // Save structured facts — all deletes scoped to this source
            String courseCode = facts.courseCode();
            saveSyllabus(source, facts);
            saveObjectives(source, courseCode, facts);
            saveOutcomes(source, courseCode, facts);
            saveSessions(source, courseCode, facts);
            saveAssessments(source, courseCode, facts);
            saveReferences(source, courseCode, facts);
            saveTools(source, courseCode, facts);

            // Index markdown chunks
            courseKnowledgeIndexer.indexMarkdown(source, courseCode, loaded.rawMarkdown());

            // Mark source as completed (dirty-checking will flush on tx commit)
            source.setStatus("COMPLETED");

            log.info("Successfully ingested course: {} from {}", courseCode, loaded.fileName());
            return new IngestionReport(source.getId(), courseCode, "COMPLETED", validation.warnings(), null);

        } catch (Exception e) {
            log.error("Ingestion failed for: {}", mdFile, e);
            return new IngestionReport(null, null, "FAILED", List.of(), e.getMessage());
        }
    }

    private void saveSyllabus(KnowledgeSource source, ExtractedSyllabusFacts facts) {
        // Delete existing syllabus for this source (not all sources for same course)
        courseSyllabusRepository.deleteBySourceId(source.getId());

        CourseSyllabus syllabus = CourseSyllabus.builder()
                .source(source)
                .courseCode(facts.courseCode())
                .courseNameVi(facts.courseNameVi())
                .courseNameEn(facts.courseNameEn())
                .credits(facts.credits())
                .theoryHours(facts.theoryHours())
                .practiceHours(facts.practiceHours())
                .selfStudyHours(facts.selfStudyHours())
                .prerequisite(facts.prerequisite())
                .previousCourse(facts.previousCourse())
                .department(facts.department())
                .description(facts.description())
                .build();
        courseSyllabusRepository.save(syllabus);
    }

    private void saveObjectives(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseObjectiveRepository.deleteBySourceId(source.getId());
        if (facts.objectives() != null) {
            List<CourseObjective> entities = facts.objectives().stream()
                    .map(obj -> CourseObjective.builder()
                            .source(source)
                            .courseCode(courseCode)
                            .description(obj.description())
                            .outcomeRefs(objectMapper.valueToTree(obj.outcomeRefs()))
                            .build())
                    .toList();
            courseObjectiveRepository.saveAll(entities);
        }
    }

    private void saveOutcomes(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseOutcomeRepository.deleteBySourceId(source.getId());
        if (facts.outcomes() != null) {
            List<CourseOutcome> entities = facts.outcomes().stream()
                    .map(out -> CourseOutcome.builder()
                            .source(source)
                            .courseCode(courseCode)
                            .outcomeCode(out.code())
                            .description(out.description())
                            .build())
                    .toList();
            courseOutcomeRepository.saveAll(entities);
        }
    }

    private void saveSessions(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseSessionRepository.deleteBySourceId(source.getId());
        List<CourseSession> entities = new ArrayList<>();
        addSessions(entities, source, courseCode, facts.theorySessions(), "THEORY");
        addSessions(entities, source, courseCode, facts.practiceSessions(), "PRACTICE");
        if (!entities.isEmpty()) {
            courseSessionRepository.saveAll(entities);
        }
    }

    private void addSessions(List<CourseSession> target, KnowledgeSource source, String courseCode,
                             List<ExtractedSyllabusFacts.SessionDto> sessions, String sessionType) {
        if (sessions != null) {
            for (ExtractedSyllabusFacts.SessionDto s : sessions) {
                target.add(CourseSession.builder()
                        .courseCode(courseCode)
                        .source(source)
                        .sessionNo(s.sessionNo())
                        .sessionType(sessionType)
                        .topic(s.topic())
                        .activities(s.activities())
                        .assessmentComponent(s.assessmentComponent())
                        .build());
            }
        }
    }

    private void saveAssessments(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseAssessmentRepository.deleteBySourceId(source.getId());
        if (facts.assessments() != null) {
            List<CourseAssessment> entities = facts.assessments().stream()
                    .map(a -> CourseAssessment.builder()
                            .source(source)
                            .courseCode(courseCode)
                            .componentCode(a.componentCode())
                            .description(a.description())
                            .weightPercent(a.weightPercent())
                            .build())
                    .toList();
            courseAssessmentRepository.saveAll(entities);
        }
    }

    private void saveReferences(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseReferenceRepository.deleteBySourceId(source.getId());
        if (facts.references() != null) {
            List<CourseReference> entities = facts.references().stream()
                    .map(ref -> CourseReference.builder()
                            .source(source)
                            .courseCode(courseCode)
                            .referenceText(ref)
                            .build())
                    .toList();
            courseReferenceRepository.saveAll(entities);
        }
    }

    private void saveTools(KnowledgeSource source, String courseCode, ExtractedSyllabusFacts facts) {
        courseToolRepository.deleteBySourceId(source.getId());
        if (facts.tools() != null) {
            List<CourseTool> entities = facts.tools().stream()
                    .map(tool -> CourseTool.builder()
                            .source(source)
                            .courseCode(courseCode)
                            .toolName(tool)
                            .build())
                    .toList();
            courseToolRepository.saveAll(entities);
        }
    }

    private String findCourseCodeForSource(KnowledgeSource source) {
        // Extract course code from file path — reliable since marker generates {code}.md
        String fileName = source.getFileName();
        if (fileName != null && fileName.endsWith(".md")) {
            return fileName.substring(0, fileName.length() - 3);
        }
        return null;
    }
}
