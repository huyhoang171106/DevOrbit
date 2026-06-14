package vn.edu.uit.devorbit_api.service.knowledge;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

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

    /**
     * Ensures the embedding column exists and has the correct dimension (4096).
     * Used by tests and startup initialization.
     */
    public void ensureEmbeddingColumn() {
        try {
            String embeddingType = currentEmbeddingColumnType();
            if (embeddingType == null) {
                jdbcTemplate.execute("ALTER TABLE knowledge_chunks ADD COLUMN embedding extensions.vector(4096)");
                return;
            }

            if (!isVector4096(embeddingType)) {
                jdbcTemplate.execute("DROP INDEX IF EXISTS idx_knowledge_chunks_embedding");
                jdbcTemplate.execute("ALTER TABLE knowledge_chunks ALTER COLUMN embedding TYPE extensions.vector(4096) USING NULL");
            }
        } catch (Exception e) {
            log.warn("ensureEmbeddingColumn failed: {}", e.getMessage());
        }
    }

    private String currentEmbeddingColumnType() {
        return jdbcTemplate.query("""
            SELECT format_type(a.atttypid, a.atttypmod)
            FROM pg_attribute a
            JOIN pg_class c ON c.oid = a.attrelid
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = 'public'
              AND c.relname = 'knowledge_chunks'
              AND a.attname = 'embedding'
              AND NOT a.attisdropped
            """, rs -> rs.next() ? rs.getString(1) : null);
    }

    private boolean isVector4096(String columnType) {
        return columnType != null
                && columnType.toLowerCase(Locale.ROOT).replace(" ", "").endsWith("vector(4096)");
    }

    @PostConstruct
    public void initialize() {
        if (!enabled) {
            return;
        }

        try {
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS extensions");
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA extensions");
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector') THEN
                        ALTER EXTENSION vector SET SCHEMA extensions;
                    END IF;
                END $$
                """);
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
                    chunk_kind VARCHAR(30) NOT NULL DEFAULT 'DETAIL',
                    parent_chunk_id UUID,
                    embedding extensions.vector(4096),
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
                    ADD COLUMN IF NOT EXISTS chunk_kind VARCHAR(30) NOT NULL DEFAULT 'DETAIL'
                """);
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ADD COLUMN IF NOT EXISTS parent_chunk_id UUID
                """);
            jdbcTemplate.execute("""
                ALTER TABLE knowledge_chunks
                    ADD COLUMN IF NOT EXISTS embedding extensions.vector(4096)
                """);
            ensureEmbeddingColumn();
            // Generated search_text column for PostgreSQL FTS (only on PostgreSQL)
            try {
                jdbcTemplate.execute("""
                    ALTER TABLE knowledge_chunks
                        ADD COLUMN IF NOT EXISTS search_text tsvector
                        GENERATED ALWAYS AS (
                            to_tsvector('simple', coalesce(section_title, '') || ' ' || coalesce(chunk_text, ''))
                        ) STORED
                    """);
            } catch (Exception e) {
                log.warn("Could not add search_text column (may not be PostgreSQL): {}", e.getMessage());
            }
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_sources_hash ON knowledge_sources(content_hash)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_code ON knowledge_chunks(course_code)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_source ON knowledge_chunks(source_id)");
            try {
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_search_text ON knowledge_chunks USING GIN (search_text)");
            } catch (Exception e) {
                log.warn("Could not create GIN index on search_text (may not be PostgreSQL): {}", e.getMessage());
            }
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_parent ON knowledge_chunks(parent_chunk_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_kind ON knowledge_chunks(chunk_kind)");
            // Fireworks qwen3-embedding-8b returns 4096 dimensions. pgvector exact
            // search supports this, but ivfflat indexes are limited to 2000 dims.
            log.info("Knowledge RAG schema is ready");
        } catch (Exception e) {
            log.warn("Knowledge RAG schema initialization skipped/failed: {}", e.getMessage());
        }
    }
}
