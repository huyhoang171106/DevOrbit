# US-023 Repo Analysis Provider Interface

## Status

implemented

## Lane

normal

## Product Contract

Repository detail should prepare for a future real AI analysis provider through a typed frontend service interface. Until a provider exists, the page must use the existing rule-based analyzer as the default and as fallback when a provider fails.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`

## Acceptance Criteria

- A typed `analyzeRepository(repoData): RepoAnalysisResult` path exists.
- No API key or provider secret is hardcoded.
- The default behavior remains rule-based analysis from public repo metadata.
- If a future provider fails or returns empty sections, the service falls back to rule-based analysis.
- Repo detail has distinct loading, error, and empty states for analysis rendering.
- No backend/API contract changes are introduced.

## Design Notes

- Service: `devorbit-web/src/lib/repoAnalysisService.ts`
- Rule engine: `devorbit-web/src/lib/repoAiAnalysis.ts`
- UI state surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`
- Route page: `devorbit-web/src/pages/student/RepoDetailPage.tsx`
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with route/API call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; API contract unchanged. |
| E2E | Not required for this interface preparation phase. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

Wiki tools were not available in this session, so `.codebase-wiki` ingestion could not be run after the code change.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 4 tests.
- `devorbit-web`: `npm run build` passed.
