# US-030 Contextual Repo Evaluation

## Status

implemented

## Lane

normal

## Product Contract

Repository detail evaluation should produce a short, decision-focused summary that changes by repository signals instead of repeating a generic template. The first visible block must tell students the repo type, best use, usefulness rating, main reason, and what to check before using it. Detailed reasoning stays behind an expand/collapse section.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/US-021-repo-ai-analysis-structure.md`
- `docs/stories/US-022-repo-rule-based-analysis.md`
- `docs/stories/US-023-repo-analysis-provider-interface.md`
- `docs/stories/US-024-repo-analysis-ui-groups.md`
- `docs/stories/US-025-repo-analysis-data-driven.md`
- `docs/stories/US-028-repo-analysis-collapsible-details.md`
- `docs/stories/US-029-repo-analysis-decision-focused.md`

## Acceptance Criteria

- Repo evaluation logic is extracted from UI into `devorbit-web/src/lib/repoEvaluation.ts`.
- The result includes typed repo type, usefulness rating, confidence, quick summary, strengths, weaknesses, next actions, detailed sections, and evidence.
- Classification handles programming exercises, project practice, study material, exam review, mixed resources, and unknown repositories.
- README is weighted by repo purpose, not used as a universal penalty.
- Current public repo data is not overclaimed: approved repos currently expose display name, description, GitHub URL, primary language, stars, tech stacks, and course fields; optional topics/forks/readme/file-list fields are used only when present.
- The default UI prioritizes quick capture; detailed reasoning is collapsed by default.
- Sparse repos fall back to insufficient-data wording without fake fields.

## Design Notes

- UI surface: `devorbit-web/src/components/student/RepoAiAnalysisSection.tsx`.
- Service surface: `devorbit-web/src/lib/repoAnalysisService.ts`.
- Domain helper: `devorbit-web/src/lib/repoEvaluation.ts`.
- Existing AI/rule-based legacy sections remain available on `RepoAnalysisResult.sections`; the new UI renders the richer `evaluation` object.
- GitNexus MCP resources were unavailable and `npx gitnexus --help` timed out, so impact analysis was approximated with call-site search before edits.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` |
| Integration | Not required; no backend/API contract change. |
| E2E | Not required for this scoped rendering and helper refactor. |
| Platform | `devorbit-web`: `npm run build` |
| Release | Not required. |

## Harness Delta

No structural harness change was required.

## Evidence

- `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` passed, 3 files / 12 tests.
- `devorbit-web`: `npm run build` passed.
