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
| DB-001 | Supabase schema and migration synchronization | pass | pass | no | Supabase CLI + PostgreSQL catalog + Maven + Vitest + run.bat startup | implemented | 2026-06-17 audit reconciled local Supabase migrations with the linked database without reset/truncate/reseed. `supabase migration list --db-url` aligned local and remote history from `20260501000000` through `20260616192252`; `supabase db diff --db-url --schema public` returned `No schema changes found`; `supabase db pull ... --schema public` performed shadow reconstruction and reported no schema changes to write. Catalog checks after migration found 0 public RLS tables without policies, 0 missing FK indexes, 0 duplicate public indexes, 0 FK orphans, `extensions.vector` 0.8.0, and `knowledge_chunks.embedding` as `extensions.vector(4096)`. Backend `mvnw.cmd test -B` passed 201/201; frontend `npx tsc --noEmit` passed; frontend `npm test -- --run --maxWorkers=1` passed 166/166; `devorbit-api/run.bat` reached Supabase hardening ready, Knowledge RAG schema ready, and Spring Boot started. |
| US-043 | Course List Navigation State | pass | no | page-level jsdom | TypeScript + Vite build | implemented | `devorbit-web`: browser Back and `Danh mục` preserve the originating page in 23/23 targeted tests; full Vitest suite 162/162 with `--maxWorkers=1`, TypeScript, and build passed on 2026-06-14. |
| US-042 | Course Library Search Relevance | pass | no | page-level jsdom | TypeScript + Vite build | implemented | `devorbit-web`: exact ranking without result locking, phrase, OR keyword, word-boundary, and typing-prefix regressions included in 23/23 targeted tests; full Vitest suite 162/162 with `--maxWorkers=1`, `npx tsc --noEmit`, and `npm run build` passed on 2026-06-14. |
| US-041 | Course Library Pagination and Repository Filter Visibility | pass | no | page-level jsdom | TypeScript + Vite build | implemented | `devorbit-web`: targeted course list/detail tests 2/2, full Vitest suite 144/144, `npx tsc --noEmit`, and `npm run build` passed on 2026-06-13. |
| US-015 | Simulation Engine | pass | pass | no | no | implemented | Plan 1.3 Summary (verified locally) |
| US-016 | Photobooth Upload | - | - | - | Browser | planned | - |
| US-017 | AI Roadmap Generator | - | pass | pass | no | implemented | US-017-PLAN-1.1 (verified locally) |
| US-021A | Smarter RAG (hybrid retrieval, query expansion, reranking, adaptive web, hierarchical chunks) | pass | pass | runtime smoke | Maven + focused tests + Supabase run.bat smoke | implemented | `devorbit-api`: `RagQueryPlannerTest`, `RagResultRerankerTest`, `KnowledgeRetrievalServiceTest`, `CourseKnowledgeIndexerTest`, `KnowledgeSchemaInitializerTest`, `SupabaseDatabaseHardeningInitializerTest`; full suite `.\mvnw.cmd test -B` passed 185/185 on 2026-06-13. `devorbit-api\run.bat` connected to Supabase Postgres 17.6 via pooler; startup logged `Supabase database hardening is ready` and `Knowledge RAG schema is ready` without `ALTER COLUMN embedding TYPE ... USING NULL`; `POST /api/ai/subject-qa/query` returned 200 for SE104. Supabase migrations `harden_database_indexes_and_rls`, `harden_remaining_supabase_advisors`, `declare_backend_owned_table_rls`, and `move_vector_extension_to_extensions_schema` added missing FK indexes, removed public direct write/listing policies, removed duplicate bookmark uniqueness, restricted `tutor_registrations` INSERT, revoked API-role execute on `rls_auto_enable`, moved pgvector to `extensions`, and made backend-owned tables explicitly deny direct Data API access; catalog checks returned 0 RLS tables without policy, 0 duplicate bookmark constraints, and security advisors `lints: []`. Full-flow smoke passed after these migrations: public courses/discovery/photobooth reads, student register -> OTP verify -> login -> `/me` -> bookmark create/list/delete, admin login -> stats -> photobooth create/read/delete, and AI subject Q&A; DB cleanup confirmed 0 residual `codex_flow_*` rows. 2026-06-14 regression check: `cmd /c ".\mvnw.cmd -Dtest=KnowledgeSchemaInitializerTest,SupabaseDatabaseHardeningInitializerTest test"` passed 6/6 after preserving existing `vector(4096)` embeddings during startup schema checks. |
| US-021B | AI Course Q&A Streaming Chat | pass | no | no | Frontend Vitest + TypeScript + Maven focused tests | implemented | `devorbit-web`: `src/hooks/useSubjectQa.stream.test.ts` + `src/components/student/__tests__/AiChatWidget.test.tsx` now cover search results, SSE completion, and roadmap payload rendering; `npx tsc --noEmit` passes; `npm test -- --run` 142/142 pass. `devorbit-api`: `SubjectQaServiceTest` (13 tests) passes, including the new ROADMAP response path and direct roadmap-intent branch into `RoadmapRecommendationResponse`. |
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

