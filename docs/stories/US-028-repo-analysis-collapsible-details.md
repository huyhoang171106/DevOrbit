# US-028 Repo Analysis Collapsible Details

## Status

implemented

## Lane

normal

## Product Contract

Repository detail analysis should keep the AI Summary visible while hiding detailed analysis sections behind an explicit show/hide control by default. Students can quickly scan the summary first, then open full README, technology, fit, learning, next steps, and warning details when needed.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`
- `docs/stories/US-023-repo-analysis-provider-interface.md`
- `docs/stories/US-024-repo-analysis-ui-groups.md`
- `docs/stories/US-025-repo-analysis-data-driven.md`
- `docs/stories/US-026-repo-readme-insights.md`
- `docs/stories/US-027-repo-analysis-summary-priority.md`

## Acceptance Criteria

- AI Summary remains visible outside the detailed area.
- Detailed analysis sections are hidden by default.
- A visible `Xem phân tích chi tiết` control expands the detailed sections.
- The same control collapses the detailed sections again.
- README insights, technology, fit details, review-first guidance, learning strategy, next steps, and warnings remain available after expansion.
- No analysis data, provider logic, API call, or route behavior is removed.

## Design Notes

- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`.
- Uses local React state; no new dependency.
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no API contract or backend behavior changed. |
| E2E | Not required for this scoped UI disclosure pass. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 5 tests.
- `devorbit-web`: `npm run build` passed.
