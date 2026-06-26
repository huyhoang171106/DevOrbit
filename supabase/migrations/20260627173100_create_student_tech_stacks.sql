-- Migration: create student_tech_stacks table
-- Students can select tech stacks they are interested in.
-- Notifications are sent when a new repo with that tech stack is available.

CREATE TABLE IF NOT EXISTS public.student_tech_stacks (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES public.student_users(id) ON DELETE CASCADE,
    tech_stack_id BIGINT NOT NULL REFERENCES public.tech_stacks(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    UNIQUE(student_id, tech_stack_id)
);

CREATE INDEX IF NOT EXISTS idx_student_tech_stacks_student_id
    ON public.student_tech_stacks(student_id);

CREATE INDEX IF NOT EXISTS idx_student_tech_stacks_tech_stack_id
    ON public.student_tech_stacks(tech_stack_id);
