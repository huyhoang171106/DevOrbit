-- Reconstructed from remote supabase_migrations.schema_migrations for local history sync.
-- Add indexes for foreign keys reported by Supabase performance advisors.
CREATE INDEX IF NOT EXISTS idx_community_messages_channel_id
    ON public.community_messages(channel_id);

CREATE INDEX IF NOT EXISTS idx_course_assessments_source_id
    ON public.course_assessments(source_id);

CREATE INDEX IF NOT EXISTS idx_course_objectives_source_id
    ON public.course_objectives(source_id);

CREATE INDEX IF NOT EXISTS idx_course_outcomes_source_id
    ON public.course_outcomes(source_id);

CREATE INDEX IF NOT EXISTS idx_course_references_source_id
    ON public.course_references(source_id);

CREATE INDEX IF NOT EXISTS idx_course_relationships_related_course_id
    ON public.course_relationships(related_course_id);

CREATE INDEX IF NOT EXISTS idx_course_sessions_source_id
    ON public.course_sessions(source_id);

CREATE INDEX IF NOT EXISTS idx_course_syllabus_source_id
    ON public.course_syllabus(source_id);

CREATE INDEX IF NOT EXISTS idx_course_tools_source_id
    ON public.course_tools(source_id);

CREATE INDEX IF NOT EXISTS idx_repo_candidates_course_id
    ON public.repo_candidates(course_id);

CREATE INDEX IF NOT EXISTS idx_repo_tech_stacks_tech_stack_id
    ON public.repo_tech_stacks(tech_stack_id);

CREATE INDEX IF NOT EXISTS idx_tech_stacks_repo_id
    ON public.tech_stacks(repo_id);

-- Photobooth frame writes must go through the authenticated Spring admin API.
-- Public direct DB access only needs read permission for published frames.
DROP POLICY IF EXISTS "Public insert photobooth_frames" ON public.photobooth_frames;
DROP POLICY IF EXISTS "Public update photobooth_frames" ON public.photobooth_frames;
DROP POLICY IF EXISTS "Public delete photobooth_frames" ON public.photobooth_frames;

-- The helper function is SECURITY DEFINER; it should not be executable by
-- anonymous or authenticated API roles.
REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM anon;
REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM authenticated;

