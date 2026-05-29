# US-031 Repo GitHub Context Fetch

## Status

implemented

## Lane

high-risk

## Product Contract

GitHub scan and candidate approval should enrich repositories with real GitHub context when available, without blocking the repo workflow if GitHub fails. Repo context includes a README excerpt, a compact newline-separated file tree, and a boolean README flag. Public repo APIs expose these fields so frontend evaluation can use real repo structure instead of only metadata.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-030-contextual-repo-evaluation.md`

## Acceptance Criteria

- Manual GitHub scan fetches README excerpt and compact file tree for new candidates.
- Candidate approval copies context into the approved repo, and fetches context at approval time if the candidate does not have it yet.
- Approved repo API responses include `readmeExcerpt`, `fileTree`, and `hasReadme`.
- GitHub README/tree failures fall back to existing metadata and do not crash scan, approval, or UI.
- File tree is stored as a short newline-separated path list, not raw GitHub JSON.

## Validation

| Layer | Expected proof |
| --- | --- |
| Backend unit/build | `devorbit-api`: Maven `test`; Maven `package -DskipTests` |
| Frontend unit | `devorbit-web`: `npm run test -- repoEvaluation` |
| Frontend build | `devorbit-web`: `npm run build` |

## Evidence

- `devorbit-api`: Maven `test` passed, 16 tests.
- `devorbit-api`: Maven `package -DskipTests` passed.
- `devorbit-web`: `npm run test -- repoEvaluation` passed, 10 tests.
- `devorbit-web`: `npm run build` passed.

## Notes

- `mvnw.cmd` failed in PowerShell with `Cannot index into a null array`; validation used the already downloaded Maven binary under `.m2/wrapper/dists`.
- GitNexus CLI impact calls failed with `Cannot destructure property 'package' of 'node.target' as it is null`; impact was approximated with call-site search.
