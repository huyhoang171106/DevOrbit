# Design

## Domain Model

- `KnowledgeChunk` gains `chunkKind` (VARCHAR 30, default 'DETAIL') and `parentChunkId` (UUID self-ref FK).
- `RagQueryPlan` record: `originalQuery`, `primaryQuery`, `textQuery`, `expandedQueries`, `detectedCourseCodes`.
- `RagQueryPlanner` service: expands Vietnamese intent terms and course metadata.
- `RagResultReranker` component: dedup, lexical overlap boost, section-title match, source diversity.

## Application Flow

1. `SubjectQaService.processQuery()` → `KnowledgeRetrievalService.search(SearchRequest)` or `.search(courseCode, query, topK)`
2. `KnowledgeRetrievalService.search()` → `RagQueryPlanner.plan()` → for each expanded query → embed → `searchHybrid()` → dedup → `RagResultReranker.rerank()`
3. If `searchHybrid` fails, fall back to `searchByVector`.
4. `SubjectQaService` inspects `SemanticKnowledgeContext.hasChunks` + `bestScore`; skips web when local chunks are sufficient.
5. `CourseKnowledgeIndexer` emits `SECTION_SUMMARY` + overlapping `DETAIL` chunks with `parent_chunk_id`.

## Interface Contract

No public DTO changes. Internal changes:
- `KnowledgeRetrievalService.search(SearchRequest)` returns same `SearchResponse` type.
- `KnowledgeRetrievalService.search(String,String,int)` returns same `SearchResult` type.
- `TutorRagService.answer(String)` returns same `TutorResponse`.
- `SubjectQaService.processQuery(SubjectQaRequest)` returns same `SubjectQaResponse`.

## Data Model

- `knowledge_chunks` adds:
  - `chunk_kind VARCHAR(30) NOT NULL DEFAULT 'DETAIL'`
  - `parent_chunk_id UUID REFERENCES knowledge_chunks(id) ON DELETE CASCADE`
  - `search_text tsvector GENERATED ALWAYS AS (to_tsvector('simple', ...)) STORED`
- Indexes: GIN on `search_text`, btree on `parent_chunk_id` and `chunk_kind`.

## UI / Platform Impact

None. All changes are backend API.

## Observability

Log messages added for:
- Hybrid RAG fallback to vector search.
- Web search skipped due to sufficient local RAG.

## Alternatives Considered

1. External reranker (Cohere). Rejected: adds dependency; deterministic Java reranker sufficient for local-first phase.
2. Vietnamese-specific text search (vn_parser). Rejected: needs DB extension; PostgreSQL `simple` config + expanded query terms adequate.
