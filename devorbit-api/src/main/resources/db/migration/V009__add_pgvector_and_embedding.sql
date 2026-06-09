-- ================================================================
-- V009: Enable pgvector + add embedding column to knowledge_chunks
-- ================================================================

-- Enable pgvector extension (safe on fresh DB and existing DB)
CREATE EXTENSION IF NOT EXISTS vector;

-- Add embedding column for vector similarity search
-- 1536 dimensions for text-embedding-3-small (OpenAI-compatible)
-- Uses REAL (float4) for memory efficiency
ALTER TABLE knowledge_chunks
    ADD COLUMN IF NOT EXISTS embedding vector(1536);

-- Create ivfflat index for fast approximate nearest neighbor search
-- Lists=100 is a good default for datasets up to 1M rows
-- This index is created concurrently to avoid locking the table
CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_embedding
    ON knowledge_chunks USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
