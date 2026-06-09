# Knowledge RAG Completion Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the DevOrbit Knowledge RAG end-to-end system — web sync, AI tutor, AI module integration, admin APIs, tests, and documentation.

**Architecture:** Spring Boot 4.0.6 + JPA/Hibernate 7.x + PostgreSQL with pgvector for vector similarity search. Firecrawl API abstraction for web scraping. Rule-based intent classification (no LLM for intent detection). FACT_QUERY path uses direct PostgreSQL structured facts; RAG path uses semantic search + LLM grounded generation.

**Tech Stack:** Java 21, Spring Boot 4.0.6, Hibernate 7.x, PostgreSQL + pgvector, Firecrawl API (WebClient), OpenCodeAiService (LLM), Mockito + AssertJ testing.

---

## Current State (Completed)

### Phase 1: PDF Syllabus RAG Pipeline ✅
- Marker markdown → structured syllabus facts → PostgreSQL
- V007 migration: `knowledge_sources`, `knowledge_chunks`, `course_syllabus`, `course_objectives`, `course_outcomes`, `course_assessments`, `course_references`, `course_tools` tables
- V008 migration: source_id FK on child tables, page_from/page_to on chunks
- V009 migration: pgvector extension + embedding column
- `MarkerMarkdownLoader`, `SyllabusValidator`, `SyllabusFactExtractor`, `SyllabusIngestionService`
- `CourseKnowledgeIndexer` (markdown chunking + metadata)
- `KnowledgeSourceService`, `KnowledgeChunkRepository`

### Phase 1.5: pgvector Embeddings ✅
- `KnowledgeEmbeddingService` — embeds chunks via OpenAI API, stores as `vector(1536)`
- `KnowledgeRetrievalService` — cosine similarity search via pgvector `<=>` operator
- `SearchRequest`/`SearchResponse` DTOs

### Phase 2: Firecrawl Web Sync ✅
- `FirecrawlClient` — `scrape()` + `crawl()` via WebClient, `@ConditionalOnProperty("firecrawl.enabled")`
- `FirecrawlProperties` — config: enabled, apiUrl, apiKey, timeoutSeconds, maxPages
- `FirecrawlKnowledgeImporter` — validates URL, delegates to `WebKnowledgeIngestionService`
- `WebKnowledgeIngestionService` — `importUrl()`, `crawlUrl()`, `recrawl()`, SHA-256 hash dedup
- `WebImportRequest`, `CrawlRequest` DTOs

### Phase 3: AI Tutor RAG ✅
- `CourseCodeDetector` — regex `\b([A-Z]{2}[0-9]{3})\b`
- `TutorIntentClassifier` — rule-based Vietnamese keyword matching
- `TutorIntent` enum — FACT_QUERY, LEARNING_ADVICE, ROADMAP, GRAPH_IMPACT, REPO_ADVICE, GENERAL_RAG
- `CourseFactQueryService` — direct DB queries for credits, prerequisite, hours, assessments, objectives, outcomes, sessions
- `TutorRagService` — orchestrator: detect course → classify intent → DB fact query OR semantic search + LLM
- `CitationBuilder` — builds Citation DTOs from ChunkResult list
- `TutorResponse` DTO — answer, citations, confidence

### Phase 4: AI Module Integration ✅
- `LlmContextBuilder.enrichWithSyllabusFacts()` — appends assessments, outcomes, sessions, prerequisites
- `RoadmapGenerator.enhanceSummaryWithLLM()` — enriches top 5 recommended courses with syllabus facts
- `GraphQueryEngine.classifyWithLLM()` — enriches extracted course codes with syllabus facts
- `AdviceGenerator.generateAdvice()` — enriches repo context with syllabus facts

### Phase 5: Admin/Debug APIs ✅
- `AdminKnowledgeController` — 13 endpoints: ingest-folder, ingest-file, sources, courses/{code}, courses/{code}/chunks, courses/{code}/embed, sources/{id}/embed, search, import-url, crawl-url, sources/{id}/recrawl, rag-preview
- `AdminKnowledgeQueryService` — listSources, getCourseDetails, getCourseChunks + DTO mapping
- `AdminKnowledgeCommandService` — ingestFolder, ingestFile

### Tests ✅ (34 tests across 6 files)
- `FirecrawlKnowledgeImporterTest` — 4 tests
- `WebKnowledgeIngestionServiceTest` — 10 tests (importUrl, crawlUrl, recrawl)
- `CourseFactQueryServiceTest` — 6 tests
- `TutorRagServiceTest` — 6 tests
- `CitationBuilderTest` — 3 tests
- `AdminKnowledgeControllerTest` — 4 tests (WebMvcTest)

---

## Remaining Work

### Task 1: Documentation — `docs/knowledge-rag.md`

**Files:**
- Create: `devorbit-api/docs/knowledge-rag.md`

- [ ] **Step 1: Create the documentation file**

```markdown
# Knowledge RAG System

## Overview

DevOrbit Knowledge RAG provides structured syllabus queries and semantic
retrieval-augmented generation for Vietnamese university course content.

## Architecture

```
User Question
  → CourseCodeDetector (regex: XX999)
  → TutorIntentClassifier (rule-based Vietnamese keywords)
  → Route:
      FACT_QUERY → CourseFactQueryService → PostgreSQL structured facts → answer (no LLM)
      Others     → KnowledgeRetrievalService.search() → pgvector cosine similarity
                  → LlmContextBuilder.buildQueryContext() → OpenCodeAiService → answer + citations
```

## Marker Conversion (PDF → Markdown)

Marker is a tool that converts PDF syllabi into structured markdown files.

```bash
# Convert a single PDF
marker_single input.pdf output_dir/

# Convert a folder of PDFs
marker_batch input_folder/ output_folder/ --concurrency 4
```

Output format: Markdown files with headings for each syllabus section:
- `## Mã học phần / Course code` → courseCode
- `## Tên học phần / Course name` → courseName
- `## Số tín chỉ` → credits
- `## Tiên quyết` → prerequisite
- `## Mục tiêu` → objectives
- `## Chuẩn đầu ra` → outcomes
- `## Nội dung` → sessions (with `## Tuần N` sub-headings)
- `## Đánh giá` → assessments (with weight percentages)

## Folder Layout

```
knowledge/
├── IT003.md          # Intro to Programming syllabus
├── IT004.md          # Data Structures syllabus
├── IT005.md          # Algorithms syllabus
└── ...
```

## Ingestion

### Ingest Folder (Marker Markdown)

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/ingest-folder
```

Processes all `.md` files in the configured knowledge directory.
Returns `FolderIngestionSummary` with per-file status.

### Ingest Single File

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/ingest-file \
  -H "Content-Type: application/json" \
  -d '{"filePath": "knowledge/IT003.md"}'
```

### Import Web URL (Firecrawl)

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/import-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/course/IT003",
    "courseCode": "IT003",
    "trustLevel": "OFFICIAL",
    "embedAfterImport": true
  }'
```

Scrapes single page via Firecrawl, saves as knowledge source, chunks, optional embed.

### Crawl Website (Firecrawl)

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/crawl-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/courses",
    "courseCode": "IT003",
    "trustLevel": "OFFICIAL",
    "maxPages": 10,
    "embedAfterImport": true
  }'
```

Crawls multiple pages, each processed independently with hash dedup.

### Re-crawl Source

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/sources/{sourceId}/recrawl
```

Re-fetches URL, compares content hash. If unchanged → SKIPPED. If changed → new source created.

## Embedding

### Embed by Course

```bash
curl -X POST "http://localhost:8080/api/admin/knowledge/courses/IT003/embed?force=false"
```

### Embed by Source

```bash
curl -X POST "http://localhost:8080/api/admin/knowledge/sources/{sourceId}/embed?force=false"
```

Embeds chunks via OpenAI `text-embedding-3-small` (1536 dimensions), stores as pgvector.

## Search

### Semantic Search

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "IT003",
    "query": "quy hoạch động",
    "topK": 5
  }'
```

Returns ranked chunks with cosine similarity scores.

### RAG Preview (Debug)

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/rag-preview \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "IT003",
    "query": "quy hoạch động học ở đâu",
    "topK": 5
  }'
```

Returns retrieved chunks + constructed prompt for debugging.

## AI Tutor RAG Flow

### Question Flow

```
User: "IT003 mấy tín chỉ?"
  → CourseCodeDetector extracts "IT003"
  → TutorIntentClassifier detects FACT_QUERY (Vietnamese: "mấy tín chỉ", "bao nhiêu tín")
  → CourseFactQueryService.getCredits("IT003") → 4
  → TutorResponse { answer: "Học phần IT003 có 4 tín chỉ.", confidence: HIGH, citations: [] }

User: "Em yếu quy hoạch động thì học IT003 sao?"
  → CourseCodeDetector extracts "IT003"
  → TutorIntentClassifier detects LEARNING_ADVICE
  → KnowledgeRetrievalService.search("IT003", "quy hoạch động yếu", 5)
  → LlmContextBuilder.buildQueryContext() → grounded prompt
  → OpenCodeAiService.chat(prompt) → answer
  → TutorResponse { answer: "...", citations: [...], confidence: MEDIUM }
```

### Intent Types

| Intent | Trigger Keywords | Path |
|--------|-----------------|------|
| FACT_QUERY | "mấy tín chỉ", "tiên quyết", "bao nhiêu phần trăm" | Direct DB query |
| LEARNING_ADVICE | "học sao", "yếu", "khó", "nên học" | RAG + LLM |
| ROADMAP | "lộ trình", "đường dẫn", "học theo thứ tự" | RAG + LLM |
| GRAPH_IMPACT | "liên quan", "ảnh hưởng", "nếu đổi" | RAG + LLM |
| REPO_ADVICE | "code", "project", "thực hành" | RAG + LLM |
| GENERAL_RAG | default | RAG + LLM |

### Citation Behavior

Citations are extracted from retrieved chunks:
- `sourceId` — knowledge source ID
- `fileName` — original file name
- `url` — source URL (for web imports)
- `sectionTitle` — markdown section heading
- `pageFrom` / `pageTo` — page numbers (for PDF markers)
- `chunkIndex` — chunk position within source

### Answer Rules

- Answer in concise Vietnamese
- Preserve Vietnamese course names
- Cite chunk sections/pages when available
- Say "không tìm thấy trong dữ liệu hiện có" when context insufficient
- No hallucination — answer only from provided context
- FACT_QUERY: never calls LLM if DB has answer

## AI Module Integration

### RoadmapGenerator
- Uses `CourseFactQueryService` for prerequisites, credits, outcomes, sessions
- Enriches top 5 recommended courses with syllabus facts before LLM generation

### GraphQueryEngine
- Uses structured facts before LLM for impact analysis
- Answers "what if I change X?" with course graph + syllabus context

### AdviceGenerator
- Combines repository metadata + course syllabus chunks
- Outputs concrete learning path with course context

## Known Limitations

- Firecrawl requires valid API key (`firecrawl.api-key` in application.yaml)
- Embedding requires OpenAI API key (`openai.api-key`)
- LLM responses depend on model availability (OpenCodeAiService)
- pgvector extension must be installed on PostgreSQL
- Marker PDF conversion quality varies with PDF complexity
- Vietnamese intent classification is rule-based (not ML) — may miss edge cases
- No real-time web crawling — Firecrawl is async batch processing
- No authentication on admin endpoints beyond ROLE_ADMIN

## Demo Workflow: IT003

### 1. Convert PDF to Markdown

```bash
marker_single syllabi/IT003.pdf knowledge/
```

### 2. Start Backend

```bash
cd devorbit-api
./mvnw spring-boot:run
```

### 3. Ingest Folder

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/ingest-folder
```

### 4. Embed Course

```bash
curl -X POST "http://localhost:8080/api/admin/knowledge/courses/IT003/embed?force=false"
```

### 5. Test Semantic Search

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{"courseCode": "IT003", "query": "quy hoạch động", "topK": 3}'
```

### 6. Test AI Tutor (FACT_QUERY)

```bash
# Via ChatService API — "IT003 mấy tín chỉ?"
# Expected: 4 credits, no LLM call, HIGH confidence
```

### 7. Test AI Tutor (RAG)

```bash
# Via ChatService API — "Em yếu quy hoạch động thì học IT003 sao?"
# Expected: grounded answer with citations from syllabus chunks
```

### 8. Import Web URL

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/import-url \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/IT003", "courseCode": "IT003", "embedAfterImport": true}'
```

### 9. Re-run Search

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{"courseCode": "IT003", "query": "quy hoạch động", "topK": 5}'
# Should now include web-sourced chunks
```

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/knowledge/ingest-folder` | Ingest all markdown files |
| POST | `/api/admin/knowledge/ingest-file` | Ingest single file |
| GET | `/api/admin/knowledge/sources` | List all knowledge sources |
| GET | `/api/admin/knowledge/courses/{code}` | Get course syllabus details |
| GET | `/api/admin/knowledge/courses/{code}/chunks` | Get course chunks |
| POST | `/api/admin/knowledge/courses/{code}/embed` | Embed course chunks |
| POST | `/api/admin/knowledge/sources/{id}/embed` | Embed source chunks |
| POST | `/api/admin/knowledge/search` | Semantic search |
| POST | `/api/admin/knowledge/import-url` | Import single URL via Firecrawl |
| POST | `/api/admin/knowledge/crawl-url` | Crawl website via Firecrawl |
| POST | `/api/admin/knowledge/sources/{id}/recrawl` | Re-crawl source |
| POST | `/api/admin/knowledge/rag-preview` | Debug RAG preview |
```

- [ ] **Step 2: Verify file created**

Run: `ls D:/temp/devorbit/devorbit-api/docs/knowledge-rag.md`
Expected: file exists with all sections above.

- [ ] **Step 3: Commit**

```bash
cd D:/temp/devorbit/devorbit-api
git add docs/knowledge-rag.md
git commit -m "docs: add Knowledge RAG system documentation with demo workflow"
```

---

### Task 2: Final Verification

- [ ] **Step 1: Compile**

Run: `cd D:/temp/devorbit/devorbit-api && ./mvnw clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 2: Run all tests**

Run: `cd D:/temp/devorbit/devorbit-api && ./mvnw test`
Expected: All new tests pass. Pre-existing failures on master are documented separately.

- [ ] **Step 3: Separate new failures from master failures**

Run on clean master to prove pre-existing failures:
```bash
cd D:/temp/devorbit/devorbit-api
git stash
./mvnw test 2>&1 | grep -E "Tests run:|FAILED"
git stash pop
```

Document any master failures in a comment below. Do NOT claim failures are pre-existing without proof.

- [ ] **Step 4: Final commit (if any fixes needed)**

```bash
cd D:/temp/devorbit/devorbit-api
git add -A
git commit -m "fix: resolve test failures from Knowledge RAG implementation"
```

---

## Files Summary

### New files (1):
1. `docs/knowledge-rag.md` — system documentation with demo workflow

### Pre-existing completed files (all phases 1-5):

**Phase 1 — PDF Syllabus RAG Pipeline:**
- `V007__create_syllabus_rag_pipeline.sql`
- `V008__add_source_id_to_child_tables.sql`
- `V009__add_pgvector_and_embedding.sql`
- `MarkerMarkdownLoader.java`
- `SyllabusValidator.java`
- `SyllabusFactExtractor.java`
- `SyllabusIngestionService.java`
- `CourseKnowledgeIndexer.java`
- `KnowledgeSourceService.java`
- Entities: `KnowledgeSource.java`, `KnowledgeChunk.java`, `CourseSyllabus.java`, `CourseObjective.java`, `CourseOutcome.java`, `CourseAssessment.java`, `CourseReference.java`, `CourseTool.java`, `CourseSession.java`
- Repositories: all 9 repository interfaces

**Phase 1.5 — pgvector Embeddings:**
- `KnowledgeEmbeddingService.java`
- `KnowledgeRetrievalService.java`
- `SearchRequest.java`, `SearchResponse.java`

**Phase 2 — Firecrawl Web Sync:**
- `FirecrawlClient.java`
- `FirecrawlProperties.java`
- `FirecrawlKnowledgeImporter.java`
- `WebKnowledgeIngestionService.java`
- `WebImportRequest.java`, `CrawlRequest.java`

**Phase 3 — AI Tutor RAG:**
- `CourseCodeDetector.java`
- `TutorIntentClassifier.java`
- `TutorIntent.java`
- `CourseFactQueryService.java`
- `TutorRagService.java`
- `CitationBuilder.java`
- `TutorResponse.java`

**Phase 4 — AI Module Integration:**
- `LlmContextBuilder.java` (modified — `enrichWithSyllabusFacts()`)
- `RoadmapGenerator.java` (modified — `enhanceSummaryWithLLM()`)
- `GraphQueryEngine.java` (modified — `classifyWithLLM()`)
- `AdviceGenerator.java` (modified — `generateAdvice()`)

**Phase 5 — Admin/Debug APIs:**
- `AdminKnowledgeController.java` (modified — 4 new endpoints)
- `AdminKnowledgeQueryService.java`
- `AdminKnowledgeCommandService.java`
- `RagPreviewRequest.java`, `RagPreviewResponse.java`

**Tests:**
- `FirecrawlKnowledgeImporterTest.java` (4 tests)
- `WebKnowledgeIngestionServiceTest.java` (10 tests)
- `CourseFactQueryServiceTest.java` (6 tests)
- `TutorRagServiceTest.java` (6 tests)
- `CitationBuilderTest.java` (3 tests)
- `AdminKnowledgeControllerTest.java` (4 tests)

---

## Risks

| Risk | Level | Mitigation |
|------|-------|------------|
| Documentation completeness | LOW | Follows actual implementation, verified against codebase |
| Pre-existing test failures on master | MEDIUM | Separate new vs old failures with proof |
| Firecrawl API key missing | LOW | `@ConditionalOnProperty` disables gracefully |
| OpenAI API key missing | LOW | Embedding service returns 0 embedded, search returns empty |
| pgvector extension not installed | LOW | Migration V009 handles installation |

---

## Intentionally Not Done

- **Frontend UI** — no frontend changes unless required
- **ML-based intent classifier** — rule-based is sufficient for Vietnamese university domain
- **Real-time web crawling** — Firecrawl is async batch, not streaming
- **Authentication changes** — existing ROLE_ADMIN pattern sufficient
- **Performance optimization** — premature without production load data
- **Multi-language support** — Vietnamese-only per domain requirement
