-- Drop vector column that H2 cannot handle (pgvector extension)
-- This is only for the lifecycle test profile; production uses PostgreSQL with pgvector
ALTER TABLE knowledge_chunks DROP COLUMN IF EXISTS embedding;
