# US-021 Repo AI Analysis Structure

## Status

implemented

## Lane

normal

## Product Contract

Repository detail should present AI analysis through a focused UI surface and keep analysis preparation separate from the route page. This phase keeps the existing backend summary/advice data and does not introduce real AI provider calls.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- Repo detail still loads repo metadata from `/api/repos/{repoId}`.
- Existing summary and advice responses still render when available.
- AI analysis rendering is isolated from `RepoDetailPage`.
- AI response text preparation is isolated in a reusable frontend helper.
- No routing, API contract, or backend behavior changes are introduced in this phase.

## Design Notes

- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`
- Helper: `devorbit-web/src/lib/repoAiAnalysis.ts`
- Route page: `devorbit-web/src/pages/student/RepoDetailPage.tsx`
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with route/API call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm test -- repoAiAnalysis` |
| Integration | Not required; API contract unchanged. |
| E2E | Not required for this extraction phase. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

Wiki tools were not available in this session, so `.codebase-wiki` ingestion could not be run after the code change.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis` passed, 1 file / 2 tests.
- `devorbit-web`: `npm run build` passed.
