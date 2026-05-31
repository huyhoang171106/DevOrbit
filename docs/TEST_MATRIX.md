# Test Matrix

This file maps product behavior to proof.

## Status Values

| Status | Meaning |
| --- | --- |
| planned | Accepted as intended behavior, not implemented |
| in_progress | Actively being built |
| implemented | Implemented and proof exists |
| changed | Contract changed after earlier implementation |
| retired | No longer part of the product contract |

## Matrix

| Story | Contract | Unit | Integration | E2E | Platform | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US-015 | Simulation Engine | pass | pass | no | no | implemented | Plan 1.3 Summary (verified locally) |
| US-016 | Photobooth Upload | - | - | - | Browser | planned | - |
| US-017 | AI Roadmap Generator | - | pass | pass | no | implemented | US-017-PLAN-1.1 (verified locally) |
| US-018 | Mobile Repo Tech Stack Filter | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |
| US-019 | Mobile Course Hub Detail Navigation | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |
| US-020 | GPA Calculator + Semester Presets + Preset Stats/Merge + Cumulative Estimate + Goal Planner + Goal What-If + Row UX + Explicit Browser Draft Controls + Background Atmosphere | pass | no | no | Vite build | implemented | `devorbit-web`: `npm test -- GpaCalculatorPage router`; `npm run build` |
| US-021 | Repo AI Analysis Structure | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis`; `npm run build` |
| US-022 | Repo Rule-Based Analysis | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis`; `npm run build` |
| US-023 | Repo Analysis Provider Interface | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-024 | Repo Analysis UI Groups | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-025 | Repo Analysis Data Driven Content | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-026 | Repo README Insights | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-027 | Repo Analysis Summary Priority | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-028 | Repo Analysis Collapsible Details | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-029 | Repo Analysis Decision Focus | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-030 | Contextual Repo Evaluation | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-031 | Repo GitHub Context Fetch | pass | no | no | Maven + Vite build | implemented | `devorbit-api`: Maven `test`, Maven `package -DskipTests`; `devorbit-web`: `npm run test -- repoEvaluation`, `npm run build` |
| US-032 | X-Ray Repo Evaluation | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-033 | Vietnamese Repository Overview UI | pass | no | no | Vite build | implemented | `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService`; `npm run build` |
| US-034 | Repo Last Activity | partial | no | no | Maven package + Vite build | implemented | `devorbit-api`: Maven `package '-Dmaven.test.skip=true'` passed; Maven `test` blocked at testCompile after commits URL helper fix; `devorbit-web`: `npm run test -- repoEvaluation repoAiAnalysis repoAnalysisService` passed 43 tests, `npm run build` passed; metadata cell always renders a time label; manual GitHub check currently rate-limited |

## Evidence Rules

- Unit proof covers pure domain and application rules.
- Integration proof covers backend enforcement, data integrity, provider
  behavior, jobs, or service contracts.
- E2E proof covers user-visible browser flows.
- Platform proof covers only shell, deployment, mobile, desktop, or runtime
  behavior that cannot be proven in lower layers.
- A story can be implemented without every proof column if the story packet
  explains why.
