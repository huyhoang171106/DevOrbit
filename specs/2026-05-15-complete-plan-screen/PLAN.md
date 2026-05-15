# Plan: Hoàn thiện Plan Screen — DevOrbit Mobile

> **Last Updated:** 2026-05-16
> **Status:** PLAN CREATED — NOT YET IMPLEMENTED

**Ngày**: 2026-05-15
**Mục tiêu**: Biến Plan tab từ stub (`studyPlan = null`, `{ /* TODO */ }`) thành hub đầy đủ với 4 sub-feature: Study Planner, Task Breakdown, GPA Forecast, Flashcards.

---

## Hiện trạng

| Thành phần | Trạng thái | Chi tiết |
|---|---|---|
| `PlanTabView()` trong MainScreen.kt | 🔴 Stub | `StudyPlannerScreen(studyPlan = null, onGeneratePlan = { /* TODO */ }, ...)` |
| `StudyPlannerScreen.kt` | 🟡 UI OK, data null | Giao diện đẹp nhưng không có dữ liệu |
| `SyllabusParserScreen.kt` | 🟢 UI OK | Đã viết xong; non-goal trong spec v2 |
| `TaskBreakdownScreen.kt` | 🟡 UI OK, disconnected | Engine đã có, chưa gắn navigation |
| `StudyPlannerEngine.kt` | 🟢 Done + tested | 14 unit tests PASS |
| `TaskBreakdownEngine.kt` | 🟢 Done + tested | 10 unit tests PASS |
| `GpaEngine.kt` | 🟢 Done + tested | 10 unit tests PASS |
| `GpaImpact` models | 🟢 Done | domain/model/GpaImpact.kt |
| `Flashcard` / `FlashcardDeck` models | 🟢 Done | domain/model/Flashcard.kt |
| `FlashcardEngine` | 🔴 Chưa có | Cần viết |
| `GpaForecastScreen` | 🔴 Chưa có | Cần tạo |
| `FlashcardScreen` | 🔴 Chưa có | Cần tạo |
| `PlanViewModel` | 🔴 Chưa có | Cần tạo |
| Plan sub-navigation | 🔴 Chưa có | Cần 4 tab nhỏ trong Plan |

---

## Kiến trúc Plan Screen hoàn chỉnh (TARGET)

```
MainScreen
 └─ PlanTabView                      ← NavigationSuiteScaffold item
     └─ PlanTabHost                  ← Sub-navigation: 4 tabs
         ├─ StudyPlannerScreen       ← Danh sách phases, generate plan
         ├─ TaskBreakdownScreen      ← Nhập goal + difficulty, breakdown steps
         ├─ GpaForecastScreen        ← GPA hiện tại, forecast, subject impact
         └─ FlashcardScreen          ← Deck list, study mode, create card
              └─ PlanViewModel       ← State cho tất cả sub-feature
```

---

## Files cần tạo (9 files)

| # | File | Pha | Mô tả |
|---|---|---|---|
| 1 | `ui/viewmodel/PlanViewModel.kt` | A | State holder cho 4 sub-feature |
| 2 | `ui/screen/plan/PlanTabHost.kt` | A | Sub-navigation: 4 tabs ngang |
| 3 | `ui/screen/plan/GpaForecastScreen.kt` | D | GPA forecast UI |
| 4 | `ui/screen/plan/FlashcardScreen.kt` | E | Flashcard deck list + study mode |
| 5 | `domain/engine/FlashcardEngine.kt` | E | SM-2 spaced repetition engine |
| 6 | `data/local/entity/FlashcardEntity.kt` | E | Room entity cho flashcard |
| 7 | `data/local/entity/FlashcardDeckEntity.kt` | E | Room entity cho deck |
| 8 | `data/local/dao/FlashcardDao.kt` | E | Room DAO cho flashcard |

## Files cần sửa (5 files)

| # | File | Pha | Thay đổi |
|---|---|---|---|
| 1 | `ui/MainScreen.kt` | A | PlanTabView → inject PlanViewModel |
| 2 | `ui/screen/plan/StudyPlannerScreen.kt` | B | Nhận PlanViewModel thay vì props rời |
| 3 | `ui/screen/plan/TaskBreakdownScreen.kt` | C | Nhận PlanViewModel thay vì props rời |
| 4 | `data/local/DevOrbitDatabase.kt` | E | Thêm FlashcardEntity, FlashcardDeckEntity, FlashcardDao |

---

## Timeline gợi ý

| Bước | Task | Thời gian |
|---|---|---|
| A-1 | Tạo PlanViewModel | ~1 session |
| A-2 | Tạo PlanTabHost + update MainScreen | ~1 session |
| B-1 | Wire StudyPlannerScreen | ~1 session |
| C-1 | Wire TaskBreakdownScreen | ~1 session |
| D-1 | Tạo GpaForecastScreen | ~1 session |
| E-1 | Tạo FlashcardEngine | ~1 session |
| E-2 | Tạo FlashcardScreen + entities | ~1 session |
| F-1 | Integration, states, test build | ~1 session |
| **Tổng** | | **~8 sessions** |
