# User Story: AI Course Q&A Assistant

As a student,
I want to ask the AI assistant questions about courses, syllabi, grading criteria, and study experiences,
So that I can better plan my learning journey and study effectively.

## Acceptance Criteria

- [x] Database is enriched with syllabus fields (`learning_objectives`, `grading_criteria`, `topics`) and chat history tables (`chat_sessions`, `chat_messages`).
- [x] Syllabus seed data parsed and populated into database (from UIT course summary document).
- [x] Backend service orchestrates query analysis, parses course codes, scrapes web context using JSoup, and generates answers using OpenCode Go DeepSeek-v4-flash model.
- [x] Frontend features a premium Glassmorphic floating AI chat widget with suggestions and citations.
- [x] Chat messages parse course codes (like `SE101`) to render clickable CourseBadges.
- [x] Generic greetings and broad resource requests do not invent DevOrbit features; the assistant asks for a specific course code when it lacks grounded course/repo context.
- [x] LLM chat path uses OpenCode responses with DevOrbit DB context, Firecrawl web scraping, and Fireworks-backed semantic retrieval without fabricating DevOrbit-only features.
- [x] Course-specific chat lazily indexes trusted DevOrbit database course/repo context into Knowledge RAG chunks and embeds them before semantic retrieval.

## Technical Notes

- **Backend**:
  - Flyway migrations `V003` and `V004` to create and populate fields.
  - `SubjectQaService.java` manages searching course syllabus fields, performing web searches, scraping web pages, and querying OpenCode Go API.
  - `SubjectQaController.java` exposes REST endpoint `/api/ai/subject-qa/query`.
- **Frontend**:
  - `AiChatWidget.tsx` floating widget with suggestions and rich citation renderer.
  - `useSubjectQa.ts` react-query hook integrated into `Layout.tsx` for cross-page availability.

## Validation Proof

- **Automated Tests**:
  - Unit tests run and pass.
  - `./mvnw.cmd -q -Dtest=SubjectQaServiceTest test` verifies the assistant still returns a response when chat persistence and web search are unavailable, and verifies greetings/resource requests do not trigger ungrounded web/LLM answers.
  - `./mvnw.cmd test -B` passed 142 tests on 2026-06-10 after Firecrawl/Fireworks/RAG wiring and native pgvector write fixes.
- **Manual**:
  - Verify course code search yields correct objectives and grading criteria (e.g. "SE101").
  - Verify student general questions (e.g. "làm sao để học tốt môn đại cương giải tích tại UIT") triggers web search and crawl fallback, returning well-reasoned answers citing links.
  - `devorbit-api/run.bat` runtime smoke test on `localhost:8080` verified `OPENCODE_API_KEY`, `FIRECRAWL_ENABLED=true`, `EMBEDDING_OFFLINE=false`, `EMBEDDING_PROVIDER=fireworks`, Fireworks embedding requests, Firecrawl scrape success, and non-offline API answers for SE104 and MA006.
  - JDBC verification against PostgreSQL confirmed `knowledge_chunks` has SE104 and MA006 rows with `embedded=1`, `vector_dims(embedding)=4096`, and `knowledge_sources.status=COMPLETED`; runtime logs showed semantic retrieval returned 1 chunk for both course queries.
