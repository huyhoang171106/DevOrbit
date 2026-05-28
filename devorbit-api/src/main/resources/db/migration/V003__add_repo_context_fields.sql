alter table if exists github_repos
    add column if not exists readme_excerpt text,
    add column if not exists has_readme boolean,
    add column if not exists file_tree text;

alter table if exists repo_candidates
    add column if not exists has_readme boolean,
    add column if not exists file_tree text;
