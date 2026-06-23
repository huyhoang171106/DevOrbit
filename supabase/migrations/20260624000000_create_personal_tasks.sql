CREATE TABLE IF NOT EXISTS public.personal_tasks (
    id BIGSERIAL PRIMARY KEY,
    student_code TEXT NOT NULL REFERENCES public.student_users(student_code) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    deadline TIMESTAMP WITH TIME ZONE,
    completed BOOLEAN NOT NULL DEFAULT false,
    recurrence TEXT,
    recurrence_days_of_week INT,
    recurrence_start_date DATE,
    recurrence_end_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_personal_tasks_student_code
    ON public.personal_tasks(student_code);

CREATE INDEX IF NOT EXISTS idx_personal_tasks_deadline
    ON public.personal_tasks(student_code, deadline);
