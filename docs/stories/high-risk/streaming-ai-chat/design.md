# Design

## SSE Event Contract

All events use `SseEmitter.event().name(event.type()).data(event)`.

Event names: `status`, `search_result`, `delta`, `complete`, `error`.

### status

|Field|Type|Description|
|---|---|---|
|type|string|`"status"`|
|stage|string|Progress stage identifier|
|message|string|Vietnamese user-facing message|

Stages: `session`, `analyze`, `devorbit_context`, `rag`, `web_search`, `web_read`, `answer`, `done`, `error`.

### search_result

|Field|Type|Description|
|---|---|---|
|type|string|`"search_result"`|
|searchResult|WebSearchResult|Single web search result|

### delta

|Field|Type|Description|
|---|---|---|
|type|string|`"delta"`|
|content|string|Incremental answer text chunk|

### complete

|Field|Type|Description|
|---|---|---|
|type|string|`"complete"`|
|response|SubjectQaResponse|Final metadata|

### error

|Field|Type|Description|
|---|---|---|
|type|string|`"error"`|
|message|string|Error description|

## Frontend State Model

New types added to `useSubjectQa.ts`:

- `SubjectQaStreamStage` — union of stage identifiers.
- `SubjectQaStreamEvent` — discriminated union by `type`.
- `SubjectQaStreamHandlers` — callback object for `onStatus`, `onSearchResult`, `onDelta`, `onComplete`, `onError`.
- `streamSubjectQa()` — async function using `fetch` + `ReadableStream` to consume SSE.

Extended `AiChatMessage`:

- `statusEvents?: AiChatStatusEvent[]` — live progress status rows.
- Content rendered directly via `MarkdownRenderer` without `StreamingText`.

## API Changes

### New endpoint

`POST /api/ai/subject-qa/stream`
- Content-Type: `application/json`
- Accept: `text/event-stream`
- Request body: `SubjectQaRequest` (existing DTO)
- Response: SSE stream via `SseEmitter` (120s timeout)

### Existing endpoint unchanged

`POST /api/ai/subject-qa/query` — unchanged contract.

## Data Model

No changes.

## UI / Platform Impact

- `AiChatWidget.tsx` replaces `StreamingText` fake typing with live delta accumulation.
- Progress rows render during streaming via `statusEvents`.
- Search results render immediately when `search_result` events arrive.
- `CopyButton` hidden during streaming.
- Browser fallback to `/query` if ReadableStream unsupported.

## Observability

- Backend logs for streaming errors, fallback to one-shot.
- Frontend error handler shows user-facing error message.
