-- Reconstructed from remote supabase_migrations.schema_migrations for local history sync.
-- Supabase recommends keeping extensions outside the exposed public schema.
-- The backend uses schema-qualified extensions.vector after this migration.
CREATE SCHEMA IF NOT EXISTS extensions;
ALTER EXTENSION vector SET SCHEMA extensions;

