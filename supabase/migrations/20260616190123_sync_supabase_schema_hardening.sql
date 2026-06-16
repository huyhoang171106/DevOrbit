-- Synchronize live Supabase posture with backend-owned database rules.
-- Rollback:
--   DROP POLICY IF EXISTS "Deny direct API access" ON public.notifications;
--   DROP INDEX IF EXISTS public.idx_repo_votes_student;
--   CREATE INDEX IF NOT EXISTS idx_photobooth_frames_frame_id ON public.photobooth_frames(frame_id);
--   DROP POLICY IF EXISTS "Public read photobooth storage buckets" ON storage.objects;
--   CREATE POLICY "Allow public read ify4b9_0" ON storage.objects FOR SELECT TO public USING (true);
--   CREATE POLICY "Allow public upload ify4b9_0" ON storage.objects FOR INSERT TO public WITH CHECK (true);

CREATE INDEX IF NOT EXISTS idx_repo_votes_student
    ON public.repo_votes(student_id);

DROP INDEX IF EXISTS public.idx_photobooth_frames_frame_id;

DO $$
BEGIN
    IF to_regclass('public.notifications') IS NOT NULL
       AND NOT EXISTS (
           SELECT 1
           FROM pg_policies
           WHERE schemaname = 'public'
             AND tablename = 'notifications'
             AND policyname = 'Deny direct API access'
       ) THEN
        CREATE POLICY "Deny direct API access"
        ON public.notifications
        FOR ALL
        TO anon, authenticated
        USING (false)
        WITH CHECK (false);
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('storage.objects') IS NOT NULL THEN
        DROP POLICY IF EXISTS "Public read access" ON storage.objects;
        DROP POLICY IF EXISTS "Allow public upload ify4b9_0" ON storage.objects;
        DROP POLICY IF EXISTS "Allow public read ify4b9_0" ON storage.objects;
        DROP POLICY IF EXISTS "Public read photobooth storage buckets" ON storage.objects;

        CREATE POLICY "Public read photobooth storage buckets"
        ON storage.objects
        FOR SELECT
        TO public
        USING (bucket_id IN ('devorbit', 'frame-overlays'));
    END IF;
END $$;
