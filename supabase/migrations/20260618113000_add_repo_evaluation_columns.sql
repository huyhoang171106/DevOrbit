ALTER TABLE "public"."github_repos" ADD COLUMN IF NOT EXISTS "repo_type" character varying(50);
ALTER TABLE "public"."github_repos" ADD COLUMN IF NOT EXISTS "usefulness_rating" character varying(50);
ALTER TABLE "public"."github_repos" ADD COLUMN IF NOT EXISTS "usefulness_score" integer;
ALTER TABLE "public"."github_repos" ADD COLUMN IF NOT EXISTS "ready_to_use_level" character varying(50);
