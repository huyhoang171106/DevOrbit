-- Migration: create group plan tables for collaborative task management
-- Mobile users can create group plans, invite members, and manage tasks together.

CREATE TABLE IF NOT EXISTS public.group_plans (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    creator_student_code TEXT NOT NULL,
    deadline DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS public.group_plan_members (
    id BIGSERIAL PRIMARY KEY,
    group_plan_id BIGINT NOT NULL REFERENCES public.group_plans(id) ON DELETE CASCADE,
    student_code TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    invited_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    responded_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(group_plan_id, student_code)
);

CREATE TABLE IF NOT EXISTS public.group_tasks (
    id BIGSERIAL PRIMARY KEY,
    group_plan_id BIGINT NOT NULL REFERENCES public.group_plans(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    assigned_to TEXT,
    deadline DATE,
    completed BOOLEAN NOT NULL DEFAULT false,
    created_by TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    delete_requested BOOLEAN NOT NULL DEFAULT false,
    delete_requested_by TEXT,
    updated_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE public.student_notifications
    ADD COLUMN IF NOT EXISTS group_plan_id BIGINT
    REFERENCES public.group_plans(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_group_plan_members_student_code
    ON public.group_plan_members(student_code);

CREATE INDEX IF NOT EXISTS idx_group_plan_members_plan_id
    ON public.group_plan_members(group_plan_id);

CREATE INDEX IF NOT EXISTS idx_group_tasks_plan_id
    ON public.group_tasks(group_plan_id);

CREATE INDEX IF NOT EXISTS idx_group_tasks_assigned_to
    ON public.group_tasks(assigned_to);
