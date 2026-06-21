-- Add AI evaluation columns to repo_candidates for auto-review pipeline
ALTER TABLE repo_candidates
ADD COLUMN IF NOT EXISTS ai_score INTEGER CHECK (ai_score >= 0 AND ai_score <= 100),
ADD COLUMN IF NOT EXISTS ai_recommendation VARCHAR(20) CHECK (ai_recommendation IN ('APPROVE', 'REVIEW', 'REJECT'));
