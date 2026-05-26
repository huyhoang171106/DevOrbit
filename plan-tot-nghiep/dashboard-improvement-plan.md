# Plan: Hoàn thiện Screen Tổng Quan (Dashboard)

> **Project**: devorbit-mobile
> **Date**: 2026-05-16
> **Status**: In Progress (Phase 1 — Data Layer)

---

## 1. Current State Analysis

### 1.1 What Exists

| File | Status | Notes |
|------|--------|-------|
| `DashboardScreen.kt` | ✅ Implemented | 5 sections: Health Gauge, Next Action, Metrics Row, Tasks, Recommendations |
| `AcademicViewModel.kt` | ✅ Implemented | Exposes courses/tasks flows, hardcoded `academicHealth = 0.85`, empty recommendations/nextAction |
| `MainScreen.kt` | ✅ Implemented | Wires DashboardScreen to AcademicViewModel |
| Domain Engines (9 files) | ✅ Implemented | BurnoutEngine, RiskEngine, WorkloadEngine, GpaEngine, RecommendationEngine, etc. |
| Domain Models | ✅ Implemented | Full model layer: AcademicHealth, RiskProfile, WorkloadProfile, BurnoutStatus, GpaImpact, etc. |
| GlassCard, Theme | ✅ Implemented | Design system + reusable components |

### 1.2 What's Missing / Broken

| Issue | Location | Impact |
|-------|----------|--------|
| `academicHealth` hardcoded = 0.85 | AcademicViewModel | Gauge shows fake data |
| MetricsRow GPA/Risk hardcoded | DashboardScreen | Always shows 3.85 / "THẤP" |
| `recommendations` always empty | AcademicViewModel | Copilot section invisible |
| `nextAction` always null | AcademicViewModel | ExecutionCard never shows |
| No burnout indicator | DashboardScreen | Missing health risk UX |
| No workload context | DashboardScreen | No heatmap/peak weeks |
| No semester timeline | DashboardScreen | No progress context |
| AcademicHealth model too thin | AcademicHealth.kt | Missing `burnout`, `workload`, `risk` fields |
| Theme references `GlassWhite`/`GlassBorder` directly | GlassCard.kt | Breaks if CosmicTheme changes; not using `CosmicTheme.colors.glass` |

### 1.3 Domain Engines Ready to Connect

| Engine | Input | Output | Dashboard Use |
|--------|-------|--------|---------------|
| BurnoutEngine | `List<LearningTask>` + nowMs | BurnoutStatus (riskLevel, indicators) | Burnout alert card |
| RiskEngine | subjects, overdueTasks, consistencyScores, weakPrereqs | RiskProfile (subjectRisks, overallRisk, riskFactors) | Risk badge in metrics row |
| WorkloadEngine | `List<LearningTask>` + semester params | WorkloadProfile (weeks, peakWeeks, status) | Workload heatmap |
| GpaEngine | currentGpa, `List<SubjectGpaImpact>` | GpaImpact (forecastGpa, recommendations) | GPA forecast in metrics row |
| RecommendationEngine | tasks, riskProfile, workloadProfile, burnoutStatus | `List<StudyRecommendation>` | Copilot recommendations section |
| TaskBreakdownEngine | goal, difficulty | TaskBreakdown + next BreakdownStep | Next Action card |
| KnowledgeGraphEngine | nodes, links, completedIds | impactScores, weakNodes, learningPath | Weak-spot indicators |
| StudyPlannerEngine | tasks, deadlines, availableHours | StudyPlan | Study plan integration |
| SimulationEngine | failedSubjectId, nodes, links | SimulationResult (blockedSubjects, delay) | Risk simulation preview |

---

## 1.4 Progress Update (2026-05-16)

| Item | Status | Notes |
|------|--------|-------|
| Domain model refactoring (model/ -> domain/model/) | ✅ Done | mobile-refactor-v2 moved all models |
| Engine files moved (model/domain -> domain/engine/) | ✅ Done | 9 engines relocated to clean architecture |
| DTO cleanup (model/ -> data/remote/dto/) | ✅ Done | 6 DTO files relocated |
| AcademicViewModel engine integration | 🔄 In Progress | Engines exist but not wired into AcademicHealth |
| DashboardScreen UI upgrades | 🔲 Pending | Phase 2 work not started |
| GlassCard theme integration | 🔲 Pending | Using CosmicTheme.colors.glass not implemented |
| Data layer (AuthViewModel, ExploreViewModel, ProfileViewModel) | ✅ Done | Auth + AuthInterceptor wired in DataModule |
| Repository layer (9 repos) | ✅ Done | AcademicRepository, AiRepositoryImpl, AuthRepositoryImpl, etc. |
| Unit tests (9 engine test files) | ✅ Done | Comprehensive engine test suite |
| Phase 3 (Animations & Polish) | 🔲 Pending | Not started |

---

## 2. Design Vision

### 2.1 Layout Blueprint

```
┌──────────────────────────────────┐
│       📊 ACADEMIC HEALTH         │  <- Real score from engines
│       ┌─────┐                    │
│       │  85 │  (pulse glow)      │
│       └─────┘                    │
│       Sức khỏe học tập: TỐT     │  <- Vietnamese label
│       Burnout: Thấp • 72%       │  <- Burnout indicator
├──────────────────────────────────┤
│  ▶️ THỰC THI TIẾP THEO          │  <- Connected to TaskBreakdownEngine
│  "Ôn chương 3 - Toán rời rạc"   │
│  [ Bắt đầu ngay ]               │
├──────────────────────────────────┤
│  ┌─────────┐ ┌─────────┐        │  <- Real data from engines
│  │ Dự báo  │ │ Rủi ro  │        │
│  │ GPA     │ │ Học vụ  │        │
│  │ 3.85    │ │ THẤP    │        │
│  └─────────┘ └─────────┘        │
│  ┌─────────┐ ┌─────────┐        │  <- NEW: Workload + Burnout
│  │ Khối    │ │ Burnout │        │
│  │ lượng   │ │         │        │
│  │ CÂN BẰNG│ │ AN TOÀN │        │
│  └─────────┘ └─────────┘        │
├──────────────────────────────────┤
│  🔥 BURNOUT WARNING (if any)     │  <- Conditional: only when risk detected
│  Phát hiện dấu hiệu burnout nhẹ │
│  • Tỉ lệ học khuya: 60%         │
│  • Deadline dồn: gấp 2.5 lần    │
├──────────────────────────────────┤
│  HÀNH TRÌNH HÔM NAY (${n} tasks) │  <- Dynamic count from tasks
│  ┌──────────────────────┐        │
│  │ ☐ Toan RR - Chuong 3 │▶️     │
│  └──────────────────────┘        │
│  ┌──────────────────────┐        │
│  │ ☐ LTHDT - Bai tap   │▶️     │
│  └──────────────────────┘        │
├──────────────────────────────────┤
│  GỢI Ý TỪ COPILOT               │  <- Connected to RecommendationEngine
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐      │
│  │ Ôn lại kiến thức nền  │      │
│  │ Toán rời rạc yêu cầu  │      │
│  │ kiến thức từ: Đại số  │      │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘      │
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐      │
│  │ Tập trung môn nguy cơ │      │
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘      │
└──────────────────────────────────┘
```

### 2.2 New Fields on AcademicHealth Model

```kotlin
data class AcademicHealth(
    val score: Double = 0.0,
    val recommendations: List<StudyRecommendation> = emptyList(),
    val nextAction: String = "",
    // NEW FIELDS:
    val gpaForecast: Double = 0.0,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val workloadStatus: WorkloadStatus = WorkloadStatus.BALANCED,
    val burnoutStatus: BurnoutStatus = BurnoutStatus(BurnoutRisk.NONE, emptyList()),
    val riskFactors: List<RiskFactor> = emptyList()
)
```

---

## 3. Implementation Plan

### Phase 1: Data Layer — Wire Engines into ViewModel

#### Task 1.1: Expand AcademicHealth model
- **File**: `domain/model/AcademicHealth.kt`
- **Change**: Add `gpaForecast`, `riskLevel`, `workloadStatus`, `burnoutStatus`, `riskFactors`
- **Validation**: Compiles, existing references auto-update via default params

#### Task 1.2: Integrate engines into AcademicViewModel
- **File**: `ui/viewmodel/AcademicViewModel.kt`
- **Changes**:
  1. Inject `AcademicRepository` (already injected)
  2. Use `courses` + `tasks` flows to derive:
     - `computeAcademicHealth()` using BurnoutEngine + RiskEngine + WorkloadEngine + GpaEngine
     - `computeRecommendations()` using RecommendationEngine
     - `computeNextAction()` using TaskBreakdownEngine
  3. Replace hardcoded `AcademicHealth(0.85, ...)` with engine-derived state
- **Data Flow**:
  ```
  API → Room (stale-while-revalidate) → ViewModel collect
       → combine(courses, tasks) {
           val burnout = BurnoutEngine.analyzeBurnout(tasks)
           val risk = RiskEngine.assessRisk(...)
           val workload = WorkloadEngine.analyzeWorkload(tasks)
           val gpa = GpaEngine.forecastGpa(...)
           val health = computeOverallScore(burnout, risk, workload, gpa)
           val recs = RecommendationEngine.generate(tasks, risk, workload, burnout)
           AcademicHealth(score=health, gpaForecast=gpa.forecastGpa,
                          riskLevel=risk.overallRisk, ...)
         }
  ```
- **Edge Cases**:
  - Empty tasks/courses → all engines return "no data" defaults
  - Partially loaded data → combine with null-safe defaults

#### Task 1.3: Compute overall health score
- **File**: `AcademicViewModel` or new `HealthScoreCalculator` utility
- **Formula** (proposed):
  ```
  health = (gpaScore * 0.25) 
          + (workloadScore * 0.20) 
          + (riskScore * 0.25) 
          + (burnoutScore * 0.20)
          + (consistencyScore * 0.10)
  ```
  Each component normalized 0.0–1.0. Final score 0.0–1.0 → multiplied by 10 for display.
- **Edge Cases**: Missing engines gracefully degrade to 0.5 (neutral)

---

### Phase 2: UI Layer — Upgrade Dashboard Components

#### Task 2.1: Upgrade AcademicHealthGauge
- **File**: `ui/screen/dashboard/DashboardScreen.kt`
- **Changes**:
  1. Show sub-metrics below the big score: Burnout indicator pill
  2. Color-coded: Green (≥7) / Yellow (4–6.9) / Red (<4)
  3. Animated score transitions when data refreshes
  4. Vietnamese label: "Sức khỏe học tập"
- **Edge Cases**: Score 0 → show "Chưa có dữ liệu"

#### Task 2.2: Replace MetricsRow with Dynamic 4-Card Row
- **File**: `DashboardScreen.kt`
- **Changes**:
  1. Remove hardcoded GPA/Risk cards
  2. Add 4 cards in 2×2 grid:
     - Dự báo GPA (from `academicHealth.gpaForecast`)
     - Rủi ro học vụ (from `academicHealth.riskLevel`)
     - Khối lượng (from `academicHealth.workloadStatus`)
     - Burnout (from `academicHealth.burnoutStatus.riskLevel`)
  3. Color-code each: good=aurora, warning=plasma/supernova mix
  4. Show Vietnamese labels everywhere
- **Edge Cases**: Missing data → show "—" vs "Không có dữ liệu"

#### Task 2.3: Add Conditional Burnout Alert Card
- **File**: `DashboardScreen.kt`
- **Changes**:
  1. New composable `BurnoutAlertCard(burnoutStatus)`
  2. Only visible when `burnoutStatus.riskLevel > NONE`
  3. Shows triggered indicators with progress bars
  4. Shows recommendation text
- **Edge Cases**: All indicators fine → card hidden entirely

#### Task 2.4: Update Section Headers for Vietnamese Consistency
- **File**: `DashboardScreen.kt`
- **Changes**: Verify all labels are in Vietnamese
- **Current**: "HÀNH TRÌNH HÔM NAY", "GỢI Ý TỪ COPILOT" ✅

#### Task 2.5: Fix GlassCard Theme Integration
- **File**: `ui/components/GlassCard.kt`
- **Changes**: Replace `GlassWhite`/`GlassBorder` references with `CosmicTheme.colors.glass` / `CosmicTheme.colors.glassBorder`
- **Why**: Avoids theme drift, enables runtime theme swapping (burnout mode)

---

### Phase 3: Polish & Animations

#### Task 3.1: Smooth Score Transitions
- Use `animateFloatAsState` on AcademicHealth score
- Animate between old/new values over 600ms ease

#### Task 3.2: Cards Enter Animation
- Use `AnimatedVisibility` with `slideInVertically` + `fadeIn` staggered
- Each card delays 50ms more than the previous

#### Task 3.3: Burnout Card Glow Effect
- Pulse animation when `BurnoutRisk >= MODERATE`
- Red glow border using `drawBehind`

---

## 4. File Change Summary

| File | Action | Complexity |
|------|--------|------------|
| `domain/model/AcademicHealth.kt` | Edit — add fields | 🟢 Easy |
| `ui/viewmodel/AcademicViewModel.kt` | Edit — integrate 5 engines | 🔴 Complex |
| `ui/screen/dashboard/DashboardScreen.kt` | Edit — upgrade 4 sections | 🔴 Complex |
| `ui/components/GlassCard.kt` | Edit — fix theme tokens | 🟢 Easy |
| No new files needed | — | — |

---

## 5. Dependencies & Ordering

```
Task 1.1 (AcademicHealth model)
    ↓
Task 1.2 (AcademicViewModel engines) ─── Task 2.5 (GlassCard fix)
    ↓                                       ↓
Task 1.3 (Health score formula)       (independent)
    ↓
Task 2.1 (Health Gauge upgrade) ─── Task 2.3 (Burnout alert card)
    ↓
Task 2.2 (Metrics Row 4-card) ─── Task 2.4 (Labels)
    ↓
Phase 3 (Animations & Polish)
```

- **Strict order**: 1.1 → 1.2 → 1.3 → 2.x (2.5 can run parallel)
- **Phase 3** can run in any order after 2.x

---

## 6. Validation Checklist

| Check | How |
|-------|-----|
| Engines return correct data | Run existing engine unit tests |
| ViewModel emits correct AcademicHealth | Manual test with mock repository |
| All Vietnamese labels render correctly | Visual inspection |
| Empty state shows gracefully | Clear Room DB → reload |
| Burnout card only shows when needed | Mock high burnout data |
| Score transitions animate smoothly | Visual inspection |
| No crash on config change | Rotate device / lifecycle test |
| Gradient/GlassCard theme consistent | Visual diff against DesignSystem.kt |

---

## 7. Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| Engine integration too heavy for ViewModel init | Use `stateIn` + `WhileSubscribed`; lazy compute |
| Room DB empty → all engines return 0 | Show "Chưa có dữ liệu" placeholders, not errors |
| Combine operator triggers recomposition too often | Add `debounce(300)` before emitting final state |
| Hardcoded GPA/Risk in MetricsRow (old code) | Fully replace, don't patch around |
