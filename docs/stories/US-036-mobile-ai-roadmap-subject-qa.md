# US-036 Mobile AI Roadmap and Subject Q&A ViewModels

## Status

implemented

## Lane

normal

## Product Contract

Mobile exposes typed API/repository/ViewModel support for AI roadmap generation and Subject Q&A. The Plan tab can generate an AI roadmap from learning goals and career path, and Subject Q&A has a reusable ViewModel plus basic chat screen.

## Acceptance Criteria

- [x] `ApiService` includes typed `GET /api/repos/{repoId}`.
- [x] `ApiService` DTOs match backend Subject Q&A response fields.
- [x] `ApiService` DTOs match backend AI roadmap recommendation fields.
- [x] `StudyPlanRepository` maps backend roadmap recommendations to mobile `StudyPlan`.
- [x] `StudyPlanViewModel` generates roadmap and toggles item completion in memory.
- [x] Plan tab is connected to `StudyPlanViewModel`.
- [x] `SubjectQaRepository` and `SubjectQaViewModel` manage chat session state.
- [x] Basic `SubjectQaScreen` exists for chat UI reuse.
- [ ] Study plan persistence is not included in this slice.
- [ ] Subject Q&A navigation from Course Detail is not included in this slice.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `StudyPlanMappersTest`, `SubjectQaStateTest`, existing mobile tests. |
| Integration | Not run; live backend/API key dependent. |
| E2E | Not available in mobile harness. |
| Platform | `:app:testDebugUnitTest` through Gradle. |

## Evidence

- `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` passed.

## Harness Delta

CodeGraph and wiki tools were not exposed in this session. Impact analysis was approximated with local API/controller/DTO inspection and Gradle compile/test proof.
