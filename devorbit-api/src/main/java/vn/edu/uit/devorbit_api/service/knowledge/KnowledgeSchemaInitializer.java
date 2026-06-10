package vn.edu.uit.devorbit_api.service.knowledge;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Keeps the Knowledge RAG storage available in environments where SQL
 * migrations are not applied automatically.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    @Value("${devorbit.knowledge.schema-init.enabled:true}")
    private boolean enabled;

    @PostConstruct
    public void initialize() {
        if (!enabled) {
            return;
        }

        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS knowledge_sources (
                    id UUID PRIMARY KEY,
                    source_type VARCHAR(100) NOT NULL,
                    title TEXT,
                    file_name TEXT,
                    file_path TEXT,
                    url TEXT,
                    content_hash TEXT NOT NULL,
                    trust_level VARCHAR(50) NOT NULL DEFAULT 'OFFICIAL',
                    status VARCHAR(50) NOT NULL,
                    raw_text TEXT,
                    error_message TEXT,
                    created_at TIMESTAMP DEFAULT NOW(),
                    updated_at TIMESTAMP DEFAULT NOW()
                )
                """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS knowledge_chunks (
                    id UUID PRIMARY KEY,
                    source_id UUID NOT NULL REFERENCES knowledge_sources(id) ON DELETE CASCADE,
                    course_code VARCHAR(50),
                    chunk_index INT NOT NULL,
                    section_title TEXT,
                    chunk_text TEXT NOT NULL,
                    metadata_json JSONB,
                    page_from INT,
                    page_to INT,
                    embedding vector(4096),
                    created_at TIMESTAMP DEFAULT NOW()
                )
                """);
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ADD COLUMN IF NOT EXISTS page_from INT
                """);
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ADD COLUMN IF NOT EXISTS page_to INT
                """);
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ADD COLUMN IF NOT EXISTS embedding vector(4096)
                """);
            jdbcTemplate.execute("DROP INDEX IF EXISTS idx_knowledge_chunks_embedding");
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ALTER COLUMN embedding TYPE vector(4096)
                    USING NULL
                """);
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_sources_hash ON knowledge_sources(content_hash)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_code ON knowledge_chunks(course_code)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_source ON knowledge_chunks(source_id)");
            // Fireworks qwen3-embedding-8b returns 4096 dimensions. pgvector exact
            // search supports this, but ivfflat indexes are limited to 2000 dims.
            log.info("Knowledge RAG schema is ready");
        } catch (Exception e) {
            log.warn("Knowledge RAG schema initialization skipped/failed: {}", e.getMessage());
        }
    }
}
