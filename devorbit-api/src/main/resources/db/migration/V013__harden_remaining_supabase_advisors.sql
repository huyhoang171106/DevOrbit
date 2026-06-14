-- Remove duplicate bookmark uniqueness created by mixed Hibernate/manual DDL.
-- The named application constraint remains in place.
ALTER TABLE public.student_bookmarks
    DROP CONSTRAINT IF EXISTS uk9pk7ct27f5eseu5adcp0322wv;

-- Public buckets can serve public object URLs without a broad storage.objects
-- SELECT policy that also permits listing every object in the bucket.
DROP POLICY IF EXISTS "Public read access" ON storage.objects;

-- This table is not used by the Spring backend. If kept for public Data API
-- intake, allow only minimally valid pending submissions and avoid broad ALL
-- access via auth.role().
DROP POLICY IF EXISTS "Admin All Access" ON public.tutor_registrations;
DROP POLICY IF EXISTS "Public Insert Access" ON public.tutor_registrations;

CREATE POLICY "Public insert tutor registrations limited"
ON public.tutor_registrations
FOR INSERT
TO anon, authenticated
WITH CHECK (
    length(trim(full_name)) BETWEEN 2 AND 255
    AND length(trim(school)) BETWEEN 2 AND 255
    AND length(trim(education_level)) BETWEEN 2 AND 255
    AND length(trim(subject)) BETWEEN 2 AND 255
    AND length(trim(email)) BETWEEN 5 AND 320
    AND email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'
    AND length(trim(phone)) BETWEEN 7 AND 32
    AND coalesce(status, 'pending') = 'pending'
);
