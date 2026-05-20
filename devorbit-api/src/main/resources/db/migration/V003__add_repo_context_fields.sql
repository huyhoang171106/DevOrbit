alter table if exists github_repos
    add column if not exists readme_excerpt text,
    add column if not exists has_readme boolean,
    add column if not exists file_tree text,
    add column if not exists last_pushed_at varchar(255);

alter table if exists repo_candidates
    add column if not exists last_pushed_at varchar(255),
    add column if not exists has_readme boolean,
    add column if not exists file_tree text;
