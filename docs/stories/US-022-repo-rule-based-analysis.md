# US-022 Repo Rule-Based Analysis

## Status

implemented

## Lane

normal

## Product Contract

Repository detail should show useful analysis generated from the repository data already available in the public repo response. This phase does not call AI provider APIs and does not depend on `/api/ai/repo/{repoId}/summary` or `/api/ai/repo/{repoId}/advice`.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`

## Acceptance Criteria

- Repo detail fetches repo metadata from `/api/repos/{repoId}` only.
- The analysis includes: Tổng quan, Công nghệ chính, Mức độ phù hợp, Điểm nên xem trước, Chiến lược học tập, and Bước tiếp theo.
- Sparse repos show explicit warnings for missing description, language, tech stacks, course context, and public README/topic data.
- Analysis text is based only on `displayName`, `description`, `primaryLanguage`, `techStacks`, `courseCode`, `courseName`, and `stars`.
- No backend/API contract or provider integration changes are introduced.

## Design Notes

- Rule engine: `devorbit-web/src/lib/repoAiAnalysis.ts`
- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`
- Route page: `devorbit-web/src/pages/student/RepoDetailPage.tsx`
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with route/API call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis` |
| Integration | Not required; API contract unchanged. |
| E2E | Not required for this frontend rule phase. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

Wiki tools were not available in this session, so `.codebase-wiki` ingestion could not be run after the code change.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis` passed, 1 file / 2 tests.
- `devorbit-web`: `npm run build` passed.
