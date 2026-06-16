-- Reconstructed from remote supabase_migrations.schema_migrations for local history sync.
ALTER TABLE public.courses
ADD COLUMN IF NOT EXISTS tenmh_en character varying(255),
ADD COLUMN IF NOT EXISTS is_open boolean DEFAULT true,
ADD COLUMN IF NOT EXISTS management_unit character varying(100),
ADD COLUMN IF NOT EXISTS mamh_old character varying(50),
ADD COLUMN IF NOT EXISTS equivalent_mh text,
ADD COLUMN IF NOT EXISTS prerequisite_mh text,
ADD COLUMN IF NOT EXISTS previous_mh text;
