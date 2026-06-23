ALTER TABLE public.student_notifications
ADD COLUMN IF NOT EXISTS task_id BIGINT;
