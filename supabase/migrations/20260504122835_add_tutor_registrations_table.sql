-- Reconstructed from remote supabase_migrations.schema_migrations for local history sync.
CREATE TABLE IF NOT EXISTS public.tutor_registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ DEFAULT now(),
    full_name TEXT NOT NULL,
    school TEXT NOT NULL,
    education_level TEXT NOT NULL, -- THPT or Đại học
    subject TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT NOT NULL,
    facebook TEXT,
    zalo TEXT,
    status TEXT DEFAULT 'pending'
);

-- Enable RLS
ALTER TABLE public.tutor_registrations ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Insert Access" ON public.tutor_registrations FOR INSERT WITH CHECK (true);
CREATE POLICY "Admin All Access" ON public.tutor_registrations FOR ALL USING (auth.role() = 'authenticated');

