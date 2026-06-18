-- Migration: Add approved_at columns and seed historical data
-- Date: 2026-06-18

-- Add columns
ALTER TABLE public.repo_candidates ADD COLUMN IF NOT EXISTS approved_at timestamp(6) without time zone;
ALTER TABLE public.github_repos ADD COLUMN IF NOT EXISTS approved_at timestamp(6) without time zone;

-- Seed historical approved_at for existing repos using ROW_NUMBER distribution:
--   150 smallest IDs → April 28-29 (alternating)
--   20 next          → May 8
--   rest             → June 3 & June 15 (alternating)
WITH numbered AS (
  SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
  FROM public.github_repos
),
total AS (
  SELECT COUNT(*) AS cnt FROM public.github_repos
),
seeded AS (
  SELECT
    n.id,
    CASE
      WHEN n.rn <= 150 AND n.rn % 2 = 1
        THEN timestamp '2026-04-28 08:00:00' + (n.rn * interval '1 minute')
      WHEN n.rn <= 150 AND n.rn % 2 = 0
        THEN timestamp '2026-04-29 08:00:00' + ((n.rn - 75) * interval '1 minute')
      WHEN n.rn <= 170
        THEN timestamp '2026-05-08 08:00:00' + (n.rn * interval '1 minute')
      WHEN n.rn % 2 = 1
        THEN timestamp '2026-06-03 08:00:00' + (n.rn * interval '1 minute')
      ELSE
        timestamp '2026-06-15 08:00:00' + (n.rn * interval '1 minute')
    END AS approved_at
  FROM numbered n, total t
  WHERE t.cnt > 0
)
UPDATE public.github_repos g
SET approved_at = s.approved_at
FROM seeded s
WHERE g.id = s.id;
