# DevOrbit Android Apps — Technical Documentation

> Generated from source code analysis of `devorbit-admin/`, `devorbit-mobile/`, and cross-referenced with `devorbit-api/` backend controllers.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Shared Technology Stack](#2-shared-technology-stack)
3. [devorbit-admin (Admin Management App)](#3-devorbit-admin-admin-management-app)
4. [devorbit-mobile (Student Client App)](#4-devorbit-mobile-student-client-app)
5. [API Endpoints — Admin App](#5-api-endpoints--admin-app)
6. [API Endpoints — Mobile App](#6-api-endpoints--mobile-app)
7. [Authentication & Token Management](#7-authentication--token-management)
8. [Backend Controller Cross-Reference](#8-backend-controller-cross-reference)
9. [Build & Development](#9-build--development)

---

## 1. Overview

DevOrbit is a university course repository and learning platform for UIT (University of Information Technology, Vietnam National University HCMC). The Android ecosystem consists of two independent apps sharing a common Spring Boot backend (`devorbit-api/`):

| Aspect | `devorbit-admin` | `devorbit-mobile` |
|---|---|---|
| **Package** | `vn.edu.uit.devorbit.admin` | `vn.edu.uit.devorbit.mobile` |
| **Role** | Admin dashboard — manage courses, repos, students, reviews, community moderation | Student client — browse courses, AI tutoring, task planning, community chat |
| **Auth prefix** | `/api/admin/` | `/api/student/` + public `/api/` |
| **Local DB** | None (stateless, remote-only) | Room (`devorbit_db`, version 11) with 6 entities |
| **Theme** | Dark "Obsidian" palette | Light "Cosmic" / UIT 2026 Light Edition |
| **Realtime** | None | STOMP over WebSocket for community chat |
| **AI features** | GitHub auto-approval, scan automation | AI Tutor (streaming SSE), knowledge graph, roadmap generation |
| **Navigation** | Bottom nav (4 primary) + Command Menu overflow (9 secondary) | Floating pill bottom nav (5 tabs) + full-screen overlays |

---

## 2. Shared Technology Stack

Both apps share the same core dependencies and patterns:

| Component | Library / Version |
|---|---|
| **Language** | Kotlin 2.0.21, JVM target 17 |
| **UI** | Jetpack Compose (BOM 2024.11.00), Material 3 |
| **DI** | Dagger Hilt 2.51.1 + `hilt-navigation-compose` |
| **Networking** | Retrofit 2.11.0 + Gson converter + OkHttp 4.12.0 logging interceptor |
| **Async** | Kotlin Coroutines 1.9.0 + Flow |
| **Preferences** | DataStore Preferences 1.1.1 |
| **Navigation** | Navigation Compose 2.8.4 |
| **Lifecycle** | ViewModel Compose 2.8.7, Lifecycle Runtime Compose 2.8.7 |
| **Adaptive** | Material 3 Adaptive (1.0.0) — adaptive layout, navigation suite |
| **Android SDK** | compileSdk 35, minSdk 26, targetSdk 35 |
| **AGP** | 8.13.2 |
| **Build** | `.env` → `BuildConfig.API_BASE_URL` loaded via `loadDotEnv()` in `build.gradle.kts` |
| **Auth** | JWT Bearer tokens, auto-refresh on 401 via interceptor |

---

## 3. devorbit-admin (Admin Management App)

### 3.1 Architecture

```
vn.edu.uit.devorbit.admin/
├── data/
│   ├── datastore/AdminSettingsDataStore.kt    — DataStore for token, username
│   ├── remote/
│   │   ├── dto/                               — 10 DTO files (Auth, Community, Course, Github, Note, Notification, Repo, Stats, Student, TechStack)
│   │   └── interceptor/
│   │       ├── AuthInterceptor.kt             — Bearer token injection + auto-refresh on 401
│   │       └── RetryInterceptor.kt            — Request retry logic
│   └── repository/AdminRepositoryImpl.kt      — Single repository wrapping all API calls
├── di/AdminModule.kt                          — Hilt module: OkHttpClient, Retrofit, ApiService, Repository
├── domain/repository/AdminRepository.kt       — Repository interface
├── navigation/Routes.kt                       — Legacy sealed class routes (Today, Subjects, Tutor, StudyPlan, Profile)
├── network/AdminApiService.kt                 — Retrofit interface (all admin endpoints)
├── ui/
│   ├── AdminRoot.kt                           — Top-level composable with NavHost
│   ├── components/ObsidianComponents.kt       — Reusable dark-theme UI components
│   ├── navigation/AdminNavigation.kt          — AdminRoutes, PrimaryTab, AdminScreen definitions
│   ├── theme/                                 — Color, Palette, Shapes, Theme, Type, FieldLabels
│   ├── login/                                 — AdminLoginScreen + ViewModel
│   ├── dashboard/                             — DashboardScreen + ViewModel (stats overview)
│   ├── students/                              — StudentsScreen + ViewModel (list, search, toggle active)
│   ├── courses/                               — CoursesScreen, CourseDetailScreen, CourseRelationshipsScreen + ViewModels
│   ├── repos/                                 — ReposScreen + ViewModel (list, update, delete, sync, evaluate)
│   ├── candidates/                            — CandidatesScreen + ViewModel (repo candidate review/approve/reject)
│   ├── reviews/                               — ReviewsScreen + ViewModel (admin manage student reviews)
│   ├── github/                                — GithubScreen (scan, logs, automation) + AutoApprovalScreen + ViewModels
│   ├── community/                             — CommunityScreen + ViewModel (channels, messages, chat sessions)
│   ├── techstack/                             — TechStackScreen + ViewModel (CRUD tech stack tags)
│   ├── notes/                                 — NotesScreen + ViewModel (view/delete admin notes)
│   ├── notifications/                         — NotificationsScreen + ViewModel (list, mark read)
│   └── reports/                               — ReportsScreen + ViewModel
├── DevOrbitAdminApp.kt                        — HiltAndroidApp
└── MainActivity.kt                            — Sets content to AdminRoot()
```

### 3.2 Navigation

**Primary tabs** (bottom nav bar):
| Tab | Route | Icon | Label |
|---|---|---|---|
| Tổng quan | `dashboard` | `Dashboard` | Dashboard stats |
| Sinh viên | `students` | `People` | Student list |
| Môn học | `courses` | `MenuBook` | Course management |
| Kho | `repos` | `Folder` | Repository management |

**Secondary screens** (accessed via Command Menu overflow):
| Screen | Route | Category |
|---|---|---|
| Duyệt kho | `candidates` | Quản lý |
| Đánh giá | `reviews` | Quản lý |
| Quét GitHub | `github` | Công cụ |
| Tự động duyệt kho | `auto-approval` | Công cụ |
| Cộng đồng | `community` | Quản lý |
| Công nghệ | `techstack` | Công cụ |
| Báo cáo | `reports` | Quản lý |
| Ghi chú | `notes` | Quản lý |
| Thông báo | `notifications` | Hệ thống |

**Deep-link routes**: `courses/{courseId}` (course detail), `course-relationships` (dependency graph editor).

### 3.3 DI Module (`AdminModule`)

```
Provides (all @Singleton):
├── HttpLoggingInterceptor   — BODY level in DEBUG, NONE in release
├── RetryInterceptor
├── AuthInterceptor          — holds volatile token/refreshToken, auto-refresh on 401
├── OkHttpClient             — 30s timeouts, auth + retry + logging interceptors
├── Retrofit                 — base URL from BuildConfig.API_BASE_URL
├── AdminApiService          — retrofit.create()
└── AdminRepository          — binds AdminRepositoryImpl
```

### 3.4 Data Layer

- **No local database** — all data is fetched from the remote API.
- `AdminSettingsDataStore` persists: JWT token, refresh token, admin username.
- `AdminRepositoryImpl` wraps every API call in `safeApiCall { }` for `Result<T>` handling with proper coroutine cancellation propagation.

### 3.5 Theme

Dark "Obsidian" theme with:
- `ObsidianPalette` — dark background, UIT Blue accents, semantic status colors
- `ObsidianType` — custom typography scale
- `ObsidianShape` — rounded shapes for cards/chips
- `ObsidianComponents` — reusable dark-theme composables (avatars, status chips, section headers)

---

## 4. devorbit-mobile (Student Client App)

### 4.1 Architecture

```
vn.edu.uit.devorbit.mobile/
├── data/
│   ├── auth/TokenRefreshManager.kt            — Coroutine-based token refresh with mutex
│   ├── datastore/SettingsDataStore.kt         — DataStore for token, refresh token, student info
│   ├── local/
│   │   ├── DevOrbitDatabase.kt                — Room database (v11, 6 entities)
│   │   ├── DevOrbitMigrations.kt              — Room migration definitions
│   │   ├── dao/                               — 6 DAOs: Course, Repo, Relationship, DailyActivity, SemesterCourse, TechStack
│   │   └── entity/                            — 6 entities matching DAOs
│   ├── remote/
│   │   ├── dto/                               — 22+ DTO files covering all API responses
│   │   └── interceptor/AuthInterceptor.kt     — Bearer token injection + auto-refresh via TokenRefreshManager
│   ├── di/
│   │   ├── RepositoryModule.kt                — Binds Auth, Discovery, Bookmark, StudyPlan, SubjectQa repos
│   │   └── CommunityModule.kt                 — Binds CommunityRepo + provides StompClient
│   ├── repository/
│   │   ├── AuthRepositoryImpl.kt              — Auth: register, login, verify OTP, forgot/reset password, profile, avatar
│   │   ├── AcademicRepository.kt              — Courses, repos, tutorials, videos, articles, reviews, knowledge graph
│   │   ├── SubjectQaRepositoryImpl.kt         — AI Tutor: ask + streaming SSE
│   │   ├── CommunityRepositoryImpl.kt         — Community: channels, messages, STOMP WebSocket
│   │   ├── BookmarkRepositoryImpl.kt          — Student bookmarks CRUD
│   │   ├── DiscoveryRepositoryImpl.kt         — Discovery: recent repos, search, top stacks
│   │   ├── StudyPlanRepositoryImpl.kt         — AI roadmap generation
│   │   ├── StreakTracker.kt                   — Daily streak tracking (repos viewed ≥ 3/day)
│   │   ├── DiscoveryMappers.kt                — DTO → domain mapping
│   │   └── StudyPlanMappers.kt                — DTO → domain mapping
│   └── ConnectivityObserver.kt                — Network connectivity monitoring
├── di/DataModule.kt                           — Hilt module: OkHttp, Retrofit, ApiService, Room DB + 6 DAOs
├── domain/
│   ├── model/                                 — TaskItem, TaskBreakdown, KnowledgeGraph, LearningTask, StudyPlan
│   └── repository/                            — 6 repository interfaces: Auth, Bookmark, Community, Discovery, StudyPlan, SubjectQa
├── network/
│   ├── ApiService.kt                          — Retrofit interface (all student/public endpoints)
│   └── stomp/
│       ├── StompClient.kt                     — Raw WebSocket STOMP client with auto-reconnect + heartbeat
│       ├── StompEventListener.kt              — Callback interface for STOMP events
│       └── StompFrame.kt                      — STOMP frame serialization/parsing
├── ui/
│   ├── MainScreen.kt                          — Root composable with floating pill bottom nav + NavHost
│   ├── DevOrbitApp.kt                         — HiltAndroidApp
│   ├── components/
│   │   ├── CalendarViews.kt                   — Calendar/date picker views
│   │   ├── WheelTimePicker.kt                 — Custom wheel-style time picker
│   │   ├── GlassCard.kt                       — Glassmorphism card component
│   │   ├── DevOrbitLogo.kt                    — Custom logo composable
│   │   ├── CosmicBackground.kt                — Light cosmic-themed background
│   │   └── GalaxyGraphCanvas.kt               — Interactive course dependency graph canvas
│   ├── navigation/Screen.kt                   — Screen sealed class definitions
│   ├── screen/
│   │   ├── auth/AuthScreen.kt                 — Login, register, OTP, forgot password
│   │   ├── dashboard/
│   │   │   ├── DashboardScreen.kt             — Home: greeting, streak, focus tasks, AI suggestions
│   │   │   └── TaskManagementScreen.kt        — Personal tasks + group plan tabs
│   │   ├── courses/
│   │   │   ├── CourseHubScreen.kt             — Course list + galaxy graph toggle
│   │   │   ├── CourseListScreen.kt            — Filterable course list
│   │   │   ├── CourseSearchFilterState.kt     — Search/filter state
│   │   │   └── CourseHubNavigationState.kt    — Navigation state
│   │   ├── plan/
│   │   │   ├── SubjectQaScreen.kt             — AI Tutor chat interface
│   │   │   ├── GroupPlanListScreen.kt         — List of group plans
│   │   │   ├── GroupPlanDetailScreen.kt       — Group plan detail + task management
│   │   │   └── AddTaskSheet.kt               — Bottom sheet for task creation
│   │   ├── community/
│   │   │   ├── CommunityScreen.kt             — Community channel list + chat
│   │   │   ├── ChannelListContent.kt          — Channel list UI
│   │   │   ├── ChatMessageBubble.kt           — Message bubble composable
│   │   │   ├── ChatInputBar.kt               — Message input bar
│   │   │   └── OnlineMembersSheet.kt         — Online members bottom sheet
│   │   ├── knowledge/
│   │   │   ├── KnowledgeTabView.kt            — Knowledge tab container
│   │   │   └── KnowledgeGraphScreen.kt        — Interactive knowledge graph
│   │   ├── explore/
│   │   │   ├── ExploreScreen.kt               — Discovery / explore repos
│   │   │   └── ExploreFilterState.kt          — Explore filter state
│   │   ├── notification/NotificationScreen.kt — Notification list
│   │   └── profile/
│   │       ├── ProfileScreen.kt               — Student profile + settings
│   │       └── ProfileDetailScreen.kt         — Edit profile overlay
│   ├── viewmodel/                             — 16 ViewModels
│   │   ├── AuthViewModel.kt                   — Auth flow management
│   │   ├── DashboardViewModel.kt              — Dashboard data + streak
│   │   ├── CourseViewModel.kt                 — Course browsing + detail
│   │   ├── TaskManagementViewModel.kt         — Personal tasks CRUD
│   │   ├── GroupPlanViewModel.kt              — Group plan management
│   │   ├── SubjectQaViewModel.kt              — AI Tutor streaming
│   │   ├── CommunityViewModel.kt              — Community chat
│   │   ├── NotificationViewModel.kt           — Notifications
│   │   ├── ProfileViewModel.kt                — Profile + settings
│   │   ├── StudyPlanViewModel.kt              — Study plans
│   │   ├── KnowledgeViewModel.kt              — Knowledge graph
│   │   ├── ExploreViewModel.kt                — Discovery
│   │   ├── AcademicViewModel.kt               — Academic data coordination
│   │   ├── BaseViewModel.kt                   — Shared base class
│   │   ├── AuthValidation.kt                  — Input validation
│   │   └── AuthSessionPolicy.kt              — Session management
│   └── theme/
│       ├── Color.kt                           — Light color tokens (PrimaryBlue, WarmWhite, etc.)
│       ├── DesignSystem.kt                    — CompositionLocals: CosmicColors, CosmicGradients, CosmicTypography, CosmicSpacing
│       └── Theme.kt                           — DevOrbitTheme with light color scheme + edge-to-edge
├── CourseDetailScreen.kt                       — Course detail with repos, tutorials, videos, articles
├── RepoDetailScreen.kt                         — Repository detail (31KB, largest screen)
├── RepoListSection.kt                          — Reusable repo list section
├── TutorialDetailScreen.kt                     — Tutorial detail view
├── RepoFilterSheet.kt                          — Repo filter bottom sheet
└── RepoFilterState.kt                          — Repo filter state
```

### 4.2 Navigation

**Floating pill bottom nav** (5 primary tabs):
| Tab | Route | Icon | Label |
|---|---|---|---|
| Hôm nay | `dashboard` | `Home` | Dashboard |
| Môn học | `subjects` | `Book` | Course hub |
| AI Tutor | `tutor` | `AutoAwesome` | AI chat |
| Kế hoạch | `plan` | `DateRange` | Task management |
| Cộng đồng | `community` | `Chat` | Community chat |

**Additional routes** (accessible from primary tabs):
| Route | Description |
|---|---|
| `notifications` | Notification list |
| `profile` | Student profile |
| `group_plan_list` | Group plan list |
| `group_plan_detail/{planId}` | Group plan detail (parameterized) |

**Design features**:
- Floating pill nav with 24dp corner radius, 1dp border, 6dp elevation
- WarmWhite background, UIT Blue active state with PrimaryContainer highlight
- Navigation transitions: fade in/out (220ms/180ms)
- Profile detail opens as a full-screen overlay (not a nav destination)

### 4.3 Room Database (`devorbit_db`, v11)

| Entity | Table | Key Fields |
|---|---|---|
| `CourseEntity` | courses | id, maMH, tenMH, credits, description, semester, loaiMonHoc, repoCount |
| `RepoEntity` | repos | id, courseId, displayName, description, githubUrl, primaryLanguage, stars, aiClassification |
| `CourseRelationshipEntity` | relationships | id, fromCourseId, toCourseId, type |
| `DailyActivityEntity` | daily_activities | studentCode, date, reposViewed, tasksCompleted, tasksTotal |
| `SemesterCourseEntity` | semester_courses | studentCode, courseId, semester |
| `TechStackEntity` | tech_stacks | id, name |

**DAOs** (6 total):
- `CourseDao` — upsertCourses, deleteAll, getAllCourses (returns `Flow`)
- `RepoDao` — upsertRepos, getReposByCourse (returns `Flow`), getRecentRepos (returns `Flow`)
- `RelationshipDao` — upsertRelationships, getAllRelationships (returns `Flow`)
- `DailyActivityDao` — upsertActivity, getActivity(studentCode, date)
- `SemesterCourseDao` — upsert, getAll(studentCode), delete(studentCode, courseId)
- `TechStackDao` — upsert, getAll, deleteByName

**Migrations**: Managed by `DevOrbitMigrations.kt` (11 versions, cumulative schema evolution).

### 4.4 DI Modules

The mobile app uses **three** Hilt modules:

#### `DataModule` (`di/DataModule.kt`) — Infrastructure

```
Provides (all @Singleton):
├── HttpLoggingInterceptor
├── AuthInterceptor              — delegates to TokenRefreshManager for refresh
├── OkHttpClient                 — 30s timeouts
├── Retrofit                     — base URL from BuildConfig.API_BASE_URL
├── ApiService                   — retrofit.create()
├── DevOrbitDatabase             — Room.databaseBuilder("devorbit_db")
└── 6 DAOs                       — CourseDao, RepoDao, RelationshipDao,
                                   DailyActivityDao, SemesterCourseDao, TechStackDao
```

#### `RepositoryModule` (`data/di/RepositoryModule.kt`) — Repository Bindings

```
Binds (all @Singleton, interface → implementation):
├── AuthRepository        → AuthRepositoryImpl
├── DiscoveryRepository   → DiscoveryRepositoryImpl
├── BookmarkRepository    → BookmarkRepositoryImpl
├── StudyPlanRepository   → StudyPlanRepositoryImpl
└── SubjectQaRepository   → SubjectQaRepositoryImpl
```

#### `CommunityModule` + `StompModule` (`data/di/CommunityModule.kt`) — Community & WebSocket

```
Binds:
└── CommunityRepository   → CommunityRepositoryImpl

Provides:
└── StompClient           — singleton raw WebSocket STOMP client
```

> **Not Hilt-bound via modules**: `AcademicRepository` is directly `@Inject`-constructed by `AcademicViewModel` (receives `ApiService`, `CourseDao`, `RepoDao`, `RelationshipDao`). `StreakTracker` is similarly injected where needed (receives `DailyActivityDao`, `SettingsDataStore`).

### 4.5 Repositories

| Repository | Injected Dependencies | Responsibility |
|---|---|---|
| `AuthRepositoryImpl` | ApiService, SettingsDataStore, TokenRefreshManager, Context | Register, login, OTP verify, forgot/reset password, profile CRUD, avatar upload, logout |
| `AcademicRepository` | ApiService, CourseDao, RepoDao, RelationshipDao | Course list/detail, repos, tutorials, videos, articles, relationships, knowledge graph, reviews, AI summary/advice |
| `SubjectQaRepositoryImpl` | ApiService | AI Tutor: synchronous `ask()` + streaming `stream()` via SSE (`@Streaming` POST) |
| `CommunityRepositoryImpl` | ApiService, StompClient | Channels, paginated messages, STOMP WebSocket connect/disconnect/subscribe/send, image upload |
| `BookmarkRepositoryImpl` | ApiService | Bookmarks CRUD (list, add, remove, check if bookmarked) |
| `DiscoveryRepositoryImpl` | ApiService | Tech stacks, recent repos, search repos, top stacks |
| `StudyPlanRepositoryImpl` | ApiService | AI roadmap generation (`POST /api/ai/generate-roadmap`) |
| `StreakTracker` | DailyActivityDao, SettingsDataStore | Tracks daily repos viewed; increments streak when ≥ 3 repos/day for consecutive days; emits `SharedFlow<Int>` on streak change |

### 4.6 Theme — UIT 2026 Light Edition

Documented in `DESIGN.md`. Key design decisions:

- **100% Light Mode** — no dark mode support
- **Color tokens**: `PrimaryBlue (#0056B3)`, `WarmWhite (#FAFAFA)`, `AccentCyan (#00B4D8)`, semantic success/warning/error
- **Typography**: Inter/Roboto system sans-serif, 6 tokens (Display 26sp → Metric 36sp)
- **Spacing**: 8dp grid (4dp Atomic → 32dp ExtraLarge)
- **Elevation**: Flat (0dp) → High (12dp) for modals
- **CompositionLocals**: `CosmicColors`, `CosmicGradients`, `CosmicTypography`, `CosmicSpacing`
- **Edge-to-edge**: Transparent status/navigation bars, dark icons on light bars
- **WCAG AA+**: 13.5:1 contrast for primary text, 7.8:1 for secondary
- **48dp minimum touch targets**

### 4.7 Realtime — STOMP WebSocket

The community chat uses a raw WebSocket STOMP client (`StompClient.kt`):

- **Connection**: `ws://{API_BASE_URL}/ws/community/websocket` (SockJS transport URL; the server registers `/ws/community` in `WebSocketConfig`)
- **Auth**: JWT is sent as `Authorization: Bearer {token}` inside the STOMP `CONNECT` frame header (`StompFrame.connect(token)`), not via the WebSocket handshake URL. Backend validates the token on the STOMP `CONNECT` frame.
- **Protocol**: STOMP frames over WebSocket (CONNECT, SUBSCRIBE, SEND, MESSAGE)
- **Subscriptions**:
  - `/topic/channel/{channelId}` — messages
  - `/topic/channel/{channelId}/presence` — online members
- **Send destination**: `/app/chat.send/{channelId}`
- **Features**: Auto-reconnect with exponential backoff, heartbeat, subscription persistence across reconnects

### 4.8 ViewModels

| ViewModel | Key Dependencies | Key State |
|---|---|---|
| `AuthViewModel` | AuthRepository, SettingsDataStore | Auth flow (login/register/OTP), session management |
| `DashboardViewModel` | AcademicRepository, StreakTracker, TaskManagementViewModel | Greeting, streak count, today's focus tasks, AI suggestions |
| `CourseViewModel` | AcademicRepository, CourseDao | Course list, course detail, repos, filter state |
| `TaskManagementViewModel` | ApiService (direct) | Personal tasks CRUD, filter (all/completed/pending), group tasks |
| `GroupPlanViewModel` | ApiService (direct) | Group plan CRUD, member management, task assignment |
| `SubjectQaViewModel` | SubjectQaRepository | AI chat messages, streaming state, session management |
| `CommunityViewModel` | CommunityRepository | Channels, messages, WebSocket lifecycle, online members |
| `NotificationViewModel` | ApiService (direct) | Notification list, unread count, mark read |
| `ProfileViewModel` | AuthRepository, ApiService, BookmarkRepository | Student profile, settings, bookmarks |
| `StudyPlanViewModel` | StudyPlanRepository | Roadmap generation inputs |
| `KnowledgeViewModel` | AcademicRepository | Knowledge graph data + query |
| `ExploreViewModel` | DiscoveryRepository | Recent repos, search, top stacks |

---

## 5. API Endpoints — Admin App

All endpoints are prefixed with `/api/admin/` and require JWT Bearer authentication.

> Cross-referenced with `devorbit-api/` controllers — see Section 8 for full mapping.

### 5.1 Auth

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `POST` | `/api/admin/auth/login` | Admin login | `AdminAuthController` |
| `POST` | `/api/admin/auth/logout` | Admin logout | `AdminAuthController` |
| `POST` | `/api/auth/refresh` | Refresh access token | `AuthController` |

### 5.2 Dashboard Stats

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/stats?sortBy=` | Get dashboard statistics | `AdminStatsController` |

### 5.3 Students

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/students?search=` | List/search students | `AdminStudentController` |
| `PUT` | `/api/admin/students/{id}/toggle-active` | Toggle student active status | `AdminStudentController` |

### 5.4 Courses

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/courses` | List all courses | `AdminCourseController` |
| `GET` | `/api/courses/{id}` | Get course detail (public) | `PublicCourseController` |
| `POST` | `/api/admin/courses` | Create course | `AdminCourseController` |
| `PUT` | `/api/admin/courses/{id}` | Update course | `AdminCourseController` |
| `DELETE` | `/api/admin/courses/{id}` | Delete course | `AdminCourseController` |

### 5.5 Course Resources (Tutorials, YouTube, Articles)

All under `/api/admin/courses/{courseId}/resources/`:

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET/POST` | `/tutorials` | List / Create tutorials | `AdminCourseResourceController` |
| `PUT/DELETE` | `/tutorials/{tutorialId}` | Update / Delete tutorial | `AdminCourseResourceController` |
| `GET/POST` | `/youtube-playlists` | List / Create YouTube playlists | `AdminCourseResourceController` |
| `PUT/DELETE` | `/youtube-playlists/{playlistId}` | Update / Delete playlist | `AdminCourseResourceController` |
| `GET/POST` | `/articles` | List / Create articles | `AdminCourseResourceController` |
| `PUT/DELETE` | `/articles/{articleId}` | Update / Delete article | `AdminCourseResourceController` |

### 5.6 Course Relationships

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/courses/relationships` | List all relationships | `AdminCourseRelationshipController` |
| `GET` | `/api/admin/courses/relationships/course/{courseId}` | Get course-specific relationships | `AdminCourseRelationshipController` |
| `POST` | `/api/admin/courses/relationships` | Create relationship | `AdminCourseRelationshipController` |
| `DELETE` | `/api/admin/courses/relationships/{id}` | Delete relationship | `AdminCourseRelationshipController` |

### 5.7 Repos

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/repos` | List all repos | `AdminRepoController` |
| `PUT` | `/api/admin/repos/{repoId}` | Update repo | `AdminRepoController` |
| `DELETE` | `/api/admin/repos/{repoId}` | Delete repo | `AdminRepoController` |
| `POST` | `/api/admin/repos/{repoId}/sync` | Sync repo metadata from GitHub | `AdminRepoController` |
| `POST` | `/api/admin/repos/evaluate-all` | Trigger AI evaluation for all repos | `AdminRepoController` |

### 5.8 Repo Candidates

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/repo-candidates?reviewer=` | List pending candidates | `AdminRepoCandidateController` |
| `GET` | `/api/admin/repo-candidates/stats` | Reviewer statistics | `AdminRepoCandidateController` |
| `POST` | `/api/admin/repo-candidates/{candidateId}/approve` | Approve candidate | `AdminRepoCandidateController` |
| `POST` | `/api/admin/repo-candidates/{candidateId}/reject` | Reject candidate | `AdminRepoCandidateController` |

### 5.9 Reviews

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/reviews/repos` | List repo reviews | `AdminRepoReviewController` |
| `DELETE` | `/api/admin/reviews/repos/{id}` | Delete repo review | `AdminRepoReviewController` |
| `GET` | `/api/admin/reviews/courses` | List course reviews | `AdminCourseReviewController` |
| `DELETE` | `/api/admin/reviews/courses/{id}` | Delete course review | `AdminCourseReviewController` |

### 5.10 GitHub Automation

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `POST` | `/api/admin/github/scan` | Scan GitHub for repos | `AdminGithubController` |
| `POST` | `/api/admin/github/scan-all` | Scan all courses | `AdminGithubController` |
| `GET` | `/api/admin/github/scan-logs` | Get scan logs | `AdminGithubController` |
| `DELETE` | `/api/admin/github/scan-logs` | Clear scan logs | `AdminGithubController` |
| `GET` | `/api/admin/github/automation-status` | Get automation status | `AdminGithubController` |
| `GET` | `/api/admin/github/auto-approvals` | List auto-approved repos | `AdminGithubController` |
| `POST` | `/api/admin/github/auto-approve` | Run auto-approval | `AdminGithubController` |

### 5.11 Community

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/admin/community/channels` | List channels | `AdminCommunityController` |
| `GET` | `/api/admin/community/messages` | List messages | `AdminCommunityController` |
| `DELETE` | `/api/admin/community/messages/{id}` | Delete message | `AdminCommunityController` |
| `GET` | `/api/admin/chat/sessions` | List chat sessions | `AdminChatController` |
| `GET` | `/api/admin/chat/sessions/{id}/messages` | Get chat messages | `AdminChatController` |

### 5.12 Tech Stack, Notes, Notifications

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET/POST` | `/api/admin/techstack` | List / Create tech stacks | `AdminTechStackController` |
| `DELETE` | `/api/admin/techstack/{id}` | Delete tech stack | `AdminTechStackController` |
| `GET` | `/api/admin/notes` | List notes | `AdminNoteController` |
| `DELETE` | `/api/admin/notes/{id}` | Delete note | `AdminNoteController` |
| `GET` | `/api/admin/notifications` | List notifications | `AdminNotificationController` |
| `GET` | `/api/admin/notifications/unread-count` | Get unread count | `AdminNotificationController` |
| `PUT` | `/api/admin/notifications/{id}/read` | Mark as read | `AdminNotificationController` |
| `PUT` | `/api/admin/notifications/read-all` | Mark all as read | `AdminNotificationController` |

---

## 6. API Endpoints — Mobile App

All student endpoints are prefixed with `/api/student/` and require JWT Bearer authentication. Public endpoints use `/api/` without auth.

> Cross-referenced with `devorbit-api/` controllers — see Section 8 for full mapping.

### 6.1 Public Course & Repo Endpoints

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/courses?q=&subjectType=&semester=&managementUnit=` | List courses (filtered) | `PublicCourseController` |
| `GET` | `/api/courses/{id}` | Course detail | `PublicCourseController` |
| `GET` | `/api/courses/{id}/tutorials` | Course tutorials | `PublicCourseController` |
| `GET` | `/api/courses/{id}/videos` | Course YouTube playlists | `PublicCourseController` |
| `GET` | `/api/courses/{id}/articles` | Course articles | `PublicCourseController` |
| `GET` | `/api/courses/relationships` | All course relationships | `PublicCourseRelationshipController` |
| `GET` | `/api/courses/relationships/course/{courseId}` | Course-specific relationships | `PublicCourseRelationshipController` |
| `GET` | `/api/courses/graph` | Knowledge graph data | `PublicCourseController` |
| `GET` | `/api/courses/{courseId}/reviews` | Course reviews | `PublicSocialController` |
| `GET` | `/api/courses/{courseId}/repos` | Repos for a course | `PublicRepoController` |
| `GET` | `/api/repos/{repoId}` | Repo detail | `PublicRepoController` |
| `GET` | `/api/repos/{repoId}/social-info` | Repo social info (votes, reviews) | `PublicSocialController` |

### 6.2 Discovery

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/discovery/recent-repos` | Recent repos | `PublicDiscoveryController` |
| `GET` | `/api/discovery/repos?q=` | Search repos | `PublicDiscoveryController` |
| `GET` | `/api/discovery/top-stacks` | Top tech stacks | `PublicDiscoveryController` |
| `GET` | `/api/tech-stacks` | All tech stacks | `PublicTechStackController` |

### 6.3 Student Auth

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `POST` | `/api/student/login` | Login with student code + password | `StudentAuthController` |
| `POST` | `/api/student/register` | Register new student | `StudentAuthController` |
| `POST` | `/api/student/verify-otp` | Verify OTP code | `StudentAuthController` |
| `POST` | `/api/student/resend-otp` | Resend OTP | `StudentAuthController` |
| `POST` | `/api/student/forgot-password` | Request password reset | `StudentAuthController` |
| `POST` | `/api/student/reset-password` | Reset password with OTP | `StudentAuthController` |
| `POST` | `/api/student/logout` | Logout | `StudentAuthController` |
| `POST` | `/api/auth/refresh` | Refresh access token | `AuthController` |

### 6.4 Student Profile

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/me` | Get profile | `StudentAuthController` |
| `POST` | `/api/student/me/avatar/upload` | Upload avatar (multipart) | `StudentAuthController` |
| `POST` | `/api/student/me/name` | Update full name | `StudentAuthController` |
| `POST` | `/api/student/me/password` | Change password | `StudentAuthController` |

### 6.5 Bookmarks

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/bookmarks` | List bookmarks | `StudentBookmarkController` |
| `POST` | `/api/student/bookmarks` | Add bookmark | `StudentBookmarkController` |
| `DELETE` | `/api/student/bookmarks/{id}` | Delete bookmark | `StudentBookmarkController` |

### 6.6 Semester Courses

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/semester-courses` | Get semester courses | `StudentSemesterCourseController` |
| `POST` | `/api/student/semester-courses` | Add semester course | `StudentSemesterCourseController` |
| `DELETE` | `/api/student/semester-courses/{courseId}` | Remove semester course | `StudentSemesterCourseController` |

### 6.7 Student Tech Stacks

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/tech-stacks` | Get student's tech stacks | `StudentTechStackController` |
| `POST` | `/api/student/tech-stacks` | Add tech stack | `StudentTechStackController` |
| `DELETE` | `/api/student/tech-stacks/by-name/{name}` | Remove by name | `StudentTechStackController` |

### 6.8 AI

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/ai/repo/{repoId}/summary` | AI repo summary | `PublicAiController` |
| `GET` | `/api/ai/repo/{repoId}/advice` | AI repo advice | `PublicAiController` |
| `POST` | `/api/ai/knowledge-graph/query` | Query knowledge graph | `PublicAiController` |
| `POST` | `/api/ai/subject-qa/query` | Ask AI Tutor (sync) | `SubjectQaController` |
| `POST` | `/api/ai/subject-qa/stream` | Ask AI Tutor (SSE streaming) | `SubjectQaController` |
| `POST` | `/api/ai/generate-roadmap` | Generate study roadmap | `PublicAiController` |

### 6.9 Social (Reviews & Votes)

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `POST` | `/api/student/repos/{repoId}/review` | Submit repo review | `StudentSocialController` |
| `DELETE` | `/api/student/repos/{repoId}/review` | Delete repo review | `StudentSocialController` |
| `POST` | `/api/student/repos/{repoId}/vote` | Vote on repo | `StudentSocialController` |

### 6.10 Notifications

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/notifications` | List notifications | `StudentNotificationController` |
| `GET` | `/api/student/notifications/unread-count` | Unread count | `StudentNotificationController` |
| `PUT` | `/api/student/notifications/{id}/read` | Mark as read | `StudentNotificationController` |
| `PUT` | `/api/student/notifications/read-all` | Mark all as read | `StudentNotificationController` |

### 6.11 Group Plans

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `POST` | `/api/student/group-plans` | Create group plan | `StudentGroupPlanController` |
| `GET` | `/api/student/group-plans` | List my group plans | `StudentGroupPlanController` |
| `GET` | `/api/student/group-plans/{id}` | Get plan detail | `StudentGroupPlanController` |
| `DELETE` | `/api/student/group-plans/{id}` | Delete plan | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/request-delete` | Request plan deletion | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/approve-delete` | Approve plan deletion | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/invite` | Invite member | `StudentGroupPlanController` |
| `GET` | `/api/student/group-plans/{id}/members` | List members | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/respond` | Respond to invite | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/leave` | Leave plan | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/transfer` | Transfer ownership | `StudentGroupPlanController` |
| `DELETE` | `/api/student/group-plans/{planId}/members/{memberId}` | Remove member | `StudentGroupPlanController` |
| `GET` | `/api/student/group-plans/{id}/tasks` | List group tasks | `StudentGroupPlanController` |
| `GET` | `/api/student/group-plans/assigned-tasks` | My assigned tasks | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/{id}/tasks` | Add group task | `StudentGroupPlanController` |
| `PUT` | `/api/student/group-plans/tasks/{taskId}` | Update group task | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/tasks/{taskId}/request-delete` | Request task deletion | `StudentGroupPlanController` |
| `POST` | `/api/student/group-plans/tasks/{taskId}/approve-delete` | Approve task deletion | `StudentGroupPlanController` |

### 6.12 Personal Tasks

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/tasks?filter=` | List personal tasks | `StudentTaskController` |
| `POST` | `/api/student/tasks` | Create task | `StudentTaskController` |
| `PUT` | `/api/student/tasks/{taskId}` | Update task | `StudentTaskController` |
| `DELETE` | `/api/student/tasks/{taskId}` | Delete task | `StudentTaskController` |
| `PATCH` | `/api/student/tasks/{taskId}/toggle` | Toggle completion | `StudentTaskController` |

### 6.13 Community

| Method | Endpoint | Description | Backend Controller |
|---|---|---|---|
| `GET` | `/api/student/community` | List channels | `StudentCommunityController` |
| `GET` | `/api/student/community/channels/{channelId}/messages?page=&size=` | Get messages (paginated) | `StudentCommunityController` |
| `POST` | `/api/student/community/channels/{channelId}/upload-image` | Upload image (multipart) | `StudentCommunityController` |
| `WS` | `/ws/community` | SockJS endpoint (registered in `WebSocketConfig`; Android client connects via `/ws/community/websocket` transport) | *(config class, not a controller)* |

---

## 7. Authentication & Token Management

Both apps use identical JWT authentication patterns:

### 7.1 Auth Flow

1. **Login/Register**: Server returns `{ token, refreshToken, ... }`
2. **Storage**: Tokens saved to DataStore Preferences
3. **Injection**: `AuthInterceptor` adds `Authorization: Bearer {token}` header to every request
4. **Refresh**: On 401 response, interceptor attempts one token refresh via `POST /api/auth/refresh`
5. **Retry**: If refresh succeeds, original request is retried with new token

### 7.2 Admin vs Mobile Differences

| Aspect | Admin | Mobile |
|---|---|---|
| **Interceptor** | Inline refresh in `AuthInterceptor.intercept()` | Delegates to `TokenRefreshManager` (coroutine-based with mutex) |
| **Token init** | `AdminRepositoryImpl.initToken()` hydrates from DataStore on first access | `AuthInterceptor` calls `TokenRefreshManager.init()` via `runBlocking` |
| **Logout** | Calls `/api/admin/auth/logout` + clears DataStore | Calls `/api/student/logout` + clears DataStore + clears TokenRefreshManager |
| **Username persistence** | Saves admin username in DataStore | Saves student name + code in DataStore |

---

## 8. Backend Controller Cross-Reference

The `devorbit-api/` Spring Boot backend exposes 36 controllers. Here's the complete mapping:

### Admin-Only Controllers (17)
| Controller | Prefix | Used By |
|---|---|---|
| `AdminAuthController` | `/api/admin/auth` | Admin app |
| `AdminStatsController` | `/api/admin/stats` | Admin app |
| `AdminStudentController` | `/api/admin/students` | Admin app |
| `AdminCourseController` | `/api/admin/courses` | Admin app |
| `AdminCourseResourceController` | `/api/admin/courses/{id}/resources` | Admin app |
| `AdminCourseRelationshipController` | `/api/admin/courses/relationships` | Admin app |
| `AdminRepoController` | `/api/admin/repos` | Admin app |
| `AdminRepoCandidateController` | `/api/admin/repo-candidates` | Admin app |
| `AdminRepoReviewController` | `/api/admin/reviews/repos` | Admin app |
| `AdminCourseReviewController` | `/api/admin/reviews/courses` | Admin app |
| `AdminGithubController` | `/api/admin/github` | Admin app |
| `AdminCommunityController` | `/api/admin/community` | Admin app |
| `AdminChatController` | `/api/admin/chat` | Admin app |
| `AdminTechStackController` | `/api/admin/techstack` | Admin app |
| `AdminNoteController` | `/api/admin/notes` | Admin app |
| `AdminNotificationController` | `/api/admin/notifications` | Admin app |
| `AdminKnowledgeController` | `/api/admin/knowledge` | Backend-only (RAG/knowledge ingest — no Android client calls this) |

### Student-Only Controllers (10)
| Controller | Prefix | Used By |
|---|---|---|
| `StudentAuthController` | `/api/student` | Mobile app |
| `StudentBookmarkController` | `/api/student/bookmarks` | Mobile app |
| `StudentCommunityController` | `/api/student/community` | Mobile app |
| `StudentGroupPlanController` | `/api/student/group-plans` | Mobile app |
| `StudentNotificationController` | `/api/student/notifications` | Mobile app |
| `StudentSemesterCourseController` | `/api/student/semester-courses` | Mobile app |
| `StudentSocialController` | `/api/student/repos/{id}/review`, `/api/student/repos/{id}/vote` | Mobile app |
| `StudentTaskController` | `/api/student/tasks` | Mobile app |
| `StudentTechStackController` | `/api/student/tech-stacks` | Mobile app |
| `SubjectQaController` | `/api/ai/subject-qa` | Mobile app |

### Shared/Public Controllers (9)
| Controller | Prefix | Used By |
|---|---|---|
| `AuthController` | `/api/auth/refresh` | Both apps |
| `PublicCourseController` | `/api/courses` | Both apps |
| `PublicCourseRelationshipController` | `/api/courses/relationships` | Both apps |
| `PublicRepoController` | `/api/repos`, `/api/courses/{id}/repos` | Both apps |
| `PublicSocialController` | `/api/repos/{id}/social-info`, `/api/courses/{id}/reviews` | Both apps |
| `PublicDiscoveryController` | `/api/discovery` | Mobile app |
| `PublicTechStackController` | `/api/tech-stacks` | Mobile app |
| `PublicAiController` | `/api/ai` | Mobile app |
| `PhotoboothFrameController` | `/api/photobooth/frames` | Neither (unused in Android) |

### Backend Services (not exposed directly)
| Service | Role |
|---|---|
| `GithubRepoService` | GitHub API integration, repo metadata fetching |
| `GithubAutoApprovalService` | Automated repo candidate approval |
| `WeeklyGithubScanScheduler` | Scheduled weekly GitHub scans |
| `RepoCandidateService` | Repo candidate lifecycle management |
| `SubjectQaService` | AI Tutor RAG pipeline (84KB — largest service) |
| `CommunityChatService` | WebSocket community chat |
| `StudentAuthService` | Student auth logic (JWT, OTP, registration) |
| `AdminAuthService` | Admin auth logic |
| `JwtService` | JWT token generation/validation |
| `StudentSemesterCourseService` | Semester course selection |
| `StudentTechStackService` | Student tech stack management |
| `CourseArticleService` | Course article management |

---

## 9. Build & Development

### 9.1 Build Commands

Both apps require Java 17 (`JAVA_HOME`) and Android SDK 35 (`ANDROID_HOME`). Both use Gradle 8.13 via the wrapper.

```bash
# Admin app (run from devorbit-admin/)
cd devorbit-admin
./gradlew compileDebugKotlin          # Kotlin compilation only
./gradlew testDebugUnitTest           # unit tests
./gradlew assembleDebug               # full debug APK
./gradlew lintDebug                   # lint checks
./gradlew assembleRelease             # release build (ProGuard/R8 enabled)

# Mobile app (run from devorbit-mobile/)
cd devorbit-mobile
./gradlew compileDebugKotlin
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew assembleRelease
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### 9.2 Environment Configuration

Both apps read `.env` files via `loadDotEnv()` in `build.gradle.kts`:

```env
# devorbit-admin/.env
ADMIN_API_BASE_URL=http://10.0.2.2:8080

# devorbit-mobile/.env
MOBILE_API_BASE_URL=http://10.0.2.2:8080
```

The `API_BASE_URL` is injected into `BuildConfig` at compile time.

### 9.3 Testing

| Type | Admin | Mobile |
|---|---|---|
| Unit tests | `testDebugUnitTest` | `testDebugUnitTest` |
| Frameworks | JUnit 4.13.2, Coroutines Test 1.9.0, Mockito Kotlin 5.4.0, Mockito Inline 5.2.0 | Same + Gson 2.11.0 (for STOMP parsing tests) |
| Test files | Under `app/src/test/java/` | Under `app/src/test/java/` |

### 9.4 Release

Admin app has ProGuard/R8 enabled for release builds (`isMinifyEnabled = true`, `isShrinkResources = true`). Mobile app does not enable minification in its build types.
