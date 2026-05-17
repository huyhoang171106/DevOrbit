# DevOrbit Mobile — Technical Spec

> **Last Updated:** 2026-05-16
> **Status:** Clean Architecture applied; Plan tab features pending

## 1. Codebase Diagnosis — ORIGINAL PROBLEMS (ALL FIXED ✅)

| # | Vấn đề | Mức độ | Fix |
|---|---|---|---|
| 1 | 2 class `AcademicRepository` khác package | 🔴 CRITICAL | ✅ Xoá 1, giữ 1 |
| 2 | 2 class `DevOrbitDatabase` | 🔴 CRITICAL | ✅ Hợp nhất entities |
| 3 | 2 navigation định nghĩa | 🟡 MEDIUM | ✅ Giữ `Screen.kt`, xoá `BottomNavItem.kt` |
| 4 | `NetworkModule.kt` object vs Hilt | 🟡 MEDIUM | ✅ Xoá object, Hilt lo hết |
| 5 | `ui/CourseListScreen.kt` vs `screen/courses/` | 🟡 MEDIUM | ✅ Giữ `screen/courses/`, xoá `ui/` |
| 6 | ViewModel dùng `data.repository.AcademicRepository` | ✅ OK | ✅ Giữ nguyên |
| 7 | `repository/AcademicRepository` dùng engine | 🟡 MEDIUM | ✅ Engine là pure functions, move vào domain |
| 8 | Engine classes test coverage = 0% | 🟡 MEDIUM | ✅ 106 unit tests |
| 9 | Theme: `DevOrbitTheme` vs `CosmicTheme` | 🟢 LOW | ✅ Đồng bộ |

## 2. Target Architecture: Clean Architecture (APPLIED ✅)

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

## 3. Package structure (sau refactor) — APPLIED ✅

```
vn.edu.uit.devorbit.mobile/
├── DevOrbitApplication.kt ✅
├── MainActivity.kt ✅
├── domain/
│   ├── model/ ✅ (16 files)
│   └── engine/ ✅ (9 files)
├── data/
│   ├── remote/
│   │   ├── ApiService.kt ✅ (mở rộng)
│   │   ├── dto/ ✅
│   │   └── interceptor/AuthInterceptor.kt ✅
│   ├── local/
│   │   ├── DevOrbitDatabase.kt ✅
│   │   ├── dao/ ✅ (CourseDao, RepoDao, RelationshipDao, TaskDao)
│   │   └── entity/ ✅
│   ├── repository/ ✅ (AcademicRepository + 5 new implementations)
│   ├── datastore/SettingsDataStore.kt ✅
│   └── di/ ✅ (RepositoryModule)
└── ui/
    ├── navigation/Screen.kt ✅ (DUY NHẤT)
    ├── theme/ ✅
    ├── components/ ✅
    ├── screen/
    │   ├── dashboard/ ✅
    │   ├── courses/ ✅
    │   ├── knowledge/ ✅
    │   ├── explore/ ✅
    │   ├── plan/ ✅ (StudyPlannerScreen, TaskBreakdownScreen, SyllabusParserScreen — pending PlanTabHost wiring)
    │   └── auth/ ✅ (AuthScreen)
    ├── viewmodel/ ✅ (Academic, Auth, Course, Explore, Knowledge, Profile)
    └── DevOrbitApp.kt ✅
```

## 4. Data Flow (APPLIED ✅)

```
[Compose UI] ←StateFlow→ [ViewModel] ──suspend──→ [Repository]
                                                      │
                                          ┌───────────┴───────────┐
                                          ↓                       ↓
                                    [ApiService]            [Room DAO]
                                          ↓                       ↓
                                    [Remote API]           [SQLite DB]
```

Offline-first pattern: ✅ applied in all repositories

## 5. Migration Strategy — ALL 5 PHASES COMPLETED ✅

| Phase | Description | Status |
|-------|-------------|--------|
| Phase 0 | Cleanup (xoá file trùng) | ✅ |
| Phase 1 | Domain layer tách biệt | ✅ |
| Phase 2 | Data layer chuẩn | ✅ |
| Phase 3 | UI hoàn thiện | ✅ |
| Phase 4 | Engines & Testing | ✅ (106 tests) |

## 6. Dependencies (sau refactor) — VERIFIED ✅

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
| KSP | 2.0.21-1.0.28 | Room annotation processing |

## 7. Risks & Mitigations (RESOLVED ✅)

| Risk | Resolution |
|---|---|
| Xoá nhầm class còn reference | ✅ Build kiểm tra trước khi xoá |
| Room migration conflict | ✅ `fallbackToDestructiveMigration()` trong dev |
| Hilt inject conflict 2 repository | ✅ @Binds kiểm tra không trùng |
| Engine logic sai khi test | ✅ 106 tests pass |

## 8. What Remains

- **Plan tab wiring**: PlanTabHost, PlanViewModel, GpaForecastScreen, FlashcardScreen
- **Flashcard persistence**: Room entities, DAO, Room database update
- **Compose UI tests**: Screen-level tests (not started)
- **Integration tests**: Repository layer tests (not started)
- **API formal spec**: Compare ApiService with backend OpenAPI
