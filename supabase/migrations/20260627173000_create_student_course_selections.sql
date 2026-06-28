-- Migration: create student_course_selections table
-- Students can select courses for their current semester.
-- Selection is per-student, with a unique constraint preventing duplicates.

CREATE TABLE IF NOT EXISTS public.student_course_selections (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES public.student_users(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES public.courses(stt) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    UNIQUE(student_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_student_course_selections_student_id
    ON public.student_course_selections(student_id);
