# Exec Plan

## Goal

Make DevOrbit's course Q&A RAG smarter with hybrid retrieval, query expansion, reranking/diversity, metadata boosting, adaptive web fallback, and overlap/hierarchical chunking.

## Scope

In scope:
- V011 migration: `chunk_kind`, `parent_chunk_id`, `search_text` tsvector, indexes.
- Entity changes for `chunkKind` and `parentChunkId`.
- `KnowledgeSchemaInitializer` DDL matching V011.
- `KnowledgeChunkRepository.searchHybrid` native query.
- `RagQueryPlan`/`RagQueryPlanner` for query expansion.
- `RagResultReranker` for deterministic reranking.
- `KnowledgeRetrievalService` multi-query hybrid flow.
- `CourseKnowledgeIndexer` overlap and section-summary/detail chunking.
- `SubjectQaService` adaptive local-first web fallback.
- `TutorRagService` score display in context.
- WebSearchService fallback and noise tests.
- Docs, story, test matrix updates.

Out of scope:
- External reranker dependency.
- Public DTO changes.
- Vietnamese-specific text search parser.
- Exa/Firecrawl provider behavior or new web providers.

## Risk Classification

Risk flags:
- Data model (new columns, generated tsvector)
- External systems (Exa/Firecrawl fallback path changes)
- Public contracts (internal SearchResult shape used by AdminKnowledgeController)
- Existing behavior (web search was unconditional for search intent, now adaptive)
- Weak proof (full-suite H2/vector blocker)
- Multi-domain (API, schema, indexing, test)

Hard gates:
- Data model: V011 migration must be reviewable; KnowledgeSchemaInitializer must match columns.
- External systems: Exa/Firecrawl optional provider keys must produce empty/fallback, not failure.

## Work Phases

1. Discovery (complete via pre-edit impact analysis).
2. Schema + entity + initializer + migration.
3. Repository hybrid query method.
4. Query planning + reranking (independent new classes + tests).
5. Multi-query hybrid retrieval service.
6. Overlap + hierarchical chunking.
7. Adaptive web fusion.
8. TutorRag score display.
9. WebSearchService fallback tests.
10. Docs/story/test-matrix update.
11. Verification.

## Stop Conditions

Pause for human confirmation if:
- Product behavior is ambiguous.
- Data migration or deletion risk appears.
- Validation requirements need to be weakened.
- Architecture direction changes.
