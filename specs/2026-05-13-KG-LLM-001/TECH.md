# TECH.md — Hoàn thiện tích hợp AI trong Knowledge Graph (Scenario Engine)

> **Last Updated:** 2026-05-16
> **Status:** Backend fully implemented; Frontend refactor pending

## 1. Current System Context

### Files Affected — Implementation Status

| File | Vai trò | Status |
|---|---|---|
| `service/AiService.java` | Backend AI logic — Facade | ✅ REWRITTEN — delegates to 5 generators |
| `service/ai/SummaryGenerator.java` | **NEW** — Summary engine | ✅ CREATED |
| `service/ai/AdviceGenerator.java` | **NEW** — Advice engine | ✅ CREATED |
| `service/ai/RoadmapGenerator.java` | **NEW** — Roadmap engine | ✅ CREATED |
| `service/ai/GraphQueryEngine.java` | **NEW** — KG query engine | ✅ CREATED |
| `service/ai/CurriculumMatcher.java` | **NEW** — Career match engine | ✅ CREATED |
| `controller/PublicAiController.java` | AI endpoints | ✅ CREATED with 4 endpoints |
| `dto/AiQueryRequest.java` | **NEW** | ✅ CREATED |
| `dto/AiQueryResponse.java` | **NEW** | ✅ CREATED |
| `web/hooks/useAiRoadmap.ts` | Frontend | ⏳ PENDING — still uses LLM |
| `web/hooks/useAiGraphQuery.ts` | **NEW** | ⏳ PENDING |
| `web/lib/ai.ts` | LLM config cũ | ⏳ PENDING — not yet deleted |

## 2. Architecture (IMPLEMENTED ✅)

```
Browser (React)
  └── useAiRoadmap.ts (PENDING: should call) → POST /api/ai/generate-roadmap (backend)
  └── (PENDING: useAiGraphQuery) → POST /api/ai/knowledge-graph/query

Spring Boot Backend (IMPLEMENTED)
  AiService.java (Facade) ✅
    ├── SummaryGenerator.java ✅    — repo metadata + course context
    ├── AdviceGenerator.java ✅     — impact scores + risks
    ├── RoadmapGenerator.java ✅    — graph + career keywords
    ├── GraphQueryEngine.java ✅    — keyword matching trên graph
    └── CurriculumMatcher.java ✅   — course career matching

Mobile (Android) — không đổi, gọi REST API từ backend
```

## 3. Implementation Status

### Phase 1: Core Engine — COMPLETED ✅

| Step | Component | Status |
|------|-----------|--------|
| Step 1 | CurriculumMatcher.java | ✅ |
| Step 2 | SummaryGenerator.java | ✅ |
| Step 3 | AdviceGenerator.java | ✅ |
| Step 4 | RoadmapGenerator.java | ✅ |
| Step 5 | GraphQueryEngine.java | ✅ |
| Step 6 | AiService.java (Facade) | ✅ |

### Phase 2: New Endpoint — COMPLETED ✅

| Step | Component | Status |
|------|-----------|--------|
| Step 7 | DTOs + Controller | ✅ (AiQueryRequest, AiQueryResponse, PublicAiController) |

### Phase 3: Frontend Refactor — PENDING ⏳

| Step | Component | Status |
|------|-----------|--------|
| Step 8 | useAiRoadmap.ts → backend | ⏳ Still uses direct LLM call |
| Step 9 | useAiGraphQuery.ts (NEW) | ⏳ Not yet created |
| Step 10 | Delete lib/ai.ts | ⏳ Not yet done |

## 4. Testing Strategy

### Unit Tests (core logic)
- Backend generators are deterministic and testable via JUnit
- No dedicated test classes created yet — should be added

### Manual Verification
| Check | Status |
|-------|--------|
| `curl GET /api/ai/repo/1/summary` | ✅ Working |
| `curl GET /api/ai/repo/1/advice` | ✅ Working |
| `curl POST /api/ai/generate-roadmap` | ✅ Working |
| `curl POST /api/ai/knowledge-graph/query` | ✅ Working |

## 5. Risks

| Risk | Status |
|---|---|
| Keyword matching không chính xác | ✅ Mitigated — fallback search in course names |
| Career keyword set thiếu | ✅ Easy to expand |
| Query pattern không match | ✅ Fallback "Xin lỗi, tôi chưa hiểu câu hỏi..." |
