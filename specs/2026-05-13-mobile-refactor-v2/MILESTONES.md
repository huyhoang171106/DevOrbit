# Milestones: DevOrbit Mobile — Refactor & Product Vision

Free-form implementation log. Record meaningful phase changes, successful milestones, failed attempts, setbacks, fixes, validation notes, and decisions.

**Current Status:** ALL 5 PHASES COMPLETED ✅

### 2026-05-14 03:15:15 - Milestone

Khởi tạo spec. Đã analyse toàn bộ codebase devorbit-mobile, xác định 9 vấn đề nghiêm trọng (2 class trùng tên AcademicRepository, 2 DevOrbitDatabase, 2 navigation, 2 DI system, screen duplicates). Đã viết PRODUCT.md với product vision "trợ lý học tập toàn diện", TECH.md với Clean Architecture target và migration 5 phases. Chờ user review và approve hướng đi.

### 2026-05-14 03:24:40 - Milestone

User review v1 → yêu cầu chỉnh sửa. Đã xoá AI Roadmap Generator, Learning Roadmap Viewer. Đã thêm Grade Tracker + What-If GPA (engine mới), xây dựng lại Flashcards (spaced repetition cơ bản), thêm Study Timer, Deadline Calendar, Study Streaks/XP, Contribution Heatmap, Course Comparison, Subject Notes. Navigation chốt 6 tabs: Dashboard | Courses | Knowledge | Explore | Plan | Profile. PRODUCT.md updated to v2.

### Phase 0 — Cleanup hoàn thành ✅

Đã xoá 8 file trùng lặp/không dùng:
- `repository/AcademicRepository.kt` + `DevOrbitDatabase.kt` + `LearningTaskDao.kt` + `CacheStore.kt` + `DevOrbitRepository.kt` — cả gói repository/ xoá vì chỉ được `ui/CourseListScreen.kt` tham chiếu
- `network/NetworkModule.kt` — singleton manual, Hilt `DataModule` đã lo Retrofit
- `ui/navigation/BottomNavItem.kt` — duplicate navigation, `Screen.kt` là duy nhất
- `ui/CourseListScreen.kt` — duplicate, `screen/courses/CourseListScreen.kt` là version hoạt động
- Clean `DevOrbitApp.kt`: xoá 14-value Screen enum orphaned + 30 dòng import không dùng

Build `assembleDebug` — **SUCCESS** ✅

### Phase 1 — Domain Layer hoàn thành ✅

**Domain models** (`domain/model/`): Tạo mới 14 files, package `vn.edu.uit.devorbit.mobile.domain.model` — Kotlin pure data classes, không annotation.

**Engines** (`domain/engine/`): 9 engines — BurnoutEngine, GpaEngine, KnowledgeGraphEngine, RecommendationEngine, RiskEngine, SimulationEngine, StudyPlannerEngine, TaskBreakdownEngine, WorkloadEngine.

**Dọn dẹp:** Xoá `model/domain/` (13 files cũ), xoá `engine/` directory.

Build `assembleDebug` — **SUCCESS** ✅

### Phase 2 — Data Layer chuẩn ✅

Files created:
- `data/datastore/SettingsDataStore.kt` — JWT token + preferences
- `data/remote/interceptor/AuthInterceptor.kt` — OkHttp interceptor injects Bearer token
- `data/remote/dto/AuthDtos.kt` — DTOs cho auth/AI/discovery endpoints
- Domain repository interfaces (5): AuthRepository, ResourceRepository, AiRepository, DiscoveryRepository, BookmarkRepository
- Implementations (5): AuthRepositoryImpl, ResourceRepositoryImpl, AiRepositoryImpl, DiscoveryRepositoryImpl, BookmarkRepositoryImpl
- DI module: `data/di/RepositoryModule.kt` — @Binds cho cả 5 repository

ApiService mở rộng: +11 endpoints
Build `assembleDebug` — **SUCCESS** ✅

### Phase 3 — UI Rebuild ✅

**Navigation**: Screen.kt với 6 tabs mới (Tổng quan, Môn học, Kiến thức, Khám phá, Kế hoạch, Cá nhân). MainScreen.kt với NavigationSuiteScaffold.

**Screens mới**: ExploreScreen + ExploreViewModel (Discovery hub), ProfileScreen + ProfileViewModel (Student info, bookmarks, dark mode toggle, logout).

**Dọn dẹp**: Xoá 9 orphaned screen files (analytics/*, risk/*), xoá AcademicTwin.kt.

Build `assembleDebug` — **SUCCESS** ✅

### Phase 4 — Unit Tests ✅

Viết 9 test files cho domain engines, 106 tests total:

| Engine | Tests | Coverage |
|--------|-------|----------|
| BurnoutEngine | 12 | Cảnh báo sớm, workload scores |
| GpaEngine | 10 | Dự báo GPA, subject impact |
| KnowledgeGraphEngine | 12 | Graph building, dependency chain |
| RecommendationEngine | 10 | Content/urgency/difficulty/balanced |
| RiskEngine | 12 | Risk factor detection |
| SimulationEngine | 14 | Failure simulation, BFS path |
| StudyPlannerEngine | 14 | Weekly plan, phase scheduling |
| TaskBreakdownEngine | 10 | Goal breakdown, dependency |
| WorkloadEngine | 12 | Time allocation, overload |

Kết quả: **106/106 PASS** ✅

### Login Screen + Auth Flow ✅

Thêm mới:
- `ui/screen/auth/AuthScreen.kt` — Login form + Register form (3 bước)
- `ui/viewmodel/AuthViewModel.kt` — Login/register state management

Update:
- `DevOrbitApp.kt` — Routing: AuthScreen nếu chưa login, MainScreen nếu đã có token
- `ProfileScreen.kt` — Hiển thị tên/MSSV từ DataStore, nút đăng xuất

Build `assembleDebug` — **SUCCESS** ✅

---

### What Remains After Phase 4

| Feature | Status | Notes |
|---------|--------|-------|
| Plan tab (PlanTabHost, PlanViewModel) | ⏳ Pending | Sub-navigation + ViewModel |
| GpaForecastScreen | ⏳ Pending | GPA forecast UI |
| FlashcardScreen | ⏳ Pending | Deck list + study mode |
| FlashcardEngine | ⏳ Pending | SM-2 spaced repetition |
| Flashcard Room entities/DAO | ⏳ Pending | Persistence layer |
| StudyPlannerScreen wiring | ⏳ Pending | Connect to PlanViewModel |
| TaskBreakdownScreen wiring | ⏳ Pending | Connect to PlanViewModel |
| Compose UI tests | ⏳ Pending | Screen render tests |
| API integration tests | ⏳ Pending | Repository layer tests |
