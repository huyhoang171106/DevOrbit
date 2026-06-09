package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.config.KnowledgeConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads marker-generated markdown files from a configured directory.
 * Supports both folder-per-course and flat file layouts.
 */
@Slf4j
@Component
public class MarkerMarkdownLoader {

    private final KnowledgeConfig knowledgeConfig;

    public MarkerMarkdownLoader(KnowledgeConfig knowledgeConfig) {
        this.knowledgeConfig = knowledgeConfig;
    }

    /**
     * Represents a loaded markdown file.
     */
    public record LoadedMarkdown(
        Path filePath,
        String fileName,
        String rawMarkdown,
        String contentHash
    ) {}

    /**
     * Scan the configured marker-md directory recursively for .md files.
     */
    public List<LoadedMarkdown> loadAll() {
        Path dir = Path.of(knowledgeConfig.getMarkerMdDir());
        return loadFromDirectory(dir);
    }

    /**
     * Scan a specific directory recursively for .md files.
     */
    public List<LoadedMarkdown> loadFromDirectory(Path dir) {
        List<LoadedMarkdown> results = new ArrayList<>();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            log.warn("Marker markdown directory does not exist: {}", dir);
            return results;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(p -> p.toString().endsWith(".md"))
                 .forEach(p -> {
                     try {
                         results.add(loadOne(p));
                     } catch (IOException e) {
                         log.error("Failed to read markdown file: {}", p, e);
                     }
                 });
        } catch (IOException e) {
            log.error("Failed to walk directory: {}", dir, e);
        }
        log.info("Loaded {} markdown files from {}", results.size(), dir);
        return results;
    }

    /**
     * Load a single markdown file.
     */
    public LoadedMarkdown loadOne(Path mdFile) throws IOException {
        String rawMarkdown = Files.readString(mdFile, StandardCharsets.UTF_8);
        String contentHash = computeHash(rawMarkdown);
        String fileName = mdFile.getFileName().toString();
        log.debug("Loaded markdown: {} (hash: {})", fileName, contentHash);
        return new LoadedMarkdown(mdFile, fileName, rawMarkdown, contentHash);
    }

    /**
     * Compute SHA-256 hash of content.
     */
    static String computeHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
