# RESEARCH.md — Phase 1: Foundation & Data Sync

## 1. Sync Architecture (Offline-First)

The "Academic OS" uses the **Repository Pattern** with **Room** as the single source of truth.

### Data Flow:
`API` → `Repository` → `RoomDatabase` → `ViewModel` → `UI`

### Pattern: Stale-While-Revalidate
1. UI displays data from Room immediately.
2. Repository triggers a background API fetch.
3. On success, Room is updated.
4. UI observes Room changes and updates reactively (Flow/StateFlow).

### Implemented Components:
- `AcademicRepository.kt` — orchestrates Course/Repo data (refreshCourses, refreshRepos, getCourseGraph)
- `AuthRepositoryImpl.kt` — handles authentication via AuthInterceptor
- `DiscoveryRepositoryImpl.kt` — handles recent/discovery repo endpoints
- `ResourceRepositoryImpl.kt` — handles course resources
- `BookmarkRepositoryImpl.kt` — handles bookmarked repos
- `AiRepositoryImpl.kt` — handles AI summary endpoints

## 2. Room Schema Design (Actual)

### Entity: `CourseEntity`
- `id` (Long, Primary Key)
- `maMH` (String, Course Code)
- `tenMH` (String, Course Name)
- `credits` (Int)
- `semester` (Int?, nullable)
- `loaiMonHoc` (String?, nullable)
- `description` (String)

### Entity: `RepoEntity`
- `id` (Long, Primary Key)
- `courseId` (Long, Foreign Key → CourseEntity)
- `displayName` (String)
- `description` (String)
- `githubUrl` (String)
- `primaryLanguage` (String?)
- `stars` (Int)
- `techStack` (String? — serialized JSON)
- `aiClassification` (String)

### Entity: `CourseRelationshipEntity` (Prerequisites)
- `id` (Long, Primary Key)
- `fromCourseId` (Long)
- `toCourseId` (Long)
- `type` (String: PREREQUISITE, COREQUISITE)

### Entity: `TaskEntity`
- `id` (Long, Primary Key)
- `courseId` (Long?)
- `title` (String)
- `description` (String)
- `deadline` (Long?)
- `completed` (Boolean)
- `priority` (Int)

## 3. Navigation Structure (Actual)

### Bottom Navigation Tabs
Using `NavigationBar` from Material 3 in `MainScreen.kt`.
1. **Dashboard**: `DashboardScreen` (Mission Control)
2. **Explore**: `ExploreScreen` (Discovery)
3. **Courses**: `CourseHubScreen` (Course Hub with List/Graph toggle)
4. **Knowledge**: `KnowledgeHubScreen` (Knowledge Graph)
5. **Profile**: `ProfileScreen` (User profile)

### Routing
Routes defined in `Screen.kt` with Navigation Compose `NavHost` in `DevOrbitApp.kt`.

## 4. Dashboard (Mission Control) Strategy

The Dashboard uses a `LazyColumn` layout with specialized sections:
- Daily Briefing summary
- Upcoming deadlines/tasks
- Semester Risk gauge
- Navigation to Focus Mode, Study Planner, Syllabus Parser, Task Breakdown

## 5. API Integration Points

| Feature | Endpoint | Method |
|---------|----------|--------|
| Course List | `/api/courses` | GET |
| Course Summary | `/api/courses/summary` | GET |
| Repo Discovery | `/api/courses/{id}/repos` | GET |
| Recent Activity | `/api/discovery/recent-repos` | GET |
| Repo Summary | `/api/discovery/repo/{id}/summary` | GET |
| Bookmarked Repos | `/api/bookmarks` | GET |
| Resource by Course | `/api/resources/course/{courseId}` | GET |
| Dependency Graph | `/api/courses/relationships` | GET |
| Graph Nodes | `/api/courses/graph` | GET |
| AI Summary | `/api/ai/repo/{id}/summary` | GET |
| Auth Login | `/api/auth/login` | POST |
| Auth Register | `/api/auth/register` | POST |

## 6. Domain Engines

Nine business logic engines built in `domain/engine/`:
- `KnowledgeGraphEngine` — node level computation, impact scores, learning paths, weak node detection
- `BurnoutEngine` — burnout risk calculation based on workload
- `GpaEngine` — GPA projection and credit tracking
- `RiskEngine` — semester risk analysis
- `SimulationEngine` — academic scenario simulation
- `RecommendationEngine` — course/repo recommendations
- `StudyPlannerEngine` — study plan generation
- `TaskBreakdownEngine` — task decomposition
- `WorkloadEngine` — workload analysis and heatmap

## 7. Implementation Notes (Actual)
- **Retrofit** for API calls (`ApiService.kt`)
- **OkHttp** with `AuthInterceptor` and `HttpLoggingInterceptor`
- **Hilt** for DI (`DataModule.kt` + `RepositoryModule.kt` in `data/di/`)
- **Room** for offline caching (`DevOrbitDatabase.kt`)
- **DataStore** for app preferences (`SettingsDataStore.kt`)
- **Kotlin Flows** for reactive data from Room
- **Gson** for JSON serialization/deserialization (DTOs in `data/remote/dto/`)
- **Jetpack Compose** (BOM 2024.11.00) with Material 3
- **Navigation Compose** 2.8.4 for routing
- **Coil** for image loading (via repository summaries)
