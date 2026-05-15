# ROADMAP.md

> **Current Phase**: Phase 1: Foundation
> **Milestone**: v1.0 (MVP Rebuild)

## Must-Haves (from SPEC)
- [x] Course Repo Hub (Search & Discovery)
- [ ] PTB Tracker (Credits & Prerequisites) — engines built, UI pending
- [ ] Smart Todo (Deadline clusters & AI planning) — engines built, UI pending
- [ ] AI Recommendations (Roadmaps & Risk) — engines built, UI pending

## Phases

### Phase 1: Foundation & Course Hub (Offline-First)
**Status**: ✅ Complete (Plans 1.1-1.4 executed)
**Objective**: Establish the 5-pillar "Academic OS" architecture and implement the Room DB caching engine.
**Deliverables**:
- ✅ **Room Database**: DevOrbitDatabase with 4 entities (Course, Repo, Relationship, Task), DAOs
- ✅ **Network Layer**: Retrofit + OkHttp with ApiService, AuthInterceptor
- ✅ **DI**: Hilt DataModule (Retrofit, OkHttp, Room, Repository injection) + RepositoryModule
- ✅ **Course Hub**: CourseHubScreen + CourseListScreen + CourseDetailScreen + RepoDetailScreen
- ✅ **Knowledge Graph**: KnowledgeHubScreen + KnowledgeGraphScreen + KnowledgeDetailScreen with search, filter, simulation
- ✅ **Galaxy Graph Canvas**: Semester-based grid layout with HK headers, completion rings, sim-fail indicators
- ✅ **Dashboard + Focus Mode**: DashboardScreen with widget layout, FocusModeScreen
- ✅ **Study Planner**: StudyPlannerScreen, SyllabusParserScreen, TaskBreakdownScreen
- ✅ **Auth**: Combined AuthScreen (login/register)
- ✅ **Theme**: Cosmic/galaxy design system (Color.kt, DesignSystem.kt, Theme.kt)
- ✅ **Domain Engines**: 9 business logic engines (KnowledgeGraphEngine, BurnoutEngine, GpaEngine, RiskEngine, SimulationEngine, RecommendationEngine, StudyPlannerEngine, TaskBreakdownEngine, WorkloadEngine)
- ✅ **UI State**: CourseHubNavigationState, RepoFilterState
- ✅ **DataStore**: SettingsDataStore for preferences
- ✅ **Tests**: Engine unit tests (9), DeserializationTest, RepoFilterStateTest, CourseHubNavigationStateTest

### Phase 2: Academic Progress Tracker (PTB)
**Status**: 🔄 Partial — GpaEngine, RiskEngine, BurnoutEngine, SimulationEngine built; PTB UI tab not yet wired
**Objective**: Implement the core prerequisite graph and credit tracking system.
**Deliverables**:
- [ ] PTB Dashboard (Credit progress, GPA tracker) — engines ready
- [ ] Prerequisite Graph visualization — RelationshipDao exists
- [ ] Graduation Roadmap generator (static logic) — SimulationEngine ready

### Phase 3: Smart Todo System
**Status**: 🔄 Partial — WorkloadEngine, TaskBreakdownEngine, StudyPlannerEngine built; Execution UI tab not yet wired
**Objective**: Create a task management system tailored for student workloads.
**Deliverables**:
- [ ] Todo List with Deadline/Course mapping — TaskDao exists
- [ ] Workload Analysis (Heatmap/Warning for busy weeks) — WorkloadEngine ready
- [ ] AI Todo Planning (Breakdown complex assignments) — TaskBreakdownEngine ready

### Phase 4: Academic Intelligence Engine
**Status**: 🔄 Partial — RecommendationEngine, AiRepositoryImpl built; Copilot UI tab not yet wired
**Objective**: Integrate advanced AI features for personalized academic advice.
**Deliverables**:
- [ ] Career Roadmap AI (Course suggestions for specific roles) — RecommendationEngine ready
- [ ] Semester Risk Analysis (Identify potential fail risks) — RiskEngine ready
- [ ] Smart Recommendations (Repo/Skill suggestions) — AiRepositoryImpl ready

### Phase 5: Premium Aesthetics & Polish
**Status**: ⬜ Not Started
**Objective**: Finalize the "WOW" factor with smooth animations and high-end UI details.
**Deliverables**:
- [ ] Galaxy Graph Canvas optimizations
- [ ] Micro-animations and transitions
- [ ] Dark Mode / Theming perfection
- [ ] Shimmer loading skeletons
- [ ] Pull-to-refresh on course list
- [ ] Empty/error states
