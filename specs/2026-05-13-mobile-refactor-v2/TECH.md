# DevOrbit Mobile — Technical Spec

## 1. Codebase Diagnosis

### 1.1 Cấu trúc hiện tại (hỗn loạn)

```
devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/
├── data/                          # ✨ Layer mới, chuẩn hơn
│   └── local/
│       ├── dao/                   # CourseDao, RepoDao, RelationshipDao, TaskDao
│       ├── entity/                # CourseEntity, RepoEntity, CourseRelationshipEntity, TaskEntity
│       └── DevOrbitDatabase.kt    # Room database (courses, repos, relationships, tasks)
├── di/
│   └── DataModule.kt              # ✅ Hilt module chính (dùng DI chuẩn)
├── model/
│   ├── CourseSummary.kt           # API response models
│   ├── RepoSummary.kt
│   ├── CourseRelationshipResponse.kt
│   ├── GraphResponse.kt
│   ├── KnowledgeModels.kt        # CourseTutorial, CourseYoutubePlaylist, CourseArticle
│   ├── TechStack.kt
│   └── domain/                   # Domain models
│       ├── KnowledgeGraph.kt
│       ├── LearningTask.kt       # @Entity Room
│       ├── StudyPlan.kt
│       ├── TaskBreakdown.kt
│       ├── Workload.kt
│       ├── RiskProfile.kt
│       ├── BurnoutStatus.kt
│       ├── BurnoutStatus.kt
│       ├── Analytics.kt
│       ├── SemesterTimeline.kt
│       ├── AcademicTwin.kt
│       ├── GpaImpact.kt
│       ├── Recommendation.kt
│       ├── SimulationResult.kt
│       └── ... (15+ files)
├── network/
│   ├── ApiService.kt              # ✅ Retrofit interface đầy đủ
│   └── NetworkModule.kt           # ❌ Object singleton (conflict với Hilt)
├── repository/
│   ├── AcademicRepository.kt      # ❌ TRÙNG TÊN với data/repository/
│   ├── CacheStore.kt
│   ├── DevOrbitDatabase.kt       # ❌ TRÙNG TÊN với data/local/
│   ├── DevOrbitRepository.kt
│   └── LearningTaskDao.kt
├── data/repository/
│   └── AcademicRepository.kt      # ❌ TRÙNG TÊN với repository/
├── engine/                        # Local computation engines
│   ├── BurnoutEngine.kt
│   ├── GpaEngine.kt
│   ├── KnowledgeGraphEngine.kt
│   ├── RecommendationEngine.kt
│   ├── RiskEngine.kt
│   ├── SimulationEngine.kt
│   ├── StudyPlannerEngine.kt
│   ├── TaskBreakdownEngine.kt
│   └── WorkloadEngine.kt
└── ui/
    ├── navigation/
    │   ├── Screen.kt              # Sealed class nav (5 tabs English)
    │   └── BottomNavItem.kt       # ❌ TRÙNG MỤC ĐÍCH với Screen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── DesignSystem.kt
    │   └── Theme.kt
    ├── components/
    │   ├── CosmicBackground.kt
    │   ├── GalaxyGraphCanvas.kt
    │   └── GlassCard.kt
    ├── viewmodel/
    │   ├── AcademicViewModel.kt
    │   └── CourseViewModel.kt
    ├── DevOrbitApp.kt
    ├── MainScreen.kt
    ├── CourseListScreen.kt         # ❌ TRÙNG với screen/courses/CourseListScreen.kt
    ├── CourseDetailScreen.kt
    ├── RepoDetailScreen.kt
    ├── RepoFilterSheet.kt
    ├── RepoListSection.kt
    └── screen/
        ├── dashboard/
        │   ├── DashboardScreen.kt
        │   └── FocusModeScreen.kt
        ├── courses/
        │   ├── CourseHubScreen.kt
        │   └── CourseListScreen.kt  # ❌ TRÙNG với ui/CourseListScreen.kt
        ├── knowledge/
        │   ├── KnowledgeGraphScreen.kt
        │   └── KnowledgeDetailScreen.kt
        ├── plan/
        │   ├── StudyPlannerScreen.kt
        │   ├── SyllabusParserScreen.kt
        │   └── TaskBreakdownScreen.kt
        ├── analytics/
        │   ├── AnalyticsScreen.kt
        │   ├── GpaScreen.kt
        │   ├── SemesterTimelineScreen.kt
        │   └── WorkloadScreen.kt
        └── risk/
            ├── RiskCenterScreen.kt
            ├── BurnoutScreen.kt
            ├── DigitalTwinScreen.kt
            ├── RecommendationsScreen.kt
            └── SimulationScreen.kt
```

### 1.2 Vấn đề nghiêm trọng

| # | Vấn đề | Mức độ | Fix |
|---|---|---|---|
| 1 | 2 class `AcademicRepository` khác package | 🔴 CRITICAL — compile không biết cái nào được inject | Xoá 1, giữ 1 |
| 2 | 2 class `DevOrbitDatabase` | 🔴 CRITICAL — Room conflict | Hợp nhất entities |
| 3 | 2 navigation định nghĩa | 🟡 MEDIUM | Giữ `Screen.kt`, xoá `BottomNavItem.kt` |
| 4 | `NetworkModule.kt` object vs Hilt | 🟡 MEDIUM | Xoá object, Hilt lo hết |
| 5 | `ui/CourseListScreen.kt` vs `screen/courses/` | 🟡 MEDIUM | Giữ `screen/courses/`, xoá `ui/` version |
| 6 | ViewModel dùng `data.repository.AcademicRepository` | ✅ OK | Giữ nguyên |
| 7 | `repository/AcademicRepository` dùng engine | 🟡 MEDIUM | Engine là pure functions, move vào domain |
| 8 | Engine classes test coverage = 0% | 🟡 MEDIUM | Thêm unit test |
| 9 | Theme: `DevOrbitTheme` vs `CosmicTheme` | 🟢 LOW | Đồng bộ |

---

## 2. Target Architecture: Clean Architecture

```
┌──────────────────────────────────────────────────────────┐
│                     UI LAYER                              │
│  Compose Screens → ViewModels (UiState)                  │
│  Navigation: Screen sealed class                          │
├──────────────────────────────────────────────────────────┤
│                   DOMAIN LAYER                            │
│  domain/model/     — Domain entities (LearningTask, etc)  │
│  domain/engine/    — Pure computation (stateless)         │
│  domain/repository/— Interfaces                           │
├──────────────────────────────────────────────────────────┤
│                   DATA LAYER                              │
│  data/remote/      — ApiService + DTO                    │
│  data/local/       — Room DAO + Entity                   │
│  data/repository/  — Impl (1 repo, 1 source of truth)    │
│  data/di/          — Hilt module duy nhất                │
└──────────────────────────────────────────────────────────┘
```

---

## 3. Package structure (sau refactor)

```
vn.edu.uit.devorbit.mobile/
├── DevOrbitApplication.kt
├── MainActivity.kt
│
├── domain/
│   ├── model/
│   │   ├── Course.kt
│   │   ├── Repo.kt
│   │   ├── CourseRelationship.kt
│   │   ├── KnowledgeGraph.kt
│   │   ├── LearningTask.kt
│   │   ├── StudyPlan.kt
│   │   ├── TaskBreakdown.kt
│   │   ├── Workload.kt
│   │   ├── RiskProfile.kt
│   │   ├── BurnoutStatus.kt
│   │   ├── AcademicHealth.kt
│   │   ├── GpaForecast.kt
│   │   ├── SimulationResult.kt
│   │   └── StudyRecommendation.kt
│   └── engine/
│       ├── StudyPlannerEngine.kt     ✅ Refactor → pure + testable
│       ├── TaskBreakdownEngine.kt    ✅ Refactor
│       ├── WorkloadEngine.kt         ✅ Refactor
│       ├── BurnoutEngine.kt          ✅ Refactor
│       ├── RiskEngine.kt             ✅ Refactor
│       ├── SimulationEngine.kt       ✅ Refactor
│       ├── GpaEngine.kt              ✅ Refactor
│       ├── KnowledgeGraphEngine.kt   ✅ Refactor
│       └── RecommendationEngine.kt   ✅ Refactor
│
├── data/
│   ├── remote/
│   │   ├── ApiService.kt            ✅ Mở rộng: thêm student, bookmark, tech
│   │   ├── dto/                     # Chỉ dùng trong data layer
│   │   │   ├── CourseDto.kt
│   │   │   ├── RepoDto.kt
│   │   │   ├── GraphResponseDto.kt
│   │   │   └── TechStackDto.kt
│   │   └── interceptor/
│   │       └── AuthInterceptor.kt   # JWT inject
│   ├── local/
│   │   ├── DevOrbitDatabase.kt      # Hợp nhất (courses, repos, relationships, tasks, learning_tasks)
│   │   ├── dao/
│   │   │   ├── CourseDao.kt
│   │   │   ├── RepoDao.kt
│   │   │   ├── RelationshipDao.kt
│   │   │   ├── TaskDao.kt
│   │   │   └── LearningTaskDao.kt
│   │   └── entity/                  # Room entities (tách biệt khỏi domain)
│   │       ├── CourseEntity.kt
│   │       ├── RepoEntity.kt
│   │       ├── RelationshipEntity.kt
│   │       ├── TaskEntity.kt
│   │       └── LearningTaskEntity.kt
│   ├── repository/
│   │   ├── CourseRepository.kt      # Tách nhỏ, SRP
│   │   ├── RepoRepository.kt
│   │   ├── GraphRepository.kt
│   │   ├── TaskRepository.kt
│   │   ├── ResourceRepository.kt    # Tutorials, videos, articles
│   │   ├── AuthRepository.kt        # Login/register JWT
│   │   └── BookmarkRepository.kt
│   ├── datastore/
│   │   └── SettingsDataStore.kt     # Preferences + JWT token
│   └── di/
│       ├── AppModule.kt             # Context, DataStore
│       ├── NetworkModule.kt         # Hilt module cho Retrofit, OkHttp
│       ├── DatabaseModule.kt        # Room database
│       └── RepositoryModule.kt      # Bind interfaces → impls
│
└── ui/
    ├── navigation/
    │   └── Screen.kt                # DUY NHẤT
    ├── theme/
    │   ├── Color.kt
    │   ├── Type.kt
    │   └── Theme.kt
    ├── components/
    │   ├── CosmicBackground.kt
    │   ├── GlassCard.kt
    │   ├── GalaxyGraphCanvas.kt
    │   ├── LoadingIndicator.kt
    │   ├── ErrorState.kt
    │   └── EmptyState.kt
    ├── screen/
    │   ├── dashboard/
    │   │   ├── DashboardScreen.kt
    │   │   ├── DashboardViewModel.kt
    │   │   └── FocusModeScreen.kt
    │   ├── courses/
    │   │   ├── CourseListScreen.kt
    │   │   ├── CourseListViewModel.kt
    │   │   ├── CourseDetailScreen.kt
    │   │   ├── CourseDetailViewModel.kt
    │   │   └── RepoDetailScreen.kt
    │   ├── knowledge/
    │   │   ├── KnowledgeGraphScreen.kt
    │   │   ├── KnowledgeGraphViewModel.kt
    │   │   └── KnowledgeDetailScreen.kt
    │   ├── resources/
    │   │   ├── ResourceScreen.kt     # YouTube, articles, tutorials
    │   │   └── ResourceViewModel.kt
    │   ├── plan/
    │   │   ├── StudyPlannerScreen.kt
    │   │   ├── StudyPlannerViewModel.kt
    │   │   ├── TaskBreakdownScreen.kt
    │   │   └── SyllabusParserScreen.kt
    │   ├── analytics/
    │   │   ├── AnalyticsScreen.kt
    │   │   ├── WorkloadScreen.kt
    │   │   ├── GpaScreen.kt
    │   │   └── TimelineScreen.kt
    │   ├── risk/
    │   │   ├── RiskCenterScreen.kt
    │   │   ├── RiskViewModel.kt
    │   │   ├── BurnoutScreen.kt
    │   │   ├── SimulationScreen.kt
    │   │   ├── DigitalTwinScreen.kt
    │   │   └── RecommendationsScreen.kt
    │   ├── auth/
    │   │   ├── LoginScreen.kt
    │   │   ├── RegisterScreen.kt
    │   │   └── AuthViewModel.kt
    │   └── bookmark/
    │       ├── BookmarkScreen.kt
    │       └── BookmarkViewModel.kt
    └── DevOrbitApp.kt
```

---

## 4. Data Flow

```
[Compose UI] ←StateFlow→ [ViewModel] ──suspend──→ [Repository]
                                                      │
                                          ┌───────────┴───────────┐
                                          ↓                       ↓
                                    [ApiService]            [Room DAO]
                                          ↓                       ↓
                                    [Remote API]           [SQLite DB]
                                          ↓
                                    [Spring Boot]
```

### Offline-first pattern

```
fun getCourses(): Flow<List<Course>> = channelFlow {
    // 1. Phát ngay cache
    val cached = dao.getAllCourses().map { it.toDomain() }
    send(cached)
    
    // 2. Fetch remote trong background
    try {
        val remote = api.getCourses()
        dao.upsert(remote.map { it.toEntity() })
        // 3. Room flow tự động cập nhật
    } catch (e: Exception) {
        // Cache đã được send ở bước 1
    }
}
```

---

## 5. Migration Strategy

### Phase 0 — Cleanup (an toàn, 1-2 session)
1. Backup toàn bộ `devorbit-mobile` branch
2. Xoá `repository/AcademicRepository.kt` — `data/repository/` đã có version tốt hơn
3. Xoá `repository/DevOrbitDatabase.kt` — `data/local/` đã có version đầy đủ
4. Xoá `network/NetworkModule.kt` — `DataModule.kt` trong `di/` đã là Hilt
5. Xoá `ui/navigation/BottomNavItem.kt` — dùng `Screen.kt` duy nhất
6. Xoá `ui/CourseListScreen.kt` — giữ `ui/screen/courses/CourseListScreen.kt`
7. Xoá `repository/CacheStore.kt` — Room đã là cache
8. Xoá `repository/LearningTaskDao.kt` — learning_tasks dùng Room DAO

### Phase 1 — Domain layer tách biệt
1. Tạo `domain/model/` với domain classes thuần Kotlin (ko annotation)
2. Tạo `domain/engine/` với engine classes là pure functions
3. Merge `LearningTask.kt` (trong model/domain) thành domain model
4. Xoá `model/domain/`, thay bằng `domain/model/`

### Phase 2 — Data layer chuẩn
1. Tách `AcademicRepository` thành các repo nhỏ theo SRP
2. Thêm `AuthInterceptor` cho OkHttp client
3. Hợp nhất Room database: 1 database chứa cả courses, repos, relationships, tasks
4. Thêm DataStore cho preferences + JWT
5. Mapping DTO → Domain → Entity rõ ràng

### Phase 3 — UI hoàn thiện
1. Thêm ResourceScreen (tutorials, videos, articles)
2. Thêm AuthScreen (login/register)
3. Thêm BookmarkScreen
4. Hoàn thiện các screen đang stub (Progress, Execution, Copilot)
5. Mỗi screen có ViewModel riêng

### Phase 4 — Engines & Testing
1. Unit test cho tất cả engine classes
2. Integration test cho repository layer
3. Compose UI test cho screen chính
4. Test coverage target: domain > 80%, data > 60%, ui > 30%

---

## 6. Dependencies (sau refactor)

| Dependency | Version | Purpose |
|---|---|---|
| Kotlin | 2.0.21 | Language |
| Compose BOM | 2024.11.00 | UI framework |
| Material 3 | via BOM | Design system |
| Navigation Compose | 2.8.4 | Screen navigation |
| Lifecycle ViewModel | 2.8.7 | MVVM |
| Hilt | 2.51.1 | DI |
| Retrofit | 2.11.0 | HTTP client |
| Gson Converter | 2.11.0 | JSON parsing |
| OkHttp Logging | 4.12.0 | Network logging |
| Room | 2.6.1 | Local database |
| DataStore | 1.1.1 | Preferences + token |
| Coroutines | 1.9.0 | Async |
| JUnit | 4.13.2 | Unit testing |
| Turbine | 1.1.0 | Flow testing |
| MockK | 1.13.13 | Mocking |
| Compose UI Test | via BOM | UI testing |

---

## 7. Risks & Mitigations

| Risk | Severity | Mitigation |
|---|---|---|
| Xoá nhầm class còn reference | HIGH | Compile kiểm tra, tạm thời `@Deprecated` trước khi xoá |
| Room migration conflict | HIGH | `fallbackToDestructiveMigration()` trong dev, schema version tracking |
| Hilt inject conflict 2 repository | HIGH | Kiểm tra `@Binds` không trùng |
| Engine logic sai khi test | MEDIUM | TDD: viết test trước khi sửa engine |
| Navigation state bị reset | MEDIUM | Dùng SavedStateHandle trong ViewModel |
| API endpoint thay đổi | LOW | Retrofit DTO mapping tách biệt, dễ sửa |

---

## 8. Validation Plan

| Mục | Phương pháp |
|---|---|
| Clean Architecture tách lớp | Kiểm tra dependency direction (domain ko biết data, ui ko biết data) |
| Không còn duplicate | GitHub Action check: `find . -name '*.kt' | sort | uniq -d` |
| Offline-first hoạt động | Unit test: gọi repo, mock API fail, verify cache trả về |
| All screens render | Compose UI test: render test cho mỗi screen |
| Navigation đúng | Test backstack với Navigation Compose testing |
| API cover | So sánh ApiService endpoints với backend OpenAPI spec |

---

## 9. API Expansion Roadmap

### Giai đoạn 1 — Hiện tại
```
ApiService: getCourses, getRepos, getTutorials, getVideos, getArticles, getRelationships, getKnowledgeGraph
```

### Giai đoạn 2 — Bổ sung (Phase 2 of refactor)
```
+ login(username, password) → AuthResponse       # POST /api/student/auth/login
+ register(msv, email, password) → AuthResponse  # POST /api/student/auth/register
+ getTechStacks() → List<TechStackDto>            # GET /api/tech
+ getReposByTech(tech) → List<RepoDto>            # GET /api/repos?tech=xxx
+ discover() → List<DiscoveryDto>                 # GET /api/discovery
+ getAiInsights(repoId) → AiInsightDto            # GET /api/ai?repoId={id}
+ addBookmark(type, id)                           # POST /api/student/bookmarks
+ removeBookmark(type, id)                        # DELETE /api/student/bookmarks/{id}
+ getBookmarks() → List<BookmarkDto>              # GET /api/student/bookmarks
```

### Giai đoạn 3 — Premium (no backend needed)
```
+ parseSyllabus(text) → List<LearningTask>         # Local NLP (Rule-based)
+ calculateGpa(grades) → GpaForecast               # Local computation
+ generateStudyPlan(tasks) → StudyPlan              # StudyPlannerEngine
+ detectBurnout(tasks) → BurnoutStatus              # BurnoutEngine
+ simulateFailure(courseId, graph) → SimulationResult # SimulationEngine
```
