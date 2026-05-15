# Milestones: Hoàn thiện tích hợp LLM trong Knowledge Graph

Free-form implementation log. Record meaningful phase changes, successful milestones, failed attempts, setbacks, fixes, validation notes, and decisions. Use third-level headings with timestamps down to seconds, for example `### 2026-05-13 14:16:36 - Short milestone title`. No strict schema is required.


### 2026-05-14 17:18:13 - Milestone

Kanban Board knowledge graph — replaced force-graph-only view with dual-mode page: (1) Kanban Board with 8 semester columns + Unassigned column, drag-and-drop course cards via @dnd-kit, career orientation picker with 9 career paths showing recommended courses highlighted in the board. (2) Original ForceGraph2D simulation view preserved via toggle. New files: CareerOrientationData.ts (career paths + semester metadata), KanbanBoard.tsx (DndContext orchestrator), KanbanColumn.tsx (droppable column), KanbanCard.tsx (sortable card). Modified: GalaxyPage.tsx — toolbar with view toggle + career orientation dropdown. Build compiles cleanly (287KB GalaxyPage chunk).

### 2026-05-14 17:26:38 - Milestone

Fixed career orientation "0 môn học được đề xuất" bug — the knowledge graph API only returns 25 mandatory KTPM courses, while career-recommended courses (SE330, SE347, IE104, etc.) are all electives filtered out of the graph. Added useCourseList hook that fetches all courses from /api/courses + merge logic in GalaxyPage that creates synthetic GraphNode entries for career-recommended electives missing from the graph. KanbanBoard now receives merged node list (graph + extra) so recommended course count and card highlighting work correctly. Build compiles cleanly.

### 2026-05-14 17:37:41 - Milestone

Kanban Board full upgrade — all 5 improvements implemented: (1) Credit display per card + per-column total with capacity bar (color-coded: green ≤15, amber 15-18, red >18). (2) Prerequisite warning — card turns red border + "Pre-req sai kỳ" badge when prerequisite course is placed in a later semester, shows prerequisite course codes below card name. (3) Auto-arrange algorithm — Kahn topological sort → level-based semester assignment respecting credit limit (18TC/semester), prerequisite ordering, and career priority (recommended courses placed earlier). (4) Save/Load — localStorage persistence with toast confirmations + unsaved-changes indicator. (5) Pre-req chain display — up to 3 prerequisite course codes shown as chips below each card. Backend change: added credits field to CourseSummaryResponse Java record + JPQL query + CourseSummary TS type.

### 2026-05-14 17:57:01 - Milestone

Integrated KTPM curriculum JSON + DAA course catalog into Kanban auto-arrange algorithm:

- Generated **curriculumData.ts** from two authoritative sources: the KTPM curriculum JSON (38 courses across 8 semesters with exact semester assignments and prerequisites) and the DAA university course catalog (1000+ courses with credits + 322 "before" course relationships).
- Changed **auto-arrange** in KanbanBoard to first assign fixed-semester courses from the KTPM JSON (IT001 → HK1, IT002 → HK2, SE104 → HK4, etc.), then algorithmically place remaining flexible courses (electives, career-recommended courses) around them — respecting credit caps and prerequisite ordering.
- Added DAA "before" course relationships as supplementary prerequisite edges in the topological sort, improving ordering for non-JSON courses.
- Updated credit map in GalaxyPage to use DAA curriculum credits as fallback when backend API credits unavailable.
- Curriculum data module is self-contained (no API dependency), enabling offline auto-arrange.

### 2026-05-14 18:07:40 - Milestone

Applied curriculum constraints to auto-arrange: SS courses (SS003-SS010) now flexible with min semester 2 (removed from fixed-semester map); career-recommended elective courses (Cơ sở ngành/Chuyên ngành tự chọn) enforced to min semester 4. Non-career flexible courses continue using topological-level-based placement. Cleaned up dead arrays from algorithm implementation.

### 2026-05-14 18:12:59 - Milestone

Made ENG01/ENG02/ENG03 flexible — removed from fixed-semester map since placement depends on English entrance exam results. Prerequisite chain (ENG01→ENG02→ENG03) still enforced via curriculum data so they auto-arrange in correct order without hard-locked semesters.

### 2026-05-14 18:19:44 - Milestone

Added English level selector button in Kanban action bar — dropdown with 4 options: Anh văn đầy đủ (all), Bắt đầu từ AV2 (skip ENG01), Bắt đầu từ AV3 (skip ENG01-02), Đã vượt qua AV (skip all ENG). Selected level feeds into auto-arrange algorithm: skipped ENG courses are excluded from semester assignment and drop to 'Chưa xếp' column. Uses Translate + CheckCircle icons from @phosphor-icons/react, amber accent when non-default level is active.

### 2026-05-14 18:27:54 - Milestone

Replaced 9 arbitrary career orientations with official KTPM elective curriculum groups from section 3.4.2: 2 orientation options — "Phát triển phần mềm" (common + 11 software dev courses) and "Môi trường ảo và Game" (common + 10 game/VR courses), both sharing "Nhóm chung" 13 elective courses. Added per-course ELECTIVE_CREDITS map for credit lookup. Added total credits bar above Kanban columns showing cumulative credits including ENG courses (even when skipped — passed AV credits still count toward degree total). When English level ≠ 'all', shows amber indicator "(+12 TC Anh văn đã tính)".

### 2026-05-14 18:43:16 - Milestone

Optimized semester 8 with graduation path selector. Added "Tốt nghiệp" button in Kanban action bar showing 3 options: Khóa luận tốt nghiệp (SE505, 10TC), Đồ án tại doanh nghiệp (SE506, 10TC), Đồ án + Chuyên đề (SE507 6TC + chosen specialty 4TC). Specialty sub-selector shows 6 courses (SE400/SE406/SE403/SE407/SE408/SE409). Auto-arrange skips non-selected graduation courses in step 2 to prevent credit overcount in HK8. Added SE502/SE503/SE505/SE506/SE507 credits to DAA_CREDITS. GalaxyPage kanbanNodes now always includes fixed-semester + graduation course codes as synthetic nodes. Exported getFixedSemesterCodes() helper.

### 2026-05-14 19:10:06 - Milestone

Replaced single-orientation career picker with individual elective course selector. New "Môn tự chọn" button opens a grouped checkbox panel showing 3 sections: Nhóm chung (13 courses), Phát triển phần mềm (11 courses), Môi trường ảo và Game (10 courses). Each course has a checkbox with credit display. Credit counter shows total vs 16 TC minimum (green when met, red when below). Users pick courses individually — not forced to select entire orientation. KanbanBoard receives selectedElectiveCodes (Set<string>) instead of selectedCareer, auto-arrange careerCodeSet derives directly from user selections, action bar banner shows "X môn đã chọn · Y TC (tối thiểu 16 TC)". Only checked courses appear in Kanban board.

### 2026-05-14 19:15:15 - Milestone

HK8 now starts empty — graduation courses (SE505/SE506/SE507) and specialty courses default to null in both initial semesterMap and new-node sync useEffect. Courses only populate in HK8 when user selects a graduation path ("Tốt nghiệp") or elective courses via "Môn tự chọn". Step 5 explicitly nullifies non-matching graduation/specialty codes even if n.semester fallback would place them. Combined declarations hoisted to avoid TS2451.

### 2026-05-14 19:20:53 - Milestone

Fixed graduation courses (SE505/506/507) and specialty courses still appearing in Kanban when no graduation path is selected. Added hiddenCodes filter to columns useMemo that completely hides these codes from ALL columns (including "Chưa xếp") when graduationPath is null. Fixed KanbanColumn to not display credit capacity bar or "Quá tải" warning for the unassigned (null) column, since it's a holding area without credit limits.

### 2026-05-14 19:40:50 - Milestone

Added "Cơ sở ngành" (section 3.4.1) elective group with 12 courses (SE359-SE117) requiring minimum 12TC. Elective picker now shows 4 groups: Cơ sở ngành (≥12TC), Nhóm chung, Phát triển phần mềm, Môi trường ảo và Game. Each group has "Chọn nhanh" button (cơ sở ngành pre-selects 4×3TC=12TC; others select all courses). Credit summary panel shows split: Cơ sở ngành X/12TC · Chuyên ngành Y/16TC · Tổng Z TC. KanbanBoard banner also shows split credit bars. Added DAA_BEFORE entries for SE116, SE359, SE360 with empty prerequisite arrays.

### 2026-05-14 19:57:13 - Milestone

Restructured elective picker with thematic sub-groups based on course descriptions. Cơ sở ngành split into 5 sub-groups (Game & Đồ họa, Cloud & DevOps, Mobile & Nhúng, Chất lượng & Quản lý, HCI & Kỹ thuật); Nhóm chung into 4 sub-groups; PT PM into 3 sub-groups; Game into 3 sub-groups. Added COURSE_NAMES map with 50+ course names. GalaxyPage picker now loops over ELECTIVE_SECTION_GROUPS, rendering sub-group headers with "Chọn tất cả" per group. Total credits label changed to "Tổng TC đã xếp" for clarity (running sum of placed courses across all semesters, not including HK8 if empty).

### 2026-05-14 20:09:00 - Milestone

Added prerequisite validation on elective course selection. When user clicks a course in the elective picker, checks getCurriculumPrereqs() (DAA 'Mã môn học trước' relationships) for unmet prerequisites. Filters out mandatory courses (always in curriculum) and already-selected electives. Shows spring-animated warning popup with WarningCircle icon listing missing prerequisite course codes and names, with 'Huỷ' and 'Vẫn chọn' buttons. Uses AnimatePresence for smooth open/close transitions.
