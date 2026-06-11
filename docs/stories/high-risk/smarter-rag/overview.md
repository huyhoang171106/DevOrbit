# Overview

## Current Behavior

DevOrbit's course Q&A RAG uses vector-only pgvector cosine similarity search in `KnowledgeRetrievalService`. Single-query embedding; course-code-only filtering; no reranker; no chunk overlap; Exa/Firecrawl web context added unconditionally when search intent is detected, regardless of local RAG quality.

## Target Behavior

Hybrid local-first retrieval pipeline: `RagQueryPlanner` expands Vietnamese study/resource/project phrasing into search variants → multi-query hybrid search (pgvector + PostgreSQL FTS) via `searchHybrid` → `RagResultReranker` with lexical overlap boost, section-title match, source diversity → adaptive web fallback triggered only when local chunks are empty/weak. Chunking uses overlap (500 chars) and `SECTION_SUMMARY`/`DETAIL` hierarchy with `parent_chunk_id`. TutorRAG exposes scores in context.

## Affected Users

- Students asking course Q&A through SubjectQaService.
- Students using AI Tutor (TutorRagService).
- Admin users previewing RAG results.

## Affected Product Docs

- `devorbit-api/docs/knowledge-rag.md`
- `docs/knowledge-ingestion-phase-1.md`
- `docs/stories/US-021-ai-course-qa-assistant.md`
- `docs/TEST_MATRIX.md`

## Non-Goals

- Adding an external reranker (Cohere, ONNX, etc.).
- Changing public DTOs (`SearchRequest`, `SearchResponse`, `SubjectQaResponse`, `TutorResponse`).
- Adding Vietnamese-specific text search parser.
- Changing Exa/Firecrawl provider behavior.
