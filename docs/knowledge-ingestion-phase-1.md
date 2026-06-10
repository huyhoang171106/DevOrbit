# Knowledge Ingestion Phase 1

PDF Syllabus RAG pipeline - Phase 1 implementation.

## Overview

This phase implements the ingestion pipeline for converting UIT course syllabi PDFs into structured, queryable data. The pipeline reads marker-generated markdown files, extracts structured facts using LLM, validates them, and stores them in PostgreSQL.

## Prerequisites

### 1. Convert PDFs to Markdown (Offline)

Use [marker](https://github.com/datalab-to/marker) to convert PDF syllabi to markdown:

```bash
# Install marker
pip install marker-pdf

# Convert PDFs to markdown
marker ./data/syllabus-pdf \
  --output_dir ./data/processed/marker-md \
  --output_format markdown \
  --paginate_output \
  --workers 1
```

### 2. Expected Input Structure

Marker supports two output layouts:

**Folder-per-course (preferred):**
```
data/processed/marker-md/
├── IT003/
│   └── IT003.md
├── SE104/
│   └── SE104.md
└── ...
```

**Flat file layout:**
```
data/processed/marker-md/
├── IT003.md
├── SE104.md
└── ...
```

## Configuration

Add to `application.yaml`:

```yaml
devorbit:
  knowledge:
    marker-md-dir: ./data/processed/marker-md
    ingestion:
      enabled: true
```

Or via environment variables:
- `KNOWLEDGE_MARKER_MD_DIR` - path to markdown directory
- `KNOWLEDGE_INGESTION_ENABLED` - enable/disable ingestion

## API Endpoints

All endpoints require `ROLE_ADMIN` authentication.

### Ingest All Files

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/ingest-folder \
  -H "Authorization: Bearer <admin-token>"
```

Response:
```json
{
  "totalFiles": 2,
  "completed": 2,
  "skipped": 0,
  "failed": 0,
  "courseCodes": ["IT003", "SE104"]
}
```

### Ingest Single File

```bash
curl -X POST http://localhost:8080/api/admin/knowledge/ingest-file \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"filePath": "./data/processed/marker-md/IT003/IT003.md"}'
```

Response:
```json
{
  "sourceId": "uuid-here",
  "courseCode": "IT003",
  "status": "COMPLETED",
  "warnings": [],
  "errorMessage": null
}
```

### List Sources

```bash
curl http://localhost:8080/api/admin/knowledge/sources \
  -H "Authorization: Bearer <admin-token>"
```

### Get Course Details

```bash
curl http://localhost:8080/api/admin/knowledge/courses/IT003 \
  -H "Authorization: Bearer <admin-token>"
```

Response includes:
- Syllabus metadata (courseCode, names, credits, hours)
- Objectives with outcome references
- Learning outcomes
- Theory and practice sessions
- Assessment components with weights
- References
- Tools

### Get Chunks (Debug)

```bash
curl http://localhost:8080/api/admin/knowledge/courses/IT003/chunks \
  -H "Authorization: Bearer <admin-token>"
```

## Verification with IT003

After ingesting IT003, verify the stored data:

```bash
# Get course details
curl http://localhost:8080/api/admin/knowledge/courses/IT003 \
  -H "Authorization: Bearer <admin-token>"
```

Expected data:
- courseCode = "IT003"
- courseNameVi = "Cấu trúc dữ liệu và giải thuật"
- courseNameEn = "Data Structures and Algorithms"
- credits = 4
- theoryHours = 45
- practiceHours = 30
- selfStudyHours = 90
- prerequisite = "Nhập môn lập trình"
- assessments: A1=10, A2=20, A3=20, A4=50
- sessions include: Tổng quan về giải thuật, Quay lui, Nhánh và cận, Chia để trị, Quy hoạch động, etc.
- tools: Code::Blocks

## What Is Not Implemented Yet

This is Phase 1. The following are intentionally not implemented:

- **Firecrawl** - PDF fetching from web sources
- **pgvector embeddings** - Vector similarity search for RAG
- **ChatService RAG integration** - Using knowledge in chat responses
- **Incremental updates** - Only full re-ingestion supported
- **Course deduplication across sources** - Multiple PDFs for same course

## Architecture

```
MarkerMarkdownLoader → SyllabusFactExtractor → SyllabusValidator
         ↓                      ↓                      ↓
    Load .md files      LLM extraction         Validate facts
         ↓                      ↓                      ↓
    KnowledgeSource    ExtractedSyllabusFacts    Warnings/Errors
         ↓                      ↓
    Store source       SyllabusIngestionService
                              ↓
                    Save all entities:
                    - course_syllabus
                    - course_objectives
                    - course_outcomes
                    - course_sessions
                    - course_assessments
                    - course_references
                    - course_tools
                    - knowledge_chunks
```

## Error Handling

- **LLM failures**: Source marked as FAILED with error message
- **Validation errors**: Hard fail for missing courseCode or course name
- **Validation warnings**: Logged and returned, do not block ingestion
- **File not found**: Returns error in ingestion report
- **Duplicate ingestion**: Skipped if content hash unchanged and status COMPLETED
