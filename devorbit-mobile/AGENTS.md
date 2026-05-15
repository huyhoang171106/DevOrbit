<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# devorbit-mobile

## Purpose
Kotlin Android application for the DevOrbit platform, providing mobile access for UIT students to browse courses, explore GitHub repositories, track academic progress, and access AI-powered learning tools. Features offline caching via Room, DataStore preferences, and seamless integration with the DevOrbit API.

## Key Files
| File | Description |
|------|-------------|
| `build.gradle.kts` | Root Gradle build configuration |
| `app/build.gradle.kts` | App module Gradle configuration (Compose BOM 2024.11.00, Room 2.6.1, Hilt 2.51.1, Retrofit 2.11.0) |
| `settings.gradle.kts` | Gradle settings |
| `gradle.properties` | Gradle properties (4g heap) |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt` | Main Android activity (entry point) |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/DevOrbitApplication.kt` | Application class with Hilt |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/network/ApiService.kt` | Retrofit API interface defining all endpoints |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/di/DataModule.kt` | Hilt DI module (Retrofit, OkHttp, Room, Repository injection) |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/ui/DevOrbitApp.kt` | Root composable with NavHost routing |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/ui/MainScreen.kt` | Main scaffold with bottom navigation bar |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/ui/navigation/Screen.kt` | Route definitions for all screens |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `data/local/entity/` | Room entities (CourseEntity, CourseRelationshipEntity, RepoEntity, TaskEntity) |
| `data/local/dao/` | Room DAOs (CourseDao, RelationshipDao, RepoDao, TaskDao) |
| `data/local/` | Room database (DevOrbitDatabase) |
| `data/remote/dto/` | API DTOs (AuthDtos, CourseSummary, GraphResponse, KnowledgeModels, RepoSummary, TechStack, CourseRelationshipResponse) |
| `data/remote/interceptor/` | OkHttp interceptors (AuthInterceptor) |
| `data/repository/` | Repository implementations (AcademicRepository, AuthRepositoryImpl, DiscoveryRepositoryImpl, ResourceRepositoryImpl, BookmarkRepositoryImpl, AiRepositoryImpl) |
| `data/datastore/` | DataStore preferences (SettingsDataStore) |
| `data/di/` | DI modules (RepositoryModule) |
| `domain/engine/` | Business logic engines (BurnoutEngine, GpaEngine, KnowledgeGraphEngine, RecommendationEngine, RiskEngine, SimulationEngine, StudyPlannerEngine, TaskBreakdownEngine, WorkloadEngine) |
| `domain/model/` | Domain models (AcademicHealth, Analytics, BurnoutStatus, Flashcard, GpaImpact, KnowledgeGraph, LearningTask, Recommendation, RiskProfile, SemesterTimeline, SimulationResult, StudyPlan, SubjectNote, TaskBreakdown, Workload) |
| `domain/repository/` | Repository interfaces (AuthRepository, BookmarkRepository, DiscoveryRepository, ResourceRepository, AiRepository) |
| `ui/screen/auth/` | Auth screens (AuthScreen — combined login/register) |
| `ui/screen/courses/` | Course hub screens (CourseHubScreen, CourseListScreen, CourseHubNavigationState) |
| `ui/screen/dashboard/` | Dashboard screens (DashboardScreen, FocusModeScreen) |
| `ui/screen/explore/` | Explore screen (ExploreScreen) |
| `ui/screen/knowledge/` | Knowledge graph screens (KnowledgeHubScreen, KnowledgeGraphScreen, KnowledgeDetailScreen) |
| `ui/screen/plan/` | Study planner screens (StudyPlannerScreen, SyllabusParserScreen, TaskBreakdownScreen) |
| `ui/screen/profile/` | Profile screen (ProfileScreen) |
| `ui/components/` | Reusable UI components (CosmicBackground, GalaxyGraphCanvas, GlassCard) |
| `ui/navigation/` | Navigation routing (Screen.kt) |
| `ui/theme/` | Cosmic/galaxy theme (Color.kt, DesignSystem.kt, Theme.kt) |
| `ui/viewmodel/` | ViewModels (AcademicViewModel, AuthViewModel, CourseViewModel, ExploreViewModel, KnowledgeViewModel, ProfileViewModel) |
| `ui/` | Top-level UI files (DevOrbitApp.kt, MainScreen.kt, CourseDetailScreen.kt, RepoDetailScreen.kt, RepoListSection.kt, RepoFilterSheet.kt, RepoFilterState.kt) |
| `app/src/main/res/` | Android resources (layouts, drawables, values) |
| `gradle/` | Gradle wrapper files (Windows .bat only) |

## For AI Agents

### Working In This Directory
- Build with Gradle: `./gradlew :app:assembleDebug` (Windows: `gradlew.bat :app:assembleDebug`)
- Follow Kotlin Android development best practices
- Repository pattern for data management (API -> Room -> UI flow)
- Offline-first approach with Room caching and stale-while-revalidate
- Hilt for dependency injection (see DataModule.kt, RepositoryModule.kt)
- Compose UI framework with cosmic/galaxy theme (see Theme.kt, DesignSystem.kt, Color.kt)

### Testing Requirements
- Unit tests in `app/src/test/java/vn/edu/uit/devorbit/mobile/`
- Engine tests: BurnoutEngineTest, GpaEngineTest, KnowledgeGraphEngineTest, RecommendationEngineTest, RiskEngineTest, SimulationEngineTest, StudyPlannerEngineTest, TaskBreakdownEngineTest, WorkloadEngineTest
- Model tests: DeserializationTest (API response parsing)
- UI state tests: RepoFilterStateTest, CourseHubNavigationStateTest
- Test on multiple Android versions
- Verify offline functionality (Room + DataStore)
- Test API integration and error handling

### Common Patterns
- Jetpack Compose for UI (Material 3)
- Repository pattern for data access (AcademicRepository)
- Kotlin coroutines + StateFlow for reactive state
- Room for offline caching (DevOrbitDatabase)
- DataStore for app preferences (SettingsDataStore)
- Retrofit/OkHttp for network calls (ApiService)
- Hilt for dependency injection (DataModule, RepositoryModule)
- ViewModel + StateFlow for reactive UI state
- Domain engines for complex business logic (KnowledgeGraphEngine, BurnoutEngine, etc.)

## Dependencies

### Internal
- `../devorbit-api/` - Backend API endpoints
- `app/src/main/java/vn/edu/uit/devorbit/mobile/network/ApiService.kt` - API integration

### External
- Kotlin 2.0.21 - Programming language
- Jetpack Compose (BOM 2024.11.00) - UI framework with Material 3
- Navigation Compose 2.8.4 - Screen navigation
- Hilt 2.51.1 - Dependency injection
- Room 2.6.1 - Local database (offline caching)
- DataStore 1.1.1 - Preferences storage
- Retrofit 2.11.0 - HTTP client
- OkHttp 4.12.0 - HTTP networking + logging interceptor
- Gson - JSON serialization
- Jetpack ViewModel + StateFlow - Reactive state
- Kotlin Coroutines 1.9.0 - Async programming
- Coil - Image loading (via devorbit-api responses)

<!-- MANUAL: Keep Key Files and Subdirectories in sync with the codebase. After adding/removing screens, entities, engines, or modules, update those tables. -->
