# Overview

## Current Behavior

DevOrbit's AI chatbot (`/api/ai/subject-qa/query`) returns a full JSON `SubjectQaResponse` after the LLM completes. The frontend waits for the entire response, then uses a fake `StreamingText` typewriter timer to simulate typing from the already-complete `message.content`. Users see no intermediate progress — no "searching" indicators, no web results appearing before the answer, no token-by-token streaming.

## Target Behavior

Add `POST /api/ai/subject-qa/stream` endpoint that emits Server-Sent Events (SSE) with these event types:

- `status` — operational progress stages (analyzing, searching DevOrbit/RAG, searching web, reading sources, composing answer)
- `search_result` — web search result cards appear as soon as available
- `delta` — answer text tokens streamed live from the LLM
- `complete` — final response metadata
- `error` — error notification

The UI renders progress rows, search result cards, and answer deltas in real time. The original `/query` endpoint and `StreamingText` fake are preserved as compatibility fallback for browsers without ReadableStream support.

## Affected Users

- Students using the DevOrbit AI Course Assistant widget.

## Affected Product Docs

- `devorbit-api/docs/knowledge-rag.md`
- `docs/stories/US-021-ai-course-qa-assistant.md`
- `docs/TEST_MATRIX.md`

## Non-Goals

- Changing the existing `/query` endpoint or `SubjectQaResponse` DTO.
- Adding a new state library to the frontend.
- Exposing raw model chain-of-thought or provider reasoning fields.
- Adding GET stream endpoint with request tokens.
- Database migration.
