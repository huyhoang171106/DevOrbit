# US-033 Vietnamese Repository Overview UI

## Status

implemented

## Lane

normal

## Product Contract

Repository detail should show a compact Vietnamese "Phân tích repo" overview first. The default view focuses on repo format, technology/tools, main topics, readiness, safety, score, README summary, suggested use, checks before use, and an optional short file tree preview.

## Acceptance Criteria

- Main evaluation surface prioritizes Tổng quan nhanh, Công nghệ / công cụ, Chủ đề chính, Mức độ sẵn sàng, Độ an toàn, Điểm đáng xem, README nói gì?, Nên dùng để, and Cần kiểm tra.
- When README context exists, the UI shows a short "README nói gì?" scan with 2-3 bullets and no raw README copy.
- When README context is absent, the README section stays very short and tells the user to inspect source manually.
- Ready-to-use is shown as a dedicated status card with stars and a type-specific reason.
- Usefulness rating is less strict: red is reserved for severely sparse/unclear repos, while suitable course-context repos can reach green.
- Quick bullets are limited to the existing maximum of 3.
- Rating uses only green, amber, and red tones.
- Các nhãn kỹ thuật nội bộ được bỏ khỏi phần tổng quan cho người dùng.
- Technical details, strengths/weaknesses, and next actions are hidden behind collapsed details by default.
- File tree preview is collapsed/short by default and does not dump the full tree into the main scan.
- Existing repo detail layout and data flow remain unchanged.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` |
| Platform | `devorbit-web`: `npm run build` |

## Evidence

- `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` passed, 3 files / 23 tests.
- `devorbit-web`: `npm run build` passed.

## Notes

- GitNexus CLI impact calls failed with `Cannot destructure property 'package' of 'node.target' as it is null`; impact was approximated with call-site search. `RepoAiAnalysisSection` is rendered from `RepoDetailPage`.
