package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;
import vn.edu.uit.devorbit_api.repository.KnowledgeSourceRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Builds RAG-ready knowledge from trusted DevOrbit database rows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseKnowledgeBootstrapService {

    private final KnowledgeSourceRepository knowledgeSourceRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final CourseKnowledgeIndexer courseKnowledgeIndexer;
    private final KnowledgeEmbeddingService knowledgeEmbeddingService;

    public void ensureCourseIndexed(Course course, List<GithubRepo> repos) {
        if (course == null || course.getMaMH() == null || course.getMaMH().isBlank()) {
            return;
        }

        String markdown = buildCourseMarkdown(course, repos == null ? List.of() : repos);
        String contentHash = sha256(markdown);
        Optional<KnowledgeSource> existing = knowledgeSourceRepository.findByContentHash(contentHash);

        if (existing.isPresent() && isReady(existing.get())) {
            log.debug("Course knowledge already indexed for {}", course.getMaMH());
            return;
        }

        KnowledgeSource source = existing.orElseGet(() -> createSource(course, contentHash, markdown));
        try {
            courseKnowledgeIndexer.indexMarkdown(source, course.getMaMH(), markdown);
            int embedded = knowledgeEmbeddingService.embedChunksForSource(source.getId(), false);
            source.setStatus("COMPLETED");
            source.setErrorMessage(null);
            source.setRawText(markdown);
            knowledgeSourceRepository.save(source);
            log.info("Course knowledge indexed for {}: embedded {} chunks", course.getMaMH(), embedded);
        } catch (Exception e) {
            source.setStatus("FAILED");
            source.setErrorMessage(e.getMessage());
            knowledgeSourceRepository.save(source);
            log.warn("Course knowledge indexing failed for {}: {}", course.getMaMH(), e.getMessage());
        }
    }

    private boolean isReady(KnowledgeSource source) {
        if (!"COMPLETED".equalsIgnoreCase(source.getStatus())) {
            return false;
        }
        List<KnowledgeChunk> chunks = knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(source.getId());
        return chunks.stream().anyMatch(chunk -> chunk.getEmbedding() != null);
    }

    private KnowledgeSource createSource(Course course, String contentHash, String markdown) {
        KnowledgeSource source = KnowledgeSource.builder()
            .sourceType("DEVORBIT_COURSE")
            .fileName("devorbit-course-" + course.getMaMH() + ".md")
            .filePath("devorbit-db://courses/" + course.getMaMH())
            .title(course.getMaMH() + " - " + safe(course.getTenMH()))
            .contentHash(contentHash)
            .trustLevel("DEVORBIT_DB")
            .status("PENDING")
            .rawText(markdown)
            .build();
        return knowledgeSourceRepository.save(source);
    }

    private String buildCourseMarkdown(Course course, List<GithubRepo> repos) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(safe(course.getMaMH())).append(" - ").append(safe(course.getTenMH())).append("\n\n");
        appendLine(markdown, "English name", course.getTenMH_EN());
        appendLine(markdown, "Credits", String.valueOf(course.getSoTC()));
        appendLine(markdown, "Theory hours", String.valueOf(course.getLt()));
        appendLine(markdown, "Practice hours", String.valueOf(course.getTh()));
        appendLine(markdown, "Course type", course.getLoaiMonHoc());
        appendLine(markdown, "Management unit", course.getManagementUnit());
        appendLine(markdown, "Semester", course.getSemester() == null ? null : String.valueOf(course.getSemester()));
        appendLine(markdown, "Old course code", course.getMaMH_Old());
        appendLine(markdown, "Equivalent course", course.getEquivalentMH());
        appendLine(markdown, "Prerequisite course", course.getPrerequisiteMH());
        appendLine(markdown, "Previous/recommended course", course.getPreviousMH());

        appendSection(markdown, "Description", course.getDescription());
        appendSection(markdown, "Learning objectives", course.getLearningObjectives());
        appendSection(markdown, "Grading criteria", course.getGradingCriteria());
        if (course.getTopics() != null && !course.getTopics().isEmpty()) {
            appendSection(markdown, "Topics", course.getTopics().toPrettyString());
        }

        markdown.append("## Linked GitHub repositories on DevOrbit\n\n");
        if (repos.isEmpty()) {
            markdown.append("DevOrbit currently has no active GitHub repositories linked to this course.\n");
        } else {
            for (GithubRepo repo : repos) {
                appendRepo(markdown, repo);
            }
        }
        return markdown.toString();
    }

    private void appendRepo(StringBuilder markdown, GithubRepo repo) {
        markdown.append("### ").append(safe(firstNonBlank(repo.getDisplayName(), repo.getRepoName()))).append("\n\n");
        appendLine(markdown, "Repository name", repo.getRepoName());
        appendLine(markdown, "GitHub URL", repo.getGithubUrl());
        appendLine(markdown, "Subject code", repo.getSubjectId());
        appendLine(markdown, "Description", repo.getDescription());
        appendLine(markdown, "Primary language", repo.getPrimaryLanguage());
        appendLine(markdown, "Legacy tech stack", repo.getTechStack());
        appendLine(markdown, "Stars", repo.getStars() == null ? null : String.valueOf(repo.getStars()));
        appendLine(markdown, "Last pushed at", repo.getLastPushedAt());
        appendLine(markdown, "Has README", repo.getHasReadme() == null ? null : String.valueOf(repo.getHasReadme()));
        appendSection(markdown, "README excerpt", repo.getReadmeExcerpt());
        appendSection(markdown, "File tree", repo.getFileTree());
    }

    private void appendLine(StringBuilder markdown, String label, String value) {
        if (value != null && !value.isBlank()) {
            markdown.append("- ").append(label).append(": ").append(value.trim()).append("\n");
        }
    }

    private void appendSection(StringBuilder markdown, String title, String value) {
        if (value != null && !value.isBlank()) {
            markdown.append("\n## ").append(title).append("\n\n").append(value.trim()).append("\n\n");
        }
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
