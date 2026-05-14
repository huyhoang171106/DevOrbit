# PRODUCT.md — Hoàn thiện tích hợp AI trong Knowledge Graph

## 1. Vấn đề & Bối cảnh

Hiện tại, DevOrbit có AI features không đồng nhất và phụ thuộc vào template cứng:

| Thành phần | Trạng thái | Cách hoạt động |
|---|---|---|
| Backend `AiService.java` | ⚠️ Template-based | Dùng `String.format()` — nội dung cứng, thiếu linh hoạt |
| Frontend `useAiRoadmap.ts` | ⚠️ Gọi LLM từ browser | Phụ thuộc API key, lộ trên client |
| Backend AI endpoints | ⚠️ Thiếu thông minh | Summary, advice không thực sự hữu ích |

## 2. Giải pháp: Scenario Generation Engine

Thay vì dùng LLM (tốn phí, cần API key, không deterministic), ta xây **ScenarioEngine** tận dụng data có sẵn trong knowledge graph để gen ra nội dung thông minh:

- **Knowledge graph data**: topological levels, impact scores, prerequisites, semesters
- **Repository metadata**: description, tech stack, stars, course liên kết
- **Curriculum data**: mandatory codes, elective categories

## 3. AI Features & Behavior

### 3.1. Repository Summary
**Endpoint:** `GET /api/ai/repo/{repoId}/summary`

**Behavior:**
- Dùng `GithubRepo` metadata + `Course` context để gen summary
- Format: `"Repository [name] thuộc môn [course] (HK[semester]). Sử dụng [language] với [stars] sao. Repository này hỗ trợ kiến thức cho: [downstream courses]."`
- Chú trọng giá trị học thuật cho sinh viên

### 3.2. Tutor Advice
**Endpoint:** `GET /api/ai/repo/{repoId}/advice`

**Behavior:**
- Dùng `impactScore` từ `KnowledgeGraphService`
- Nếu impact > ngưỡng: cảnh báo + lời khuyên tập trung
- Gợi ý cách học: fork, đọc source, làm bài tập
- Dùng simulation data nếu có (trượt → block bao nhiêu môn)

### 3.3. Roadmap Generation
**Endpoint:** `POST /api/ai/generate-roadmap`

**Behavior:**
- User gửi `learningGoals` + `careerPath`
- Engine match keywords với course names/descriptions
- Sắp xếp theo topological level (không đề xuất môn chưa đủ prerequisite)
- Phân loại mandatory (tự thêm) vs elective (match từ careerPath)
- Gen reasoning theo từng môn

### 3.4. Knowledge Graph Query (NEW)
**Endpoint:** `POST /api/ai/knowledge-graph/query`

**Behavior:**
- User hỏi về graph bằng ngôn ngữ tự nhiên
- Engine parse câu hỏi bằng keyword patterns
- Hỗ trợ: "Môn nào tiên quyết cho [code]?", "Học sau [code]?", "Tác động nếu trượt [code]?"
- Trả về answer + relevant node IDs để frontend highlight

## 4. Behavior Invariants

1. **Zero LLM dependency** — không gọi API ngoài, không cần API key
2. **Deterministic** — cùng input → cùng output (dễ test, dễ demo)
3. **Endpoints giữ nguyên contract** — web + mobile không đổi
4. **Tiếng Việt** — tất cả responses bằng tiếng Việt
5. **Graph-aware** — mọi response đều dùng graph structure

## 5. Non-goals

- Không thay đổi schema database
- Không thêm UI mới
- Không caching phức tạp (response gần như free)
