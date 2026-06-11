# User Story: AI Course Q&A Assistant

As a student,
I want to ask the AI assistant questions about courses, syllabi, grading criteria, and study experiences,
So that I can better plan my learning journey and study effectively.

## Acceptance Criteria

- [x] Database is enriched with syllabus fields (`learning_objectives`, `grading_criteria`, `topics`) and chat history tables (`chat_sessions`, `chat_messages`).
- [x] Syllabus seed data parsed and populated into database (from UIT course summary document).
- [x] Backend service orchestrates query analysis, parses course codes, discovers web context with Exa, scrapes detail pages with Firecrawl when needed, and generates answers using OpenCode Go DeepSeek-v4-flash model.
- [x] Frontend features a premium Glassmorphic floating AI chat widget with suggestions and citations.
- [x] Chat messages parse course codes (like `SE101`) to render clickable CourseBadges.
- [x] Generic greetings and broad resource requests do not invent DevOrbit features; the assistant asks for a specific course code when it lacks grounded course/repo context.
- [x] LLM chat path uses OpenCode responses with DevOrbit DB context, Firecrawl web scraping, and Fireworks-backed semantic retrieval without fabricating DevOrbit-only features.
- [x] Course-specific chat lazily indexes trusted DevOrbit database course/repo context into Knowledge RAG chunks and embeds them before semantic retrieval.
- [x] Hybrid local retrieval: RagQueryPlanner expands Vietnamese intent terms, multi-query hybrid search (pgvector + PostgreSQL FTS), RagResultReranker applies dedup/lexical boost/source diversity.
- [x] Adaptive web fallback: web search only triggered when local RAG chunks are empty/weak for detected courses.
- [x] Hierarchical chunking: SECTION_SUMMARY + DETAIL chunks with 500-char overlap and parent_chunk_id references.
- [x] Hybrid search gracefully falls back to vector-only search when PostgreSQL search_text is unavailable.
- [x] Real SSE streaming: `POST /api/ai/subject-qa/stream` emits status, search_result, delta, complete, and error events; AI chat widget renders progress rows, search result cards as they arrive, and answer tokens live without fake typewriter.

## Technical Notes

- **Backend**:
  - Flyway migrations `V003` and `V004` to create and populate fields.
  - `SubjectQaService.java` manages searching course syllabus fields, performing Exa discovery, scraping detail pages with Firecrawl fallback, and querying OpenCode Go API.
  - `SubjectQaController.java` exposes REST endpoint `/api/ai/subject-qa/query`.
  - `/api/ai/subject-qa/stream` SSE endpoint added for live streaming; `/query` one-shot endpoint preserved.

## Validation Proof

- **Automated Tests**:
  - Smarter RAG unit tests: `RagQueryPlannerTest` (9 tests), `RagResultRerankerTest` (8 tests), `CourseKnowledgeIndexerTest` (8 tests), `KnowledgeRetrievalServiceTest` (7 tests) — all pass.
  - Orchestration tests: `SubjectQaServiceTest` (7 tests), `TutorRagServiceTest` (8 tests), `WebSearchServiceTest` (5 tests) — all pass.
  - Existing contract tests: `AdminKnowledgeControllerTest` (4 tests), `PublicRepoControllerTest` (3 tests) — all pass.
  - Full suite: `mvnw.cmd test -B` — 172/173 tests pass (1 pre-existing H2/vector blocker in `FirecrawlDisabledTest`).
  - Compile: `mvnw.cmd compile -B` succeeds.
- **Manual**:
  - Postman/smoke API calls confirm hybrid retrieval fallback to vector search when `search_text` is unavailable.
  - Subject Q&A adaptively skips web search when local RAG has high-confidence chunks.
  - Verify course code search yields correct objectives and grading criteria (e.g. "SE101").
  - Verify student general questions (e.g. "làm sao để học tốt môn đại cương giải tích tại UIT") triggers web search and crawl fallback, returning well-reasoned answers citing links.
  - `devorbit-api/run.bat` runtime smoke test on `localhost:8080` verified `OPENCODE_API_KEY`, `FIRECRAWL_ENABLED=true`, `EMBEDDING_OFFLINE=false`, `EMBEDDING_PROVIDER=fireworks`, Fireworks embedding requests, Firecrawl scrape success, and non-offline API answers for SE104 and MA006.
  - JDBC verification against PostgreSQL confirmed `knowledge_chunks` has SE104 and MA006 rows with `embedded=1`, `vector_dims(embedding)=4096`, and `knowledge_sources.status=COMPLETED`; runtime logs showed semantic retrieval returned 1 chunk for both course queries.

## Streaming Chat Evidence

- **Frontend Tests**:
  - `useSubjectQa.stream.test.ts` (6 tests) — SSE parser dispatches status/search_result/delta/complete/error events; handles split chunks, non-OK response, missing body, fetch errors.
  - `AiChatWidget.test.tsx` (3 tests) — search results show while streaming; status rows render with Vietnamese text; accumulated markdown renders without fake typewriter.
  - TypeScript: `npx tsc --noEmit` passes with zero errors.
- **Backend Tests** (Maven-available):
  - `OpenCodeAiServiceTest` — SSE delta parsing, offline fallback, malformed chunk resilience.
  - `SubjectQaServiceTest` — `prepareQuery` emits status/search_result events; direct response shortcut.
  - `SubjectQaControllerTest` — `/stream` endpoint returns `text/event-stream`.
  - Existing one-shot tests pass unchanged.
