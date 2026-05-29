# US-027 Repo Analysis Summary Priority

## Status

implemented

## Lane

normal

## Product Contract

Repository detail analysis should lead with a concise AI Summary so students can quickly understand what the repo is for, whether it is suitable for study/deadline use, what to do first, and whether there is a key warning. Existing detailed analysis and README insights remain available below.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`
- `docs/stories/US-023-repo-analysis-provider-interface.md`
- `docs/stories/US-024-repo-analysis-ui-groups.md`
- `docs/stories/US-025-repo-analysis-data-driven.md`
- `docs/stories/US-026-repo-readme-insights.md`

## Acceptance Criteria

- The analysis area starts with a focused AI Summary card.
- Summary surfaces the repository purpose or a safe sparse-data fallback.
- Summary shows no more than three status badges: learning fit, data completeness, and key warning state.
- Summary shows the first practical action and at most two additional immediate actions.
- The detailed `nextSteps` card is capped to three bullets.
- README insights, technology, fit details, learning strategy, and warning details remain available.
- No API call, route, provider, or analysis generation logic is removed.

## Design Notes

- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`.
- Existing rule-based analysis sections are reused as the source of truth for summary content.
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no API contract or backend behavior changed. |
| E2E | Not required for this scoped layout-priority pass. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 5 tests.
- `devorbit-web`: `npm run build` passed.
