# US-032 X-Ray Repo Evaluation

## Status

implemented

## Lane

normal

## Product Contract

Repository evaluation should generate a compact "X-quang repo" result from real repo metadata, README excerpt, and file tree. The evaluation separates repo identity, tech/tools, core learning topics, ready-to-use level, course group context, group-specific highlights, recommendation, usefulness, and confidence.

## Acceptance Criteria

- Evaluation result includes repo identity, tech tools, core topics, ready-to-use level, course group, group highlights, recommendation, score, confidence, and up to 3 quick bullets.
- Course group is inferred before summary generation and adjusts the evaluation lens.
- Tech/tools are not confused with learning topics.
- General-skill repos such as SS004 do not treat unrelated language metadata as a main strength without source-code context.
- IT003/Data Structures & Algorithms repos with C++ metadata but missing file tree remain programming exercises/foundation algorithm context, not project app setup.
- Sparse repos fall back to "not enough data" without invented insights.
- Existing result fields remain available so current UI does not break before the UI simplification story.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` |
| Platform | `devorbit-web`: `npm run build` |

## Evidence

- `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` passed, 3 files / 23 tests.
- `devorbit-web`: `npm run build` passed.

## Notes

- GitNexus CLI impact calls still failed with `Cannot destructure property 'package' of 'node.target' as it is null`; impact was approximated with call-site search.
- UI remains intentionally unchanged for this story; rendering simplification belongs to the next part.
