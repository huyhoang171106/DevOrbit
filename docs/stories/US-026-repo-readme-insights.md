# US-026 Repo README Insights

## Status

implemented

## Lane

normal

## Product Contract

Repository detail analysis should include a small README insights section when README-like content is available. The section should derive only from existing repo data, detect setup/run commands and reading targets when present, and show an explicit fallback when README data is missing.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`
- `docs/stories/US-023-repo-analysis-provider-interface.md`
- `docs/stories/US-024-repo-analysis-ui-groups.md`
- `docs/stories/US-025-repo-analysis-data-driven.md`

## Acceptance Criteria

- Analysis includes a `README insights` card in the existing analysis area.
- README-like fields are accepted when present: `readmeExcerpt`, `readmeContent`, `readmeMarkdown`, `readmeText`, or `readme`.
- If README exists, the analyzer extracts a short goal signal, important keywords, setup/run commands, and reading targets when they appear.
- If README is missing, the analyzer says `Chưa có README để phân tích sâu.` and points users back to source, description, or topics.
- No new API call, AI provider, API key, dependency, route change, or page redesign is introduced.

## Design Notes

- Rule engine: `devorbit-web/src/lib/repoAiAnalysis.ts`.
- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`.
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no API contract or backend behavior changed. |
| E2E | Not required for this scoped analysis-card pass. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 5 tests.
- `devorbit-web`: `npm run build` passed.
