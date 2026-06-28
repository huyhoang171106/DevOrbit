-- Migration: add delete request support to group_plans
-- Members can request plan deletion; only the creator can approve/reject.

ALTER TABLE public.group_plans
    ADD COLUMN IF NOT EXISTS delete_requested BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS delete_requested_by TEXT;
