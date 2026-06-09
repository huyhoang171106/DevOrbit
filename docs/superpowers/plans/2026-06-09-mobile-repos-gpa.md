# Mobile Repos And GPA Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the mobile Course and Knowledge tabs with Repos and GPA screens.

**Architecture:** Reuse existing mobile course/repo data flows for the Repos screen, and add a small pure Kotlin GPA calculator for local GPA math. Keep Compose screens thin by putting calculation and route labels in focused classes that can be unit tested.

**Tech Stack:** Kotlin 2.0.21, Jetpack Compose Material 3, Hilt ViewModels, Gradle unit tests.

---

## File Structure

- Modify `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/navigation/Screen.kt` to replace `Courses` and `Knowledge`.
- Modify `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt` to route new screens.
- Create `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/screen/repos/ReposByCourseScreen.kt` for the course-to-repo browser.
- Create `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/domain/gpa/GpaCalculator.kt` for pure GPA math.
- Create `devorbit-mobile/app/src/test/java/vn/edu/uit/devorbit/mobile/domain/gpa/GpaCalculatorTest.kt` for TDD coverage.
- Create `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/screen/gpa/GpaScreen.kt` for Compose UI.
- Update `docs/TEST_MATRIX.md` and `devorbit-mobile/AGENTS.md`.

## Task 1: Navigation Replacement

**Files:**
- Test: `devorbit-mobile/app/src/test/java/vn/edu/uit/devorbit/mobile/ui/navigation/ScreenTest.kt`
- Modify: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/navigation/Screen.kt`
- Modify: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt`

- [ ] Write failing test asserting bottom nav includes `Dashboard`, `Repos`, `Explore`, `Plan`, `GPA`, `Profile` and excludes `Courses`, `Knowledge`.
- [ ] Run `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.ui.navigation.ScreenTest` and confirm failure.
- [ ] Add `Screen.navItems`, `Screen.Repos`, and `Screen.Gpa`.
- [ ] Replace hard-coded nav item list in `MainScreen.kt` with `Screen.navItems`.
- [ ] Run the targeted test and confirm pass.
- [ ] Commit: `feat(mobile): replace course knowledge tabs`

## Task 2: Repos By Course Screen

**Files:**
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/screen/repos/ReposByCourseScreen.kt`
- Modify: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt`
- Test: reuse `CourseHubNavigationStateTest` and `CourseSearchFilterStateTest` if present.

- [ ] Reuse existing `CourseViewModel` course search/filter and repo detail state.
- [ ] Implement a Repos screen that lists courses in compact rows and opens repo-focused course detail.
- [ ] Use existing `CourseDetailScreen` and `RepoDetailScreen` for detail pages.
- [ ] Route `Screen.Repos` to `ReposByCourseScreen`.
- [ ] Run `.\gradlew.bat :app:testDebugUnitTest`.
- [ ] Commit: `feat(mobile): browse repos by course`

## Task 3: GPA Domain Calculator

**Files:**
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/domain/gpa/GpaCalculator.kt`
- Create: `devorbit-mobile/app/src/test/java/vn/edu/uit/devorbit/mobile/domain/gpa/GpaCalculatorTest.kt`

- [ ] Write failing tests for weighted semester GPA, invalid rows, cumulative projection, required target term GPA, and infeasible target.
- [ ] Run `.\gradlew.bat :app:testDebugUnitTest --tests vn.edu.uit.devorbit.mobile.domain.gpa.GpaCalculatorTest` and confirm failure.
- [ ] Implement `GpaCourseInput`, `GpaSemesterResult`, `GpaCumulativeResult`, `GpaTargetResult`, and `GpaCalculator`.
- [ ] Run the targeted test and confirm pass.
- [ ] Commit: `feat(mobile): add gpa calculator domain`

## Task 4: GPA Screen UI

**Files:**
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/screen/gpa/GpaScreen.kt`
- Modify: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt`

- [ ] Build Compose UI with editable course rows for name, credits, grade.
- [ ] Add controls for current GPA, completed credits, and target GPA.
- [ ] Display semester GPA, valid credits, projected cumulative GPA, and target required GPA.
- [ ] Route `Screen.Gpa` to `GpaScreen`.
- [ ] Run `.\gradlew.bat :app:assembleDebug`.
- [ ] Commit: `feat(mobile): add gpa calculator screen`

## Task 5: Docs And Final Verification

**Files:**
- Modify: `docs/TEST_MATRIX.md`
- Modify: `devorbit-mobile/AGENTS.md`

- [ ] Add a test matrix row for the mobile repos and GPA screens.
- [ ] Update mobile AGENTS key screens if needed.
- [ ] Run `.\gradlew.bat :app:testDebugUnitTest`.
- [ ] Run `.\gradlew.bat :app:assembleDebug`.
- [ ] Commit: `docs: record mobile repos gpa proof`
