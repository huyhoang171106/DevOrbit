package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import vn.edu.uit.devorbit_api.config.KnowledgeConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarkerMarkdownLoaderTest {

    @TempDir
    Path tempDir;

    private MarkerMarkdownLoader createLoader() {
        KnowledgeConfig config = mock(KnowledgeConfig.class);
        when(config.getMarkerMdDir()).thenReturn(tempDir.toString());
        return new MarkerMarkdownLoader(config);
    }

    @Test
    void loadOne_readsFileAndComputesHash() throws IOException {
        Path md = tempDir.resolve("IT001.md");
        Files.writeString(md, "# IT001\nSome content\n");
        MarkerMarkdownLoader loader = createLoader();

        MarkerMarkdownLoader.LoadedMarkdown result = loader.loadOne(md);

        assertThat(result.fileName()).isEqualTo("IT001.md");
        assertThat(result.rawMarkdown()).contains("# IT001");
        assertThat(result.contentHash()).hasSize(64); // SHA-256 hex
    }

    @Test
    void loadOne_hashIsDeterministic() throws IOException {
        Path md = tempDir.resolve("IT001.md");
        String content = "# IT001\nDeterministic content\n";
        Files.writeString(md, content);
        MarkerMarkdownLoader loader = createLoader();

        String hash1 = loader.loadOne(md).contentHash();
        String hash2 = loader.loadOne(md).contentHash();

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void loadOne_hashChangesWithContent() throws IOException {
        Path md1 = tempDir.resolve("IT001.md");
        Path md2 = tempDir.resolve("IT002.md");
        Files.writeString(md1, "# IT001\nContent A\n");
        Files.writeString(md2, "# IT002\nContent B\n");
        MarkerMarkdownLoader loader = createLoader();

        String hash1 = loader.loadOne(md1).contentHash();
        String hash2 = loader.loadOne(md2).contentHash();

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void loadFromDirectory_findsMdFilesRecursively() throws IOException {
        Files.writeString(tempDir.resolve("IT001.md"), "# IT001\n");
        Path sub = tempDir.resolve("subdir");
        Files.createDirectories(sub);
        Files.writeString(sub.resolve("IT002.md"), "# IT002\n");
        MarkerMarkdownLoader loader = createLoader();

        List<MarkerMarkdownLoader.LoadedMarkdown> results = loader.loadFromDirectory(tempDir);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(MarkerMarkdownLoader.LoadedMarkdown::fileName)
                .containsExactlyInAnyOrder("IT001.md", "IT002.md");
    }

    @Test
    void loadFromDirectory_skipsNonMdFiles() throws IOException {
        Files.writeString(tempDir.resolve("IT001.md"), "# IT001\n");
        Files.writeString(tempDir.resolve("notes.txt"), "not a markdown file");
        MarkerMarkdownLoader loader = createLoader();

        List<MarkerMarkdownLoader.LoadedMarkdown> results = loader.loadFromDirectory(tempDir);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).fileName()).isEqualTo("IT001.md");
    }

    @Test
    void loadFromDirectory_returnsEmptyForNonExistentDir() {
        MarkerMarkdownLoader loader = createLoader();
        Path fakeDir = tempDir.resolve("nonexistent");

        List<MarkerMarkdownLoader.LoadedMarkdown> results = loader.loadFromDirectory(fakeDir);

        assertThat(results).isEmpty();
    }
}
