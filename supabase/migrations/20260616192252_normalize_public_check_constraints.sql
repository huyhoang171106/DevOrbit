-- Normalize CHECK constraint definitions so Supabase shadow diffs reconstruct
-- the same public schema. This does not change allowed values.
-- Rollback: re-run the same statements; the logical constraints are unchanged.

ALTER TABLE public.chat_channels
    DROP CONSTRAINT IF EXISTS chat_channels_type_check;

ALTER TABLE public.chat_channels
    ADD CONSTRAINT chat_channels_type_check
    CHECK (((type)::text = ANY ((ARRAY['GENERAL'::character varying, 'COURSE'::character varying, 'TECH_STACK'::character varying])::text[])))
    NOT VALID;

ALTER TABLE public.chat_channels
    VALIDATE CONSTRAINT chat_channels_type_check;

ALTER TABLE public.otps
    DROP CONSTRAINT IF EXISTS otps_purpose_check;

ALTER TABLE public.otps
    ADD CONSTRAINT otps_purpose_check
    CHECK (((purpose)::text = ANY ((ARRAY['EMAIL_VERIFICATION'::character varying, 'PASSWORD_RESET'::character varying])::text[])))
    NOT VALID;

ALTER TABLE public.otps
    VALIDATE CONSTRAINT otps_purpose_check;
