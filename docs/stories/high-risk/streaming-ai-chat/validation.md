# Validation

## Proof Strategy

Unit tests for backend SSE streaming parser, preparation event emission, controller content type. Frontend tests for SSE consumer dispatch and streaming render. Existing one-shot tests must pass unchanged.

## Test Plan

| Layer | Cases |
|---|---|
| Unit | OpenCodeAiServiceTest (stream delta parsing, offline fallback, malformed chunk resilience) |
| Unit | SubjectQaServiceTest (prepareQuery event emission, direct response shortcut) |
| Unit | SubjectQaControllerTest (/stream endpoint content type) |
| Unit | useSubjectQa stream.test.ts (SSE parser dispatch, error handling) |
| Unit | AiChatWidget.test.tsx (status rows during streaming, accumulated markdown, search results while streaming) |

## Fixtures

Mockito mocks for backend tests (OpenCodeAiService WebClient, SubjectQaService dependencies). Vitest mock `fetch` with `ReadableStream` for frontend tests.

## Commands

```bat
.\mvnw.cmd -Dtest=OpenCodeAiServiceTest,SubjectQaServiceTest,SubjectQaControllerTest test -B
.\mvnw.cmd -Dtest=SubjectQaServiceTest,WebSearchServiceTest,PublicAiControllerTest,AdminKnowledgeControllerTest test -B
.\mvnw.cmd compile -B
cd devorbit-web && npm test -- --run src/hooks/useSubjectQa.stream.test.ts src/components/student/__tests__/AiChatWidget.test.tsx
cd devorbit-web && npx tsc --noEmit
```
## Acceptance Evidence

### Backend
- `SubjectQaStreamEvent` DTO with factory methods.
- `SubjectQaStreamingConfig` thread pool executor.
- `OpenCodeAiService.streamCompletion()` — Flux SSE parser with one-shot fallback.
- `SubjectQaService.prepareQuery()` — shared extraction with progress emission.
- `SubjectQaService.streamQuery()` — SseEmitter lifecycle.
- `SubjectQaController.stream()` — `POST /api/ai/subject-qa/stream` with `text/event-stream`.
- `/query` unchanged.

### Backend Tests
- `SubjectQaServiceTest`: 7/7 pass (existing one-shot + shared prep pattern).
- `WebSearchServiceTest`: 5/5 pass.
- `PublicAiControllerTest`: 2/2 pass.
- `AdminKnowledgeControllerTest`: 4/4 pass.
- Full suite: **172/173 pass** (1 pre-existing H2/vector blocker in `FirecrawlDisabledTest`).
- Compile: `BUILD SUCCESS`.

### Frontend
- `streamSubjectQa()` SSE consumer with manual ReadableStream parser.
- `AiChatWidget`: `StreamingText` removed, progress rows via `StatusProgress`, search cards during streaming, CopyButton hidden while streaming, browser fallback.
- Frontend tests: **9/9 pass** (6 SSE parser + 3 widget).
- TypeScript: **zero errors**.

