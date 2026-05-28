# PRODUCT.md — Hoàn thiện tích hợp AI trong Knowledge Graph

> **Last Updated:** 2026-05-16
> **Status:** IMPLEMENTED — Backend Scenario Engine completed, Kanban Board completed

## 1. Vấn đề & Bối cảnh

Hiện tại, DevOrbit có AI features không đồng nhất và phụ thuộc vào template cứng:

| Thành phần | Trạng thái | Cách hoạt động |
|---|---|---|
| Backend `AiService.java` | ✅ Scenario Engine | Delegate to 5 specialized generators — deterministic, graph-aware |
| Frontend `useAiRoadmap.ts` | ⚠️ Still uses LLM SDK | Legacy implementation; should be refactored to call backend |
| Backend AI endpoints | ✅ 4 endpoints | Summary, Advice, Roadmap, KG Query — all public |

## 2. Giải pháp: Scenario Generation Engine (IMPLEMENTED ✅)

Thay vì dùng LLM (tốn phí, cần API key, không deterministic), ta xây **ScenarioEngine** tận dụng data có sẵn trong knowledge graph để gen ra nội dung thông minh:

- **Knowledge graph data**: topological levels, impact scores, prerequisites, semesters
- **Repository metadata**: description, tech stack, stars, course liên kết
- **Curriculum data**: mandatory codes, elective categories

## 3. AI Features & Behavior (IMPLEMENTED ✅)

### 3.1. Repository Summary
**Endpoint:** `GET /api/ai/repo/{repoId}/summary` ✅

### 3.2. Tutor Advice
**Endpoint:** `GET /api/ai/repo/{repoId}/advice` ✅
- Impact-score aware, downstream course count

### 3.3. Roadmap Generation
**Endpoint:** `POST /api/ai/generate-roadmap` ✅
- Keyword matching, topological sorting, mandatory + elective classification

### 3.4. Knowledge Graph Query (NEW)
**Endpoint:** `POST /api/ai/knowledge-graph/query` ✅
- Pattern matching for natural language queries
- Fallback search in course names

## 4. Behavior Invariants (MAINTAINED ✅)

1. **Zero LLM dependency** — không gọi API ngoài, không cần API key ✅
2. **Deterministic** — cùng input → cùng output ✅
3. **Endpoints giữ nguyên contract** — web + mobile không đổi ✅
4. **Tiếng Việt** — tất cả responses bằng tiếng Việt ✅
5. **Graph-aware** — mọi response đều dùng graph structure ✅

## 5. Non-goals (MAINTAINED ✅)

- Không thay đổi schema database ✅
- Không thêm UI mới ✅
- Không caching phức tạp ✅

## 6. What Remains

| Item | Status | Description |
|------|--------|-------------|
| Frontend refactor (useAiRoadmap) | ⏳ Pending | Should call backend instead of LLM directly |
| Frontend (useAiGraphQuery) | ⏳ Pending | New hook to call `/api/ai/knowledge-graph/query` |
| Delete lib/ai.ts | ⏳ Pending | Remove legacy LLM config file |
| Remove ai SDK deps | ⏳ Pending | Clean up package.json dependencies |
