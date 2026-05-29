# US-025 Repo Analysis Data Driven Content

## Status

implemented

## Lane

normal

## Product Contract

Repository detail analysis should vary more clearly by repository data while staying rule-based and safe. It should use the public repo metadata DevOrbit currently has, avoid inventing missing facts, and show clear fallbacks when README/topics/forks/updatedAt are unavailable.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`
- `docs/stories/US-023-repo-analysis-provider-interface.md`
- `docs/stories/US-024-repo-analysis-ui-groups.md`

## Acceptance Criteria

- Description, language, tech stacks, course metadata, and stars influence the generated analysis text.
- Optional future fields such as topics, README excerpt, forks, updatedAt/lastPushedAt, and deadline are used when present.
- Analysis reports information completeness as `ít`, `trung bình`, or `đầy đủ`.
- Analysis reports learning fit as `thấp`, `vừa`, or `cao`.
- Missing README, description, technology, topics, forks, updated date, or course context produce explicit warnings.
- Sparse repos do not crash and do not receive invented README/setup/domain details.
- No new frontend AI provider call, API call, route change, dependency, or UI redesign is introduced.

## Design Notes

- Rule engine: `devorbit-web/src/lib/repoAiAnalysis.ts`.
- Service interface and UI grouping stay unchanged.
- GitNexus MCP tools were not available in this session, so impact analysis was approximated with route/API call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no API contract or backend behavior changed. |
| E2E | Not required for this scoped rule-engine pass. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService` passed, 2 files / 5 tests.
- `devorbit-web`: `npm run build` passed.
