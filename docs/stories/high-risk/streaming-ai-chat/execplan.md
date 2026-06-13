# Exec Plan

## Goal

Add real SSE streaming to DevOrbit AI chatbot with progress events, incremental web result display, and token-by-token answer deltas.

## Scope

In scope:
- `SubjectQaStreamEvent` DTO with factory methods.
- `SubjectQaProgressSink` / `SubjectQaPreparation` extraction in `SubjectQaService`.
- `OpenCodeAiService.streamCompletion()` with OpenAI SSE delta parsing.
- `POST /api/ai/subject-qa/stream` endpoint in `SubjectQaController`.
- `SubjectQaStreamingConfig` thread pool executor.
- Frontend `streamSubjectQa()` SSE consumer with manual parser.
- `AiChatWidget` live streaming state, progress rows, search result display.
- Tests for backend streaming parser and frontend SSE consumer.
- Docs updates.

Out of scope:
- `/query` endpoint removal or DTO changes.
- State management library.
- Raw model chain-of-thought exposure.
- GET stream endpoint with short-lived tokens.
- Database migration.

## Risk Classification

Risk flags:
- External systems (OpenCode LLM streaming support assumed OpenAI-compatible; fallback to one-shot if not)
- Public contracts (new `/stream` endpoint is additive; `/query` unchanged)
- Existing behavior (one-shot path must be preserved exactly)
- Weak proof (mocked SSE responses on both sides; full e2e requires running API + web)
- Multi-domain (API Java + frontend TypeScript + UI component state)

Hard gates:
- External systems: `OpenCodeAiService.streamCompletion` falls back to `generateCompletion` if streaming fails before first delta.
- Existing behavior: all `SubjectQaService.processQuery()` tests must pass unchanged.
- Public contracts: the `/query` endpoint must not be removed or have its response shape changed.

## Work Phases

1. Discovery (story artifacts + impact analysis).
2. Backend DTO + streaming config + progress sink.
3. SubjectQaService refactor (prepareQuery extraction).
4. OpenCodeAiService streaming method.
5. SSE controller endpoint.
6. Frontend SSE client.
7. AiChatWidget live streaming.
8. Backend + frontend tests.
9. Docs updates.
10. Verification.

## Stop Conditions

Pause for human confirmation if:
- Product behavior is ambiguous.
- LLM provider does not support streaming and fallback produces poor UX.
- Existing one-shot tests break.
- Architecture direction changes.
