-- ================================================================
-- V010: Resize knowledge embeddings for Fireworks qwen3-embedding-8b
-- ================================================================

CREATE EXTENSION IF NOT EXISTS vector;

DROP INDEX IF EXISTS idx_knowledge_chunks_embedding;

-- Existing 1536-dimensional/noop embeddings cannot be cast to the 4096-dimensional
-- Fireworks model. Clear them and let the admin embed endpoints regenerate.
ALTER TABLE knowledge_chunks
    ALTER COLUMN embedding TYPE vector(4096)
    USING NULL;

-- Fireworks qwen3-embedding-8b returns 4096 dimensions. pgvector exact search
-- supports this, but ivfflat indexes are limited to 2000 dimensions.
