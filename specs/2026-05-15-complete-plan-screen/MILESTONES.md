# Milestones: Complete Plan Screen

Implementation log for completing the DevOrbit Mobile Plan screen.

> **Current Status:** Plan created; implementation not yet started

### 2026-05-15 22:00:00 — Plan created

Đã analyse toàn bộ codebase, xác định gaps:

**Current state**: PlanTabView là stub hoàn toàn (`studyPlan = null`, `{ /* TODO */ }`).
**Resources available**: 3 screens đã viết (StudyPlanner, SyllabusParser, TaskBreakdown), 2 engines đã test (StudyPlanner, TaskBreakdown), GpaEngine + models, Flashcard domain model.

**Plan**: 5 phases (A→F), 9 files mới, 5 files sửa.

**Quyết định**:
- SyllabusParserScreen giữ nguyên (non-goal per spec v2)
- PlanViewModel là single ViewModel cho cả Plan tab
- Flashcard persist qua Room (entity mới) thay vì in-memory
- Sub-navigation dùng TabRow + HorizontalPager (Material 3 foundation pager)

### NOT YET IMPLEMENTED

The following files from the PLAN.md have NOT yet been created:

| # | File | Status |
|---|---|---|
| 1 | `ui/viewmodel/PlanViewModel.kt` | ❌ Not created |
| 2 | `ui/screen/plan/PlanTabHost.kt` | ❌ Not created |
| 3 | `ui/screen/plan/GpaForecastScreen.kt` | ❌ Not created |
| 4 | `ui/screen/plan/FlashcardScreen.kt` | ❌ Not created |
| 5 | `domain/engine/FlashcardEngine.kt` | ❌ Not created |
| 6 | `data/local/entity/FlashcardEntity.kt` | ❌ Not created |
| 7 | `data/local/entity/FlashcardDeckEntity.kt` | ❌ Not created |
| 8 | `data/local/dao/FlashcardDao.kt` | ❌ Not created |

And the following files have NOT been modified:
| # | File | Status |
|---|---|---|
| 1 | `ui/MainScreen.kt` — PlanTabView wiring | ❌ Not updated |
| 2 | `ui/screen/plan/StudyPlannerScreen.kt` — PlanViewModel wiring | ❌ Not updated |
| 3 | `ui/screen/plan/TaskBreakdownScreen.kt` — PlanViewModel wiring | ❌ Not updated |
| 4 | `data/local/DevOrbitDatabase.kt` — Flashcard entities | ❌ Not updated |
