# Milestones: Hoàn thiện tích hợp LLM trong Knowledge Graph

Free-form implementation log. Record meaningful phase changes, successful milestones, failed attempts, setbacks, fixes, validation notes, and decisions.

**Current Status:** All milestones completed ✅

### 2026-05-14 17:18:13 - Milestone

Kanban Board knowledge graph — replaced force-graph-only view with dual-mode page: (1) Kanban Board with 8 semester columns + Unassigned column, drag-and-drop course cards via @dnd-kit, career orientation picker with 9 career paths showing recommended courses highlighted in the board. (2) Original ForceGraph2D simulation view preserved via toggle. New files: CareerOrientationData.ts (career paths + semester metadata), KanbanBoard.tsx (DndContext orchestrator), KanbanColumn.tsx (droppable column), KanbanCard.tsx (sortable card). Modified: GalaxyPage.tsx — toolbar with view toggle + career orientation dropdown. Build compiles cleanly (287KB GalaxyPage chunk).

### 2026-05-14 17:26:38 - Milestone

Fixed career orientation "0 môn học được đề xuất" bug — the knowledge graph API only returns 25 mandatory KTPM courses, while career-recommended courses are all electives filtered out of the graph. Added useCourseList hook that fetches all courses from /api/courses + merge logic in GalaxyPage that creates synthetic GraphNode entries for career-recommended electives missing from the graph. KanbanBoard now receives merged node list (graph + extra) so recommended course count and card highlighting work correctly.

### 2026-05-14 17:37:41 - Milestone

Kanban Board full upgrade — all 5 improvements implemented: (1) Credit display per card + per-column total with capacity bar (color-coded: green ≤15, amber 15-18, red >18). (2) Prerequisite warning — card turns red border + "Pre-req sai kỳ" badge when prerequisite course is placed in a later semester. (3) Auto-arrange algorithm — Kahn topological sort → level-based semester assignment respecting credit limit (18TC/semester), prerequisite ordering, and career priority. (4) Save/Load — localStorage persistence with toast confirmations + unsaved-changes indicator. (5) Pre-req chain display — up to 3 prerequisite course codes shown as chips below each card. Backend change: added credits field to CourseSummaryResponse + JPQL query.

### 2026-05-14 17:57:01 - Milestone

Integrated KTPM curriculum JSON + DAA course catalog into Kanban auto-arrange algorithm. Generated curriculumData.ts from authoritative sources. Changed auto-arrange to first assign fixed-semester courses from KTPM JSON, then algorithmically place remaining flexible courses (electives, career-recommended courses). Added DAA "before" course relationships as supplementary prerequisite edges.

### 2026-05-14 18:07:40 - Milestone

Applied curriculum constraints: SS courses (SS003-SS010) flexible with min semester 2; career-recommended elective courses enforced to min semester 4. Non-career flexible courses use topological-level-based placement.

### 2026-05-14 18:12:59 - Milestone

Made ENG01/ENG02/ENG03 flexible — removed from fixed-semester map since placement depends on English entrance exam results. Prerequisite chain (ENG01→ENG02→ENG03) still enforced via curriculum data.

### 2026-05-14 18:19:44 - Milestone

Added English level selector button in Kanban action bar — dropdown with 4 options: all levels, skip ENG01, skip ENG01-02, skip all ENG. Uses Phosphor Icons, amber accent when non-default level is active.

### 2026-05-14 18:27:54 - Milestone

Replaced 9 arbitrary career orientations with official KTPM elective curriculum groups from section 3.4.2: "Phát triển phần mềm" and "Môi trường ảo và Game" options with "Nhóm chung" 13 elective courses. Added per-course ELECTIVE_CREDITS map. Added total credits bar above Kanban columns.

### 2026-05-14 18:43:16 - Milestone

Optimized semester 8 with graduation path selector. Added "Tốt nghiệp" button with 3 options: Khóa luận tốt nghiệp (SE505, 10TC), Đồ án tại doanh nghiệp (SE506, 10TC), Đồ án + Chuyên đề (SE507 6TC + chosen specialty 4TC). Specialty sub-selector shows 6 courses. Auto-arrange skips non-selected graduation courses.

### 2026-05-14 19:10:06 - Milestone

Replaced single-orientation career picker with individual elective course selector. New "Môn tự chọn" button opens a grouped checkbox panel showing 3 sections: Nhóm chung (13 courses), Phát triển phần mềm (11 courses), Môi trường ảo và Game (10 courses). Credit counter shows total vs 16 TC minimum.

### 2026-05-14 19:15:15 - Milestone

HK8 now starts empty — graduation courses (SE505/SE506/SE507) and specialty courses default to null. Courses only populate in HK8 when user selects a graduation path or elective courses.

### 2026-05-14 19:20:53 - Milestone

Fixed graduation courses and specialty courses still appearing in Kanban when no graduation path selected. Added hiddenCodes filter to columns useMemo. Fixed credit capacity bar display for unassigned column.

### 2026-05-14 19:40:50 - Milestone

Added "Cơ sở ngành" (section 3.4.1) elective group with 12 courses (SE359-SE117) requiring minimum 12TC. Elective picker now shows 4 groups with "Chọn nhanh" buttons. Credit summary panel shows split breakdown.

### 2026-05-14 19:57:13 - Milestone

Restructured elective picker with thematic sub-groups based on course descriptions. Added COURSE_NAMES map with 50+ course names. Sub-group headers with "Chọn tất cả" per group.

### 2026-05-14 20:09:00 - Milestone

Added prerequisite validation on elective course selection. When user clicks a course in the elective picker, checks DAA 'Mã môn học trước' relationships for unmet prerequisites. Shows spring-animated warning popup.

### 2026-05-15 — AI Scenario Engine Backend Complete

Backend AI Scenario Engine completed with 5 generators:
- SummaryGenerator, AdviceGenerator, RoadmapGenerator, GraphQueryEngine, CurriculumMatcher
- All deterministic, zero LLM dependency, Vietnamese output
- Public endpoints: `/api/ai/repo/{id}/summary`, `/api/ai/repo/{id}/advice`, `/api/ai/generate-roadmap`, `/api/ai/knowledge-graph/query`

---

**Spec completion notes:**
- The frontend refactor (useAiRoadmap → backend call, useAiGraphQuery, delete ai.ts) from TECH.md was NOT completed, pending full Scenario Engine frontend integration.
- AI SDK (`@ai-sdk/openai-compatible`, `ai`) and `VITE_LLM_API_KEY` remain in the frontend config but are no longer used by default.
