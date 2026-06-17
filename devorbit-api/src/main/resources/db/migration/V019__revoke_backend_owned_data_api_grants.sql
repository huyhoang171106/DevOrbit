-- Tighten the Supabase Data API posture for tables owned by the Spring backend.
-- Existing deny-all RLS policies already prevent row access for anon/authenticated;
-- this migration also removes table reachability for those roles.
--
-- Rollback:
--   GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER
--   ON selected backend-owned tables TO anon, authenticated;

DO $$
DECLARE
    table_name text;
BEGIN
    FOREACH table_name IN ARRAY ARRAY[
        'admin_users',
        'chat_channels',
        'chat_messages',
        'chat_sessions',
        'community_messages',
        'course_articles',
        'course_assessments',
        'course_objectives',
        'course_outcomes',
        'course_references',
        'course_relationships',
        'course_reviews',
        'course_sessions',
        'course_syllabus',
        'course_tools',
        'course_tutorials',
        'course_youtube_playlists',
        'courses',
        'github_repos',
        'knowledge_chunks',
        'knowledge_sources',
        'learning_roadmaps',
        'note_code_snippets',
        'notes',
        'notifications',
        'otps',
        'repo_candidates',
        'repo_reviews',
        'repo_tech_stacks',
        'repo_votes',
        'roadmap_items',
        'roadmap_phases',
        'student_bookmarks',
        'student_users',
        'tech_stacks'
    ]
    LOOP
        IF to_regclass('public.' || table_name) IS NOT NULL THEN
            EXECUTE format(
                'REVOKE ALL PRIVILEGES ON TABLE public.%I FROM anon, authenticated',
                table_name
            );
        END IF;
    END LOOP;
END $$;
