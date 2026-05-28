# US-029 Repo Analysis Decision Focus

## Status

implemented

## Lane

normal

## Product Contract

Repository detail analysis should help students decide quickly whether a repo is worth opening, useful for study/deadline work, or only suitable as reference. The UI should avoid repeating long report-style sections while preserving the underlying data-driven analysis and README insight logic.

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
- `docs/stories/US-028-repo-analysis-collapsible-details.md`

## Acceptance Criteria

- The leading analysis block is named `Đánh giá nhanh`.
- The quick review has one conclusion, one decision label, and a short reason.
- Decision labels are one of: `Nên xem`, `Xem có chọn lọc`, `Chỉ tham khảo`, `Không đủ dữ liệu`.
- The visible analysis includes at most two strengths and two weaknesses.
- The visible next actions are exactly three practical steps.
- Detailed analysis is grouped into only three buckets: `Repo nói về gì?`, `Có áp dụng được không?`, and `Cần kiểm tra trước khi tin`.
- Existing rule-based/data-driven analysis, README insights, service flow, and API fetching remain intact.

## Design Notes

- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`.
- Existing analysis sections are still generated and reused, but the renderer now compacts them into decision-oriented groups.
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no API contract or backend behavior changed. |
| E2E | Not required for this scoped rendering pass. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 5 tests.
- `devorbit-web`: `npm run build` passed.
