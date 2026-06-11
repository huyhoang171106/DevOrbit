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
      Others     → RagQueryPlanner (query expansion + course metadata)
                  → Multi-query hybrid search: pgvector + PostgreSQL FTS
                  → RagResultReranker (lexical boost, section match, source diversity)
                  → OpenCodeAiService → answer + citations
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

### Hybrid Retrieval

The KnowledgeRetrievalService now performs multi-query hybrid retrieval:
- **RagQueryPlanner** expands Vietnamese study/resource/project intent terms and enriches queries with course metadata from DB.
- **searchHybrid** CTE combines pgvector cosine similarity and PostgreSQL FTS (`ts_rank_cd`) with RRF-style score fusion, plus metadata boosts for trust level, source type, and chunk kind.
- **RagResultReranker** deduplicates by chunk ID, applies lexical overlap boost (+0.006 per matching token, max 0.030), section-title match boost (+0.010), and source diversity (max 2 per source before allowing extras).
- Falls back to vector-only `searchByVector` if `search_text` column or `plainto_tsquery` is unavailable.

### Chunking

CourseKnowledgeIndexer now emits two chunk kinds:
- **SECTION_SUMMARY**: For each headed section, a summary chunk with title + first 900 chars.
- **DETAIL**: Full section text; large sections are split with 500-char overlap and reference the parent summary via `parent_chunk_id`.

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

## Streaming Chat UX

### Endpoints

- `POST /api/ai/subject-qa/query` — existing one-shot JSON endpoint (unchanged).
- `POST /api/ai/subject-qa/stream` — new SSE endpoint for live streaming.

### SSE Event Contract

All events are sent as SSE with `event:<type>` and `data:<JSON>` lines.

| Event Type | Payload Fields | Description |
|------------|----------------|-------------|
| `status` | `type`, `stage`, `message` | Operational progress (analyzing, searching, composing) |
| `search_result` | `type`, `searchResult` | Single web search result card |
| `delta` | `type`, `content` | Incremental answer text chunk |
| `complete` | `type`, `response` | Final response metadata |
| `error` | `type`, `message` | Error notification |

### Progress Stages

`session`, `analyze`, `devorbit_context`, `rag`, `web_search`, `web_read`, `answer`, `done`, `error`

### UI Behavior

- Status rows render as compact progress items with spinner for active stage.
- Search result cards appear as soon as the backend emits them (before answer text).
- Answer text grows incrementally from SSE `delta` events — no fake typewriter.
- Final message shows sources and Copy button after streaming completes.
- Browser fallback: if `ReadableStream` is unsupported, falls back to the one-shot `/query` endpoint.

### Constraints

- Progress statuses describe system operations only.
- The `/query` endpoint is preserved for compatibility.

## Known Limitations

- Firecrawl requires valid API key (`firecrawl.api-key` in application.yaml)
- Embedding requires OpenAI API key (`openai.api-key`)
- LLM responses depend on model availability (OpenCodeAiService)
- pgvector extension must be installed on PostgreSQL
- Marker PDF conversion quality varies with PDF complexity
- Intent classification is rule-based; retrieval now expands Vietnamese phrasing before hybrid search.
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
