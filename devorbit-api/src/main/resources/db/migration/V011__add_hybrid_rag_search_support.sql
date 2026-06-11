-- ================================================================
-- V011: Hybrid RAG search and hierarchical chunks
-- ================================================================

ALTER TABLE knowledge_chunks
    ADD COLUMN IF NOT EXISTS chunk_kind VARCHAR(30) NOT NULL DEFAULT 'DETAIL';

ALTER TABLE knowledge_chunks
    ADD COLUMN IF NOT EXISTS parent_chunk_id UUID NULL REFERENCES knowledge_chunks(id) ON DELETE CASCADE;

ALTER TABLE knowledge_chunks
    ADD COLUMN IF NOT EXISTS search_text tsvector GENERATED ALWAYS AS (
        to_tsvector('simple', coalesce(section_title, '') || ' ' || coalesce(chunk_text, ''))
    ) STORED;

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_search_text
    ON knowledge_chunks USING GIN (search_text);

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_parent
    ON knowledge_chunks(parent_chunk_id);

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_kind
    ON knowledge_chunks(chunk_kind);
