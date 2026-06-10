package vn.edu.uit.devorbit_api.service.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;
import vn.edu.uit.devorbit_api.entity.KnowledgeSource;
import vn.edu.uit.devorbit_api.repository.KnowledgeChunkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chunks raw markdown into section-aware chunks for future RAG retrieval.
 * No embedding yet — just stores chunk text with metadata.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourseKnowledgeIndexer {

    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final ObjectMapper objectMapper;

    private static final int TARGET_CHUNK_SIZE = 4000; // ~1000 tokens
    private static final int MAX_CHUNK_SIZE = 5000;
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,4})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern PAGE_MARKER_PATTERN = Pattern.compile(
        "\\[(?:Page|Trang)\\s+(\\d+)(?:\\s*[-–]\\s*(\\d+))?\\]", Pattern.CASE_INSENSITIVE);

    /**
     * Chunk markdown and store chunks in the database.
     * Deletes existing chunks for the courseCode from the same source before inserting.
     */
    @Transactional
    public List<KnowledgeChunk> indexMarkdown(KnowledgeSource source, String courseCode, String markdown) {
        // Delete existing chunks from THIS source only (not other sources for same course)
        knowledgeChunkRepository.deleteBySourceId(source.getId());

        List<ChunkInfo> chunked = chunkMarkdown(markdown);
        List<KnowledgeChunk> saved = new ArrayList<>();

        for (int i = 0; i < chunked.size(); i++) {
            ChunkInfo info = chunked.get(i);
            ObjectNode meta = objectMapper.createObjectNode();
            meta.put("sourceFile", source.getFileName());
            meta.put("courseCode", courseCode);
            meta.put("chunkIndex", i);
            if (info.sectionTitle != null) {
                meta.put("section", info.sectionTitle);
            }
            if (info.pageFrom != null) {
                meta.put("pageFrom", info.pageFrom);
            }
            if (info.pageTo != null) {
                meta.put("pageTo", info.pageTo);
            }

            KnowledgeChunk.KnowledgeChunkBuilder builder = KnowledgeChunk.builder()
                    .source(source)
                    .courseCode(courseCode)
                    .chunkIndex(i)
                    .sectionTitle(info.sectionTitle)
                    .chunkText(info.text)
                    .metadataJson(meta);
            if (info.pageFrom != null) {
                builder.pageFrom(info.pageFrom);
            }
            if (info.pageTo != null) {
                builder.pageTo(info.pageTo);
            }
            KnowledgeChunk chunk = builder.id(UUID.randomUUID()).build();
            knowledgeChunkRepository.insertChunkWithoutEmbedding(
                chunk.getId(),
                source.getId(),
                chunk.getCourseCode(),
                chunk.getChunkIndex(),
                chunk.getSectionTitle(),
                chunk.getChunkText(),
                metadataJson(chunk),
                chunk.getPageFrom(),
                chunk.getPageTo()
            );
            saved.add(chunk);
        }

        log.info("Indexed {} chunks for course {} from source {}", saved.size(), courseCode, source.getId());
        return saved;
    }

    /**
     * Get chunks for a course.
     */
    public List<KnowledgeChunk> getChunks(String courseCode) {
        return knowledgeChunkRepository.findByCourseCodeOrderByChunkIndexAsc(courseCode);
    }

    /**
     * Chunk markdown into section-aware pieces.
     */
    List<ChunkInfo> chunkMarkdown(String markdown) {
        List<ChunkInfo> chunks = new ArrayList<>();

        // Split by headings
        List<Section> sections = splitByHeadings(markdown);

        StringBuilder currentChunk = new StringBuilder();
        String currentSection = null;
        Integer currentChunkPageFrom = null;
        Integer currentChunkPageTo = null;

        for (Section section : sections) {
            String sectionText = section.fullText();

            if (currentChunk.length() + sectionText.length() > TARGET_CHUNK_SIZE && currentChunk.length() > 0) {
                // Save current chunk
                chunks.add(new ChunkInfo(currentSection, currentChunk.toString().trim(),
                    currentChunkPageFrom, currentChunkPageTo));
                currentChunk = new StringBuilder();
                currentSection = section.title();
                currentChunkPageFrom = section.pageFrom();
                currentChunkPageTo = section.pageTo();
            }

            // If single section is too large, split it further
            if (sectionText.length() > MAX_CHUNK_SIZE) {
                if (currentChunk.length() > 0) {
                    chunks.add(new ChunkInfo(currentSection, currentChunk.toString().trim(),
                        currentChunkPageFrom, currentChunkPageTo));
                    currentChunk = new StringBuilder();
                    currentSection = section.title();
                    currentChunkPageFrom = section.pageFrom();
                    currentChunkPageTo = section.pageTo();
                }
                chunks.addAll(splitLargeSection(section));
            } else {
                if (currentSection == null) {
                    currentSection = section.title();
                }
                if (currentChunkPageFrom == null && section.pageFrom() != null) {
                    currentChunkPageFrom = section.pageFrom();
                }
                if (section.pageTo() != null) {
                    currentChunkPageTo = section.pageTo();
                }
                currentChunk.append(sectionText).append("\n\n");
            }
        }

        // Add remaining content
        if (currentChunk.length() > 0) {
            chunks.add(new ChunkInfo(currentSection, currentChunk.toString().trim(),
                currentChunkPageFrom, currentChunkPageTo));
        }

        return chunks;
    }

    private List<Section> splitByHeadings(String markdown) {
        List<Section> sections = new ArrayList<>();
        Matcher matcher = HEADING_PATTERN.matcher(markdown);

        int lastEnd = 0;
        String lastTitle = null;
        Integer lastPageFrom = null;
        Integer lastPageTo = null;

        while (matcher.find()) {
            if (lastEnd < matcher.start()) {
                String content = markdown.substring(lastEnd, matcher.start()).trim();
                if (!content.isEmpty()) {
                    sections.add(new Section(lastTitle, content + "\n", lastPageFrom, lastPageTo));
                }
            }
            lastTitle = matcher.group(2).trim();
            lastEnd = matcher.start();
            // Extract page markers from the heading text
            Integer[] pages = extractPageRange(matcher.group(0));
            lastPageFrom = pages[0];
            lastPageTo = pages[1];
        }

        // Add remaining content after last heading
        if (lastEnd < markdown.length()) {
            String content = markdown.substring(lastEnd).trim();
            if (!content.isEmpty()) {
                sections.add(new Section(lastTitle, content + "\n", lastPageFrom, lastPageTo));
            }
        }

        // If no headings found, treat entire markdown as one section
        if (sections.isEmpty()) {
            Integer[] pages = extractPageRange(markdown);
            sections.add(new Section(null, markdown, pages[0], pages[1]));
        }

        return sections;
    }

    /**
     * Extract page range from text containing [Page X-Y] or [Trang X] markers.
     * Returns [pageFrom, pageTo] — either may be null.
     */
    private Integer[] extractPageRange(String text) {
        Integer pageFrom = null;
        Integer pageTo = null;
        Matcher m = PAGE_MARKER_PATTERN.matcher(text);
        if (m.find()) {
            pageFrom = Integer.parseInt(m.group(1));
            pageTo = m.group(2) != null ? Integer.parseInt(m.group(2)) : pageFrom;
        }
        return new Integer[]{pageFrom, pageTo};
    }

    private List<ChunkInfo> splitLargeSection(Section section) {
        List<ChunkInfo> chunks = new ArrayList<>();
        String text = section.fullText();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + TARGET_CHUNK_SIZE, text.length());

            // Try to break at a paragraph boundary
            if (end < text.length()) {
                int newlinePos = text.lastIndexOf("\n\n", end);
                if (newlinePos > start + TARGET_CHUNK_SIZE / 2) {
                    end = newlinePos + 2;
                }
            }

            chunks.add(new ChunkInfo(section.title(), text.substring(start, end).trim(),
                section.pageFrom(), section.pageTo()));
            start = end;
        }

        return chunks;
    }

    private String metadataJson(KnowledgeChunk chunk) {
        try {
            return objectMapper.writeValueAsString(chunk.getMetadataJson());
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize knowledge chunk metadata", e);
        }
    }

    record ChunkInfo(String sectionTitle, String text, Integer pageFrom, Integer pageTo) {}
    record Section(String title, String fullText, Integer pageFrom, Integer pageTo) {}
}
