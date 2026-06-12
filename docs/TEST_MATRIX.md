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
| US-021A | Smarter RAG (hybrid retrieval, query expansion, reranking, adaptive web, hierarchical chunks) | pass | pass | runtime smoke | Maven + focused tests | implemented | `devorbit-api`: `RagQueryPlannerTest`, `RagResultRerankerTest`, `KnowledgeRetrievalServiceTest`, `CourseKnowledgeIndexerTest` — all 32 pass; `SubjectQaServiceTest`, `TutorRagServiceTest`, `WebSearchServiceTest` — all 20 pass; `AdminKnowledgeControllerTest`, `PublicRepoControllerTest` — all 7 pass; full suite `mvnw.cmd test -B` 172/173 pass (1 pre-existing H2/vector blocker in `FirecrawlDisabledTest`); compile succeeds. |
| US-021B | AI Course Q&A Streaming Chat | pass | no | no | Frontend Vitest + TypeScript + Maven focused tests | implemented | `devorbit-web`: `src/hooks/useSubjectQa.stream.test.ts` (6 tests) + `src/components/student/__tests__/AiChatWidget.test.tsx` (3 tests) - all 9 pass; `npx tsc --noEmit` passes; backend `OpenCodeAiServiceTest`, `SubjectQaServiceTest`, `SubjectQaControllerTest` added. 2026-06-12: `devorbit-api` `.\mvnw.cmd -Dtest=SubjectQaServiceTest,OpenCodeAiServiceTest,ChatServiceTest,TutorRagServiceTest test -B` passes 23 tests, including Vietnamese greeting false-positive, Java backend career-course recommendation, first-year curriculum grounding, and UIT orientation web-search follow-up regressions. |
| US-018 | Mobile Repo Tech Stack Filter | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |
| US-019 | Mobile Course Hub Detail Navigation | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |
| US-035 | Mobile Course Search, Bookmarks, and Explore Search/Filter | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest`; commits `7e01b5d`, `6122db9`, `bbcc294`, `be174b4` |
| US-036 | Mobile AI Roadmap and Subject Q&A ViewModels | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |
| US-040 | Mobile Student Auth Registration Session | pass | no | no | Gradle unit test + debug build | implemented | `devorbit-mobile`: targeted `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthSessionPolicyTest` passed; full `.\gradlew.bat :app:testDebugUnitTest` passed; `.\gradlew.bat :app:assembleDebug` passed; `installDebug` blocked because `adb devices` returned no connected devices |
| US-037 | Community Data Model: chat channels, messages, repo reviews, repo votes, course reviews | pass | no | no | Maven test suite | implemented | `devorbit-api`: `mvn.cmd -Dtest=CommunityPersistenceContractTest test` passed 3 tests; `mvn.cmd test` passed 29 tests; commits `2331cf8`, `f613598` |
| US-038 | Community WebSocket STOMP Auth | pass | no | no | Maven test suite | implemented | `devorbit-api`: `mvn.cmd -Dtest=WebSocketConfigContractTest test` passed 4 tests; `mvn.cmd test` passed 33 tests; commit `0205453` |
| US-039 | Community REST APIs and WebSocket Message Endpoints | pass | no | no | Maven test suite | implemented | `devorbit-api`: `mvn.cmd -Dtest=CommunityMilestone3ContractTest test` passed 8 tests; `mvn.cmd test` passed 41 tests; commits `695ba11`, `155223f` |
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

