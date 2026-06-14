package vn.edu.uit.devorbit_api.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Applies idempotent Supabase/PostgreSQL hardening that the backend depends on
 * while Flyway is not yet wired into this project.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SupabaseDatabaseHardeningInitializer {

    private final JdbcTemplate jdbcTemplate;

    @Value("${devorbit.database.hardening.enabled:true}")
    private boolean enabled = true;

    @PostConstruct
    public void initialize() {
        if (!enabled || !isPostgres()) {
            return;
        }

        try {
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS extensions");
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA extensions");
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector') THEN
                        ALTER EXTENSION vector SET SCHEMA extensions;
                    END IF;
                END $$
                """);
            createMissingForeignKeyIndexes();
            dropDuplicateBookmarkConstraint();
            hardenPhotoboothAndStoragePolicies();
            hardenTutorRegistrationPolicies();
            revokePublicSecurityDefinerFunction();
            declareBackendOwnedTables();
            log.info("Supabase database hardening is ready");
        } catch (Exception e) {
            log.warn("Supabase database hardening skipped/failed: {}", e.getMessage());
        }
    }

    boolean isPostgres() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT current_setting('server_version')", String.class);
            return version != null && !version.isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    private void createMissingForeignKeyIndexes() {
        String[][] indexes = {
                {"idx_community_messages_channel_id", "community_messages", "channel_id"},
                {"idx_course_assessments_source_id", "course_assessments", "source_id"},
                {"idx_course_objectives_source_id", "course_objectives", "source_id"},
                {"idx_course_outcomes_source_id", "course_outcomes", "source_id"},
                {"idx_course_references_source_id", "course_references", "source_id"},
                {"idx_course_relationships_related_course_id", "course_relationships", "related_course_id"},
                {"idx_course_sessions_source_id", "course_sessions", "source_id"},
                {"idx_course_syllabus_source_id", "course_syllabus", "source_id"},
                {"idx_course_tools_source_id", "course_tools", "source_id"},
                {"idx_repo_candidates_course_id", "repo_candidates", "course_id"},
                {"idx_repo_tech_stacks_tech_stack_id", "repo_tech_stacks", "tech_stack_id"},
                {"idx_tech_stacks_repo_id", "tech_stacks", "repo_id"}
        };

        for (String[] index : indexes) {
            executeIfTableExists(index[1], "CREATE INDEX IF NOT EXISTS " + index[0]
                    + " ON public." + index[1] + "(" + index[2] + ")");
        }
    }

    private void dropDuplicateBookmarkConstraint() {
        executeIfTableExists("student_bookmarks", """
            ALTER TABLE public.student_bookmarks
                DROP CONSTRAINT IF EXISTS uk9pk7ct27f5eseu5adcp0322wv
            """);
    }

    private void hardenPhotoboothAndStoragePolicies() {
        executeIfTableExists("photobooth_frames", """
            DROP POLICY IF EXISTS "Public insert photobooth_frames" ON public.photobooth_frames
            """);
        executeIfTableExists("photobooth_frames", """
            DROP POLICY IF EXISTS "Public update photobooth_frames" ON public.photobooth_frames
            """);
        executeIfTableExists("photobooth_frames", """
            DROP POLICY IF EXISTS "Public delete photobooth_frames" ON public.photobooth_frames
            """);
        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF to_regclass('storage.objects') IS NOT NULL THEN
                    DROP POLICY IF EXISTS "Public read access" ON storage.objects;
                END IF;
            END $$
            """);
    }

    private void hardenTutorRegistrationPolicies() {
        executeIfTableExists("tutor_registrations", """
            DROP POLICY IF EXISTS "Admin All Access" ON public.tutor_registrations
            """);
        executeIfTableExists("tutor_registrations", """
            DROP POLICY IF EXISTS "Public Insert Access" ON public.tutor_registrations
            """);
        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF to_regclass('public.tutor_registrations') IS NOT NULL
                   AND NOT EXISTS (
                       SELECT 1
                       FROM pg_policies
                       WHERE schemaname = 'public'
                         AND tablename = 'tutor_registrations'
                         AND policyname = 'Public insert tutor registrations limited'
                   ) THEN
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
                        AND email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$'
                        AND length(trim(phone)) BETWEEN 7 AND 32
                        AND coalesce(status, 'pending') = 'pending'
                    );
                END IF;
            END $$
            """);
    }

    private void revokePublicSecurityDefinerFunction() {
        jdbcTemplate.execute("""
            DO $$
            BEGIN
                IF to_regprocedure('public.rls_auto_enable()') IS NOT NULL THEN
                    REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM PUBLIC;
                    REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM anon;
                    REVOKE EXECUTE ON FUNCTION public.rls_auto_enable() FROM authenticated;
                END IF;
            END $$
            """);
    }

    private void declareBackendOwnedTables() {
        jdbcTemplate.execute("""
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
                    IF to_regclass('public.' || table_name) IS NOT NULL
                       AND NOT EXISTS (
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
            END $$
            """);
    }

    private void executeIfTableExists(String tableName, String sql) {
        if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT to_regclass(?) IS NOT NULL",
                Boolean.class,
                "public." + tableName))) {
            jdbcTemplate.execute(sql);
        }
    }
}
