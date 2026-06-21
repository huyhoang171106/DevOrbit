-- Migration: create student_notifications table
-- Mobile users receive notifications when a new repo is approved for a bookmarked course.

CREATE TABLE IF NOT EXISTS public.student_notifications (
    id BIGSERIAL PRIMARY KEY,
    student_code TEXT NOT NULL REFERENCES public.student_users(student_code) ON DELETE CASCADE,
    title TEXT NOT NULL,
    body TEXT NOT NULL DEFAULT '',
    type TEXT NOT NULL DEFAULT 'NEW_REPO',
    repo_id BIGINT REFERENCES public.github_repos(id) ON DELETE SET NULL,
    course_id BIGINT REFERENCES public.courses(stt) ON DELETE SET NULL,
    tech_stack_name TEXT,
    is_read BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    read_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_student_notifications_student_code
    ON public.student_notifications(student_code);

CREATE INDEX IF NOT EXISTS idx_student_notifications_unread
    ON public.student_notifications(student_code, is_read);
