ALTER TABLE public.student_users
    ADD COLUMN IF NOT EXISTS token_version integer NOT NULL DEFAULT 0;

DO $$
BEGIN
    ALTER TABLE public.student_users
        ADD CONSTRAINT student_users_token_version_nonnegative
        CHECK (token_version >= 0);
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;
