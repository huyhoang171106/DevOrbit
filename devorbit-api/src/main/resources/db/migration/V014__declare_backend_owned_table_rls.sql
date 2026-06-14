-- Most DevOrbit tables are owned by the Spring Boot API, not Supabase Auth.
-- Make the direct Data API posture explicit: anon/authenticated cannot access
-- these tables directly. The backend JDBC connection is unaffected.
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
        IF NOT EXISTS (
            SELECT 1
            FROM pg_policies
            WHERE schemaname = 'public'
              AND tablename = table_name
              AND policyname = 'Deny direct API access'
        ) THEN
            EXECUTE format(
                'CREATE POLICY "Deny direct API access" ON public.%I FOR ALL TO anon, authenticated USING (false) WITH CHECK (false)',
                table_name
            );
        END IF;
    END LOOP;
END $$;
