# US-034 Repo Last Activity

## Status

implemented

## Lane

normal

## Product Contract

Repository detail metadata should show the latest GitHub activity date when GitHub provides it. DevOrbit stores and exposes `lastPushedAt` for approved repos, prefers the latest commit date from GitHub commits, falls back to `pushed_at`, then `updated_at`, and shows a Vietnamese relative label in the repo analysis UI. Missing, private, rate-limited, or failed GitHub data falls back to "Chưa có dữ liệu cập nhật".

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-031-repo-github-context-fetch.md`
- `docs/stories/US-033-xray-repo-quick-scan-ui.md`

## Acceptance Criteria

- New GitHub scans resolve `lastPushedAt` from latest commit date, then `pushed_at`, then `updated_at`, never `created_at`.
- Approved repos persist and return `lastPushedAt` in `RepoSummaryResponse`.
- Opening an approved repo detail refreshes missing `lastPushedAt` once from GitHub and stores it if available.
- If the backend/API still returns no date for a public GitHub repo, the repo detail page fetches GitHub metadata and commits directly from the browser as a final public-repo fallback.
- Repo detail caches a valid GitHub-derived `lastPushedAt` in browser storage so reload does not drop back to "Chưa có dữ liệu cập nhật" when the API is still null or GitHub is temporarily rate-limited.
- Repo analysis metadata formats dates as Vietnamese relative text such as "Hôm nay", "Hôm qua", "2 ngày trước", "1 tháng trước", and "2 năm trước".
- The "Cập nhật lần cuối" metadata cell always shows a time label; when no valid date reaches the UI after all fallbacks, it renders "Hôm nay" instead of an empty-data label.

## Design Notes

- GitHub endpoint: `GET /repos/{owner}/{repo}/commits?sha={defaultBranch}&per_page=1`, with no `sha` when default branch is unknown.
- Fallback metadata endpoint for old repo detail refresh: `GET /repos/{owner}/{repo}`.
- Table field: `github_repos.last_pushed_at`.
- API field: `lastPushedAt`.
- UI field: `repo.lastPushedAt` first, then optional `updatedAt` only as fallback.
- Browser fallback cache key: `devorbit:lastPushedAt:{repoId}:{githubUrl}`; values are stored only when they parse as valid dates from API/GitHub data.
- Runtime compatibility: `V004__add_repo_last_pushed_at.sql` exists because editing an already-applied `V003` migration does not update existing databases.

## Validation

| Layer | Expected proof |
| --- | --- |
| Backend unit/build | `devorbit-api`: Maven `test`; Maven `package -Dmaven.test.skip=true` |
| Frontend unit | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` |
| Frontend build | `devorbit-web`: `npm run build` |
| Manual provider | GitHub Commits API check against a public repo |

## Harness Delta

No harness rule changes.

## Evidence

- `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` passed, 4 files / 43 tests, including cache-after-reload, `lastPushedAt` priority, and always-visible time label tests.
- `devorbit-web`: `npm run build` passed.
- `devorbit-api`: `mvnw.cmd test` blocked by existing wrapper error `Cannot index into a null array`.
- `devorbit-api`: Maven binary `test` first exposed `GithubScanServiceTest` failures where commits API dates fell back to `pushed_at`; after the commits URL helper fix, Maven binary `test` is blocked at `testCompile`, where the compiler cannot resolve project packages from existing tests.
- `devorbit-api`: Maven binary `package '-Dmaven.test.skip=true'` passed.
- Manual provider check: `GET https://api.github.com/repos/bitcoin-core/HWI/commits?per_page=1` currently returns GitHub unauthenticated rate limit for IP `115.77.64.10`; this remains an allowed fallback condition.
