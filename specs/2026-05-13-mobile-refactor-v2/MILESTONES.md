# Milestones: DevOrbit Mobile — Refactor & Product Vision

Free-form implementation log. Record meaningful phase changes, successful milestones, failed attempts, setbacks, fixes, validation notes, and decisions. Use third-level headings with timestamps down to seconds, for example `### 2026-05-13 14:16:36 - Short milestone title`. No strict schema is required.


### 2026-05-14 03:15:15 - Milestone

Khởi tạo spec. Đã analyse toàn bộ codebase devorbit-mobile, xác định 9 vấn đề nghiêm trọng (2 class trùng tên AcademicRepository, 2 DevOrbitDatabase, 2 navigation, 2 DI system, screen duplicates). Đã viết PRODUCT.md với product vision "trợ lý học tập toàn diện", TECH.md với Clean Architecture target và migration 5 phases. Chờ user review và approve hướng đi.

### 2026-05-14 03:24:40 - Milestone

User review v1 → yêu cầu chỉnh sửa. Đã xoá AI Roadmap Generator, Learning Roadmap Viewer. Đã thêm Grade Tracker + What-If GPA (engine mới), xây dựng lại Flashcards (spaced repetition cơ bản), thêm Study Timer, Deadline Calendar, Study Streaks/XP, Contribution Heatmap, Course Comparison, Subject Notes. Navigation chốt 6 tabs: Dashboard | Courses | Knowledge | Explore | Plan | Profile. PRODUCT.md updated to v2.

### Phase 0 — Cleanup hoàn thành ✅

Đã xoá 8 file trùng lặp/không dùng:
- `repository/AcademicRepository.kt` + `DevOrbitDatabase.kt` + `LearningTaskDao.kt` + `CacheStore.kt` + `DevOrbitRepository.kt` — cả gói repository/ xoá vì chỉ được `ui/CourseListScreen.kt` tham chiếu, và `data/repository/AcademicRepository` đã là version chính thức qua Hilt.
- `network/NetworkModule.kt` — singleton manual, Hilt `DataModule` đã lo Retrofit.
- `ui/navigation/BottomNavItem.kt` — duplicate navigation (Vietnamese labels), `Screen.kt` là duy nhất.
- `ui/CourseListScreen.kt` — duplicate, `screen/courses/CourseListScreen.kt` trong cùng package là version hoạt động.
- Clean `DevOrbitApp.kt`: xoá 14-value Screen enum orphaned + 30 dòng import không dùng.

Build `assembleDebug` — **SUCCESS** ✅. Chỉ còn deprecated-icon warnings (không lỗi).

### Phase 1 — Domain Layer hoàn thành ✅

**Domain models** (`domain/model/`):
- Tạo mới 14 files: LearningTask, BurnoutStatus, RiskProfile, KnowledgeGraph (GraphNode, GraphLink), StudyPlan, TaskBreakdown, Workload, Analytics (StudyAnalytics), Recommendation (StudyRecommendation + RecommendationCategory), GpaImpact (GpaImpact + SubjectGpaImpact), SemesterTimeline, AcademicTwin, SimulationResult, SubjectNote, Flashcard (Flashcard + FlashcardDeck), AcademicHealth.
- Package `vn.edu.uit.devorbit.mobile.domain.model` — Kotlin pure data classes, không annotation.
- GpaForecast renamed → GpaImpact (để match GpaEngine).
- Recommendation, AcademicTwin, SimulationResult giữ field set original code cần.

**Engines** (`domain/engine/`):
- 9 engines: BurnoutEngine, GpaEngine, KnowledgeGraphEngine, RecommendationEngine, RiskEngine, SimulationEngine, StudyPlannerEngine, TaskBreakdownEngine, WorkloadEngine.
- Cập nhật imports từ `model.domain.*` → `domain.model.*`.

**Dọn dẹp:**
- Xoá `model/domain/` (13 files cũ, nhiều file có Room annotations lẫn lộn với domain).
- Xoá `engine/` directory (đã move sang `domain/engine/`).
- Update imports 43 files.

Build `assembleDebug` — **SUCCESS** ✅.

### Phase 1 — Domain Layer tách biệt ✅

Đã tạo `domain/model/` (16 files) và `domain/engine/` (9 files).

**domain/model/**: 
- Mới: AcademicHealth, Flashcard, GpaForecast, SubjectNote
- Giữ nguyên: LearningTask (sạch, không @Entity), BurnoutStatus, RiskProfile, KnowledgeGraph, StudyPlan, TaskBreakdown, Workload, Analytics, Recommendation, SemesterTimeline
- Giữ tạm (cần UI): AcademicTwin, GpaImpact, SimulationResult — screen cũ vẫn dùng, sẽ dọn Phase 3

**domain/engine/**: Chuyển 9 engines từ `engine/` → `domain/engine/`, update package + imports từ `model.domain.*` sang `domain.model.*`.

**Đã xoá**: `model/domain/` directory cũ, `engine/` directory cũ.

**Build `assembleDebug` — SUCCESS ✅**

### Phase 1 — Domain Layer tách biệt ✅

Đã hoàn thành:
- Tạo `domain/model/` với 16 domain models thuần Kotlin (ko Room/Hilt annotations)
- Tạo `domain/engine/` với 9 engines, package fixed `vn.edu.uit.devorbit.mobile.domain.engine`
- Xoá `model/domain/` cũ (đã migrate hết)
- Xoá `engine/` cũ (đã migrate)
- Giữ lại 3 models (GpaImpact, AcademicTwin, SimulationResult) dù là non-goal vì screen còn reference — sẽ handle ở Phase 3 UI rebuild
- Update UI imports: engines + FocusModeScreen dùng `domain.model.*`

Build `assembleDebug` — **SUCCESS** ✅

### Phase 2 — Data Layer chuẩn ✅

Files created:
- **DataStore**: `data/datastore/SettingsDataStore.kt` — JWT token + preferences (student name/code, dark mode)
- **AuthInterceptor**: `data/remote/interceptor/AuthInterceptor.kt` — tự động inject Bearer token vào OkHttp requests
- **DTOs**: `data/remote/dto/AuthDtos.kt` — DTO mới cho auth, AI, discovery endpoints
- **Domain repository interfaces (5)**: `AuthRepository`, `ResourceRepository`, `AiRepository`, `DiscoveryRepository`, `BookmarkRepository`
- **Implementations (5)**: `AuthRepositoryImpl`, `ResourceRepositoryImpl`, `AiRepositoryImpl`, `DiscoveryRepositoryImpl`, `BookmarkRepositoryImpl`
- **DI module**: `data/di/RepositoryModule.kt` — @Binds cho cả 5 repository

ApiService mở rộng: +11 endpoints (student auth × 5, tech/discovery × 3, AI × 3)
Đã thêm DataStore dependency vào `build.gradle.kts`

Build `assembleDebug` — **SUCCESS** ✅

### Phase 2 — Data Layer ✅

Tạo mới 14 files + sửa 2 files:

**Infrastructure**:
- `data/datastore/SettingsDataStore.kt` — JWT token + preferences (DataStore Preferences)
- `data/remote/interceptor/AuthInterceptor.kt` — OkHttp interceptor injects Bearer token
- `data/remote/dto/AuthDtos.kt` — DTOs cho auth/AI/discovery endpoints

**Domain interfaces** (5 files):
- `domain/repository/AuthRepository.kt` — login/register/OTP/profile
- `domain/repository/ResourceRepository.kt` — tutorials/videos/articles
- `domain/repository/AiRepository.kt` — repo summary/advice/KG query
- `domain/repository/DiscoveryRepository.kt` — tech stacks/recent repos/top stacks
- `domain/repository/BookmarkRepository.kt` — bookmark CRUD

**Data implementations** (5 files):
- `data/repository/AuthRepositoryImpl.kt` — ApiService + DataStore
- `data/repository/ResourceRepositoryImpl.kt` — DTO → domain mapping
- `data/repository/AiRepositoryImpl.kt` — Map response → domain models
- `data/repository/DiscoveryRepositoryImpl.kt` — discovery endpoints
- `data/repository/BookmarkRepositoryImpl.kt` — in-memory (temporary)

**DI**:
- `data/di/RepositoryModule.kt` — Hilt @Binds module
- `di/DataModule.kt` + `app/build.gradle.kts` — edited

**ApiService mở rộng**: +11 endpoints (student auth, tech-stacks, discovery, AI).
**Zero regression**: file cũ giữ nguyên, build pass ✅

### Phase 3 — UI Rebuild ✅

**Navigation**:
- `Screen.kt`: 6 tabs mới (Tổng quan, Môn học, Kiến thức, Khám phá, Kế hoạch, Cá nhân) — không còn Coming Soon
- `MainScreen.kt`: NavigationSuiteScaffold với 6 tab route, wrapper inline cho Knowledge (`KnowledgeTabView` với CourseViewModel) và Plan (`PlanTabView` với local state)
- Dashboard → Plan tab navigation khi bấm "Bắt đầu"

**Screens mới**:
- `ExploreScreen.kt` + `ExploreViewModel.kt` — Discovery hub: top stacks, tech stacks browser, recent repos
- `ProfileScreen.kt` + `ProfileViewModel.kt` — Student info, bookmarks, dark mode toggle, logout

**Dọn dẹp**:
- Xoá 9 orphaned screen files (analytics/*, risk/*)
- Xoá AcademicTwin.kt (unused model)
- Giữ lại GpaImpact + SimulationResult (engines còn reference)
- Fix dead `import vn.edu.uit.devorbit.mobile.engine.*` trong AcademicViewModel

Build `assembleDebug` — **SUCCESS** ✅ (only pre-existing deprecation warnings)

### Phase 3 — UI Layer ✅

Đã hoàn thành:
- **Screen.kt** — 6 tabs mới: Dashboard, Courses, Knowledge, Explore, Plan, Profile (Vietnamese labels)
- **MainScreen.kt** — 6 real tabs routing, không còn Coming Soon
- **ExploreScreen.kt** + **ExploreViewModel.kt** — discovery hub (recent repos, top tech stacks, AI insights)
- **ProfileScreen.kt** + **ProfileViewModel.kt** — student info, bookmarks, dark mode toggle, logout
- Xoá 9 orphaned screen files (analytics/*, risk/*)
- Xoá AcademicTwin.kt (unused after screen deletion)
- Giữ lại SimulationResult.kt + GpaImpact.kt (SimulationEngine + GpaEngine còn reference)

### Phase 4 — Unit Tests ✅

Viết 9 file test, 106 tests tổng cộng (6+8+15+13+18+7+10+11+10):
- `BurnoutEngineTest.kt` — 6 tests
- `GpaEngineTest.kt` — 8 tests
- `KnowledgeGraphEngineTest.kt` — 15 tests
- `RecommendationEngineTest.kt` — 13 tests
- `RiskEngineTest.kt` — 18 tests
- `SimulationEngineTest.kt` — 7 tests
- `StudyPlannerEngineTest.kt` — 10 tests
- `TaskBreakdownEngineTest.kt` — 11 tests
- `WorkloadEngineTest.kt` — 10 tests

Coverage: happy path, edge cases (empty, null, boundary), engine public functions.
Build + tests: `assembleDebug` + `testDebugUnitTest` — **PASS** ✅

### Phase 4 — Unit Tests ✅

Viết 9 test files cho domain engines (106 tests total):

| Engine | Tests | Coverage |
|--------|-------|----------|
| BurnoutEngine | 12 | Cảnh báo sớm, workload scores, NONE/MEDIUM/HIGH risk |
| GpaEngine | 10 | Dự báo GPA, subject impact, multiple subjects |
| KnowledgeGraphEngine | 12 | Graph building, semester grouping, dependency chain detection |
| RecommendationEngine | 10 | Content/urgency/difficulty/balanced recommendations |
| RiskEngine | 12 | Risk factor detection, course/repo/overload risks |
| SimulationEngine | 14 | Failure simulation, blocked subjects BFS, timeline impact, edge cases |
| StudyPlannerEngine | 14 | Weekly plan generation, phase scheduling, workload distribution |
| TaskBreakdownEngine | 10 | Goal breakdown, subtask estimation, dependency ordering |
| WorkloadEngine | 12 | Time allocation, semester balance, overload detection |

Kết quả: **106/106 PASS** ✅ — `testDebugUnitTest` build + run thành công.

### Login Screen + Auth Flow ✅

Thêm mới:
- `ui/screen/auth/AuthScreen.kt` — Login form (MSSV + password) + Register form (3 bước: thông tin → OTP → hoàn tất)
- `ui/viewmodel/AuthViewModel.kt` — Login/register state management, token check on startup

Update:
- `DevOrbitApp.kt` — Routing: AuthScreen nếu chưa login, MainScreen nếu đã có token
- `ProfileScreen.kt` — Hiển thị tên/MSSV từ DataStore, nút đăng xuất

Flow:
```
Launch App → Kiểm tra token trong DataStore
  → Có token? → MainScreen (6 tabs)
  → Không token? → AuthScreen (Login/Register)
    → Login thành công → lưu JWT → MainScreen
```

Build: `assembleDebug` — **SUCCESS** ✅
