-- Drop vector column that H2 cannot handle (pgvector extension)
-- This is only for the lifecycle test profile; production uses PostgreSQL with pgvector
ALTER TABLE knowledge_chunks DROP COLUMN IF EXISTS embedding;

-- Ensure H2-generated chat_channels.active column has a default (entity marks it NOT NULL)
ALTER TABLE chat_channels ALTER COLUMN active SET DEFAULT true;
