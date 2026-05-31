# Architecture Audit

> Generated: 2026-05-31 | Project: DevOrbit | Scope: Full system

---

## Component Map

### devorbit-api — Spring Boot 4 Backend

| Attribute | Value |
|-----------|-------|
| **Framework** | Spring Boot 4.0.6 (Java 21) |
| **Architecture** | 3-layer: Controller → Service → Repository |
| **Database** | PostgreSQL 16 (via Supabase Pooler) |
| **Auth** | JWT (jjwt 0.12.6) with role-based access (ADMIN, STUDENT) |
| **External Integrations** | GitHub REST API v3, Supabase Storage, Gmail SMTP, Gemini/OpenAI |
| **ORM** | Spring Data JPA |
| **API Docs** | springdoc-openapi 2.8.6 |
| **Build** | Maven with wrapper (mvnw.cmd) |
| **Tests** | @WebMvcTest controller tests, H2 in-memory integration tests |

**Package structure:**
`
vn.edu.uit.devorbit_api/
├── config/          (9 classes)  — JWT, Security, GitHub, OpenAPI, Jackson, ElectiveGroup
├── constant/        (1 class)    — CurriculumConstants (SE2025)
├── controller/      (20 classes) — 9 Admin, 6 Public, 2 Student, Health, Photobooth, FileUpload
├── dto/             (43 DTOs)    — admin/ (26), publicapi/ (10), student/ (5), root (1)
├── entity/          (22 classes) — JPA entities for all domain tables
├── event/           (1 class)    — RelationshipChangedEvent
├── exception/       (4 classes)  — BadRequest, NotFound, Unauthorized + ApiExceptionHandler
├── repository/      (18 repos)   — Spring Data JPA interfaces
└── service/         (25 classes) — Business logic + 5 AI services (ai/ subpackage)
`

### devorbit-web — React 19 SPA

| Attribute | Value |
|-----------|-------|
| **Framework** | React 19 + Vite 6 |
| **Language** | TypeScript |
| **Routing** | React Router 7 (client-side) |
| **State** | TanStack React Query 5 + Zustand 5 |
| **Styling** | Tailwind CSS 3.4 |
| **Visualization** | D3 ForceGraph 2D, Three.js / R3F 3D |
| **Build** | Vite 6, bun |
| **Tests** | Vitest |

**Architecture pattern:** SPA with client-side routing, feature-based page directories (pages/student/, pages/admin/), shared components, lib utilities, and custom hooks.

### devorbit-mobile — Kotlin Jetpack Compose

| Attribute | Value |
|-----------|-------|
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose |
| **DI** | Hilt |
| **Networking** | Retrofit + OkHttp |
| **Architecture** | MVVM (ViewModel + Repository) |
| **DB** | Room (local cache) |
| **Build** | Gradle (wrapper: gradlew.bat only) |

**Package structure:**
`
app/src/main/java/.../
├── data/           — Retrofit API, DTOs, Repository implementations
├── domain/engine/  — Business logic engines (9 engines)
├── domain/model/   — Domain models (14 models)
├── ui/screen/      — Compose screens (course, repo, explore, knowledge, photobooth)
├── ui/viewmodel/   — ViewModels (6: Academic, Auth, Course, Explore, Knowledge, Profile)
└── ui/theme/       — Material3 theming
`

### devorbit-showcase — Next.js 16 Portfolio

| Attribute | Value |
|-----------|-------|
| **Framework** | Next.js 16 (App Router) |
| **Language** | TypeScript |
| **3D** | Three.js / R3F |
| **Build** | Next.js + npm |
| **Lint** | ESLint |

---

## Layer Violations

The 3-layer architecture (Controller → Service → Repository) is violated in 5 locations:

| # | Violating Class | Repository Injected | Severity | Fix |
|---|----------------|-------------------|----------|-----|
| 1 | PublicDiscoveryController | GithubRepoRepository | Medium | Delegate to GithubRepoService |
| 2 | PublicDiscoveryController | TechStackRepository | Medium | Delegate to TechStackService |
| 3 | AiService | GithubRepoRepository | Medium | Add method to GithubRepoService |
| 4 | i/RoadmapGenerator | CourseRepository | Medium | Use CourseService |
| 5 | i/SummaryGenerator | CourseRepository | Medium | Use CourseService |

**Impact:** Repositories are accessed directly, bypassing service-layer business logic. This creates invisible coupling and makes it harder to add caching, validation, or transaction boundaries.

---

## Circular Dependencies

| Service A | Service B | Nature | Workaround |
|-----------|-----------|--------|------------|
| KnowledgeGraphService | CourseRelationshipService | Mutual dependency — KnowledgeGraphService calls CourseRelationshipService for prerequisite data; CourseRelationshipService calls KnowledgeGraphService for graph updates | @Lazy annotation on constructor injection |

**Risk:** Spring resolves this at runtime via proxy, but it indicates a design issue where two services share too much domain responsibility. Both services should depend on a shared data contract (interface) rather than each other.

---

## God Files (>300 lines)

### Backend (API)

| File | Lines | Size | Responsibility |
|------|-------|------|---------------|
| i/RoadmapGenerator.java | 728 | 39KB | Phase generation, prerequisite resolution, course sorting, AI prompt building, curriculum traversal |
| i/GraphQueryEngine.java | 337 | 17KB | Graph traversal, path finding, relationship queries |
| GithubScanService.java | 351 | 17KB | GitHub API scanning, candidate creation, context fetching |
| CourseService.java | 234 | 11KB | CRUD operations, mapping, validation |

**Recommendation for RoadmapGenerator (728 lines):**
- Extract PhaseBuilder — handles phase creation and ordering
- Extract PrerequisiteResolver — resolves prerequisite chains
- Extract CourseSorter — topological sort of courses
- Extract PromptBuilder — AI prompt construction
- Target: 4-5 files of ~150-200 lines each

### Frontend (Web)

| File | Lines | Size | Responsibility |
|------|-------|------|---------------|
| lib/repoEvaluation.ts | 1,071 | 61KB | Evaluation scoring, X-ray analysis, README parsing, Vietnamese formatting, metadata extraction |
| pages/student/GpaCalculatorPage.tsx | 979 | 47KB | GPA calculation, semester presets, goal planner, what-if simulator, UI layout |
| components/student/KanbanBoard.tsx | 935 | 41KB | Drag-drop, column management, card rendering, persistence, responsive layout |

**Recommendation for repoEvaluation.ts (1,071 lines):**
- Extract evaluation/ module — scoring algorithms
- Extract nalysis/ module — X-ray and README parsing
- Extract ormatting/ module — Vietnamese text formatting
- Extract 	ypes/ module — interfaces and type definitions

**Recommendation for GpaCalculatorPage.tsx (979 lines):**
- Extract useGoalPlanner hook — goal setting and what-if logic
- Extract usePresetManager hook — semester preset CRUD
- Extract GpaEngine — pure calculation functions
- Split UI into SemesterTable, GoalPanel, CumulativeEstimate components

**Recommendation for KanbanBoard.tsx (935 lines):**
- Extract KanbanColumn component — column rendering
- Extract KanbanCard component — individual card
- Extract useKanbanDrag hook — drag-drop logic
- Extract useKanbanPersistence hook — save/load state

---

## Feature Boundary Assessment

| Project | Boundary Clarity (1-5) | Notes |
|---------|----------------------|-------|
| devorbit-api | 4 | Clear 3-layer architecture with well-defined controllers. Violations in PublicDiscoveryController and AI services weaken boundaries. |
| devorbit-web | 3 | Feature-based pages exist but god components blur boundaries. Shared lib/ directory accumulates too much logic. |
| devorbit-mobile | 4 | Clean MVVM with domain/engine separation. Good Hilt DI. Missing Gradle wrapper for Unix. |
| devorbit-showcase | 3 | Template-derived codebase with leftover URLs/IPs. Feature boundaries unclear due to inherited structure. |

---

## Refactor Priority Matrix

| Issue | Impact | Effort | Priority | Recommendation |
|-------|--------|--------|----------|----------------|
| 5 layer violations (repos in controllers/AI) | Medium | 1 day | P1 | Route all DB access through services |
| Circular dependency (Knowledge ↔ CourseRelationship) | Medium | 2 hrs | P1 | Extract shared GraphDataProvider interface |
| RoadmapGenerator god class (728 lines) | High | 2-3 days | P2 | Extract 4-5 focused helper classes |
| repoEvaluation.ts god file (1,071 lines) | High | 2-3 days | P2 | Split into evaluation/, analysis/, formatting/ modules |
| GpaCalculatorPage.tsx god component (979 lines) | High | 2-3 days | P2 | Extract hooks + sub-components |
| KanbanBoard.tsx god component (935 lines) | High | 2-3 days | P2 | Extract hooks + sub-components |
| Duplicate graph loading in mobile | Medium | 1 day | P2 | Shared GraphRepository between ViewModels |
| Missing Unix Gradle wrapper (gradlew) | Low | 10 min | P2 | Generate with gradle wrapper |
| Template leftovers in showcase | Low | 15 min | P3 | Replace hardcoded URLs/IPs |

---

## Architecture Health Score

| Dimension | Score | Notes |
|-----------|-------|-------|
| Layering | 7/10 | Clean 3-layer in most places, 5 violations to fix |
| Dependency Management | 8/10 | Hilt DI (mobile), Spring DI (API), clean imports |
| Modularity | 6/10 | God files hurt modularity; extract/refactor needed |
| Testability | 7/10 | Good test infrastructure, but coverage tools missing |
| Documentation | 8/10 | ARCHITECTURE.md, AGENTS.md files, entity docs |
| **Overall** | **7.2/10** | Solid foundation with targeted refactoring needed |