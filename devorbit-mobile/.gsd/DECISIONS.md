# DECISIONS.md

# ADR-001: Mobile Rebuild Approach
**Date**: 2026-05-12
**Status**: Accepted (Superseded in part by actual implementation)

**Context**: The existing mobile app needed a structural overhaul to support the new "Academic OS" vision.

## Phase 1 Decisions: Academic OS Architecture

**Date:** 2026-05-12

### 1. Navigation & Navigation (OS Foundation)
- **Planned**: 5-pillar Bottom Navigation Bar (Dashboard, Courses, Progress, Execution, AI Copilot).
- **Actual**: Bottom navigation with: Dashboard, Explore, Courses, Knowledge, Profile. Progress/Execution/Copilot tab screens not yet built; their engines exist.
- **Note**: Study Planner, Focus Mode, Syllabus Parser, Task Breakdown are accessible via navigation from Dashboard/Courses rather than as separate bottom tabs.
- **Dashboard**: acts as "Mission Control" with widgets (implemented).

### 2. User Experience (Discovery vs. Productivity)
- **Implemented**: Primary List View (CourseListScreen) with search-first UX (search bar, semester filter chips).
- **Implemented**: "Galaxy" toggle — integrated GalaxyGraphCanvas as secondary Explore mode.
- **Philosophy**: List for daily productivity; Galaxy for occasional exploration.

### 3. Engineering (Data Flow)
- **Implemented**: Offline-first using Room DB (DevOrbitDatabase with 4 entities).
- **Implemented**: API → Room → UI (stale-while-revalidate via AcademicRepository).
- **Room setup**: Completed in Phase 1 with CourseDao, RepoDao, RelationshipDao, TaskDao.

### 4. Aesthetics (Visual Identity)
- **Style**: "Professional Intelligence System" (Linear + Notion + Raycast).
- **Implemented**: Dark-mode-first, subtle galaxy gradients achieved via CosmicBackground composable, GalaxyGraphCanvas, GlassCard, and cosmic theme (Color.kt, DesignSystem.kt, Theme.kt).
- **Avoided**: Neon overload, cyberpunk — theme uses muted galaxy palette.

## After-Action Notes (2026-05-16)

### What Changed From Plan
1. **Screen structure diverged from 5-pillar plan**: Navigation ended up as Dashboard/Explore/Courses/Knowledge/Profile tabs rather than the originally planned Dashboard/Courses/Progress/Execution/Copilot. The 3 remaining pillars (Progress, Execution, Copilot) have their engines built but lack dedicated tab UI.
2. **Login/Register removed**: Combined into a single AuthScreen.
3. **Separate RepositoryModule**: Originally planned as single DI layer; split into DataModule (network/DB) + RepositoryModule (repos) + RepositoryModule in data/di/.
4. **Login/Register removed**: `LoginScreen.kt` and `RegisterScreen.kt` were removed; replaced by `AuthScreen.kt`.

### What Was Preserved
- Room offline-first architecture
- Repository pattern with stale-while-revalidate
- Compose + Material 3 UI
- Hilt dependency injection
- Dark-mode-first cosmic aesthetic
- Search-first UX in course hub

**Decision**: Rebuild the mobile app focusing on 4 core modules: Course Hub, PTB, Smart Todo, and AI Engine.
**Consequences**: Existing screens were refactored to align with the new vision. The 5-pillar nav plan was adjusted during execution to match actual development priorities.
