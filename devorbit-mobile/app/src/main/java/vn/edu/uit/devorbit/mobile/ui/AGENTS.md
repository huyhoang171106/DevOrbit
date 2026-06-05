<!-- Parent: ../../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# ui

## Purpose
Jetpack Compose UI layer for the DevOrbit Android application. Contains all user interface screens, components, navigation, theming, and ViewModels for the "Academic OS" experience.

## Key Files
| File | Description |
|------|-------------|
| `DevOrbitApp.kt` | Root composable with NavHost, routing, auth state handling |
| `MainScreen.kt` | Main scaffold with bottom navigation bar and tab switching |
| `MainActivity.kt` | Android activity entry point |
| `screen/auth/AuthScreen.kt` | Combined authentication flow (login/register) |
| `screen/courses/CourseHubScreen.kt` | Course hub with semester filter, stats, and list/graph toggle |
| `screen/courses/CourseListScreen.kt` | High-density course list with search and semester filter |
| `screen/courses/CourseHubNavigationState.kt` | Course hub detail navigation state holder |
| `screen/dashboard/DashboardScreen.kt` | Dashboard (Mission Control) with widget layout |
| `screen/dashboard/FocusModeScreen.kt` | Focus mode timer/study session screen |
| `screen/explore/ExploreScreen.kt` | Explore/discover screen for repositories |
| `screen/knowledge/KnowledgeHubScreen.kt` | Knowledge tab hub with graph/detail navigation |
| `screen/knowledge/KnowledgeGraphScreen.kt` | Galaxy graph canvas with search, filter, semester chips |
| `screen/knowledge/KnowledgeDetailScreen.kt` | Node detail with learning path, simulation, resource linking |
| `screen/plan/StudyPlannerScreen.kt` | Study plan creation and management |
| `screen/plan/SyllabusParserScreen.kt` | Syllabus parsing and extraction |
| `screen/plan/TaskBreakdownScreen.kt` | Task breakdown from assignments |
| `screen/profile/ProfileScreen.kt` | User profile and settings |
| `CourseDetailScreen.kt` | Individual course detail with prerequisites and related courses |
| `RepoDetailScreen.kt` | GitHub repository detail and information |
| `RepoListSection.kt` | Repository browsing component |
| `RepoFilterSheet.kt` | Repository filtering bottom sheet |
| `RepoFilterState.kt` | Repository tech stack filter state |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `components/` | Reusable composables (CosmicBackground.kt, GalaxyGraphCanvas.kt, GlassCard.kt) |
| `navigation/` | Route definitions (Screen.kt) |
| `theme/` | Cosmic/galaxy design system (Color.kt, DesignSystem.kt, Theme.kt) |
| `viewmodel/` | ViewModels (AcademicViewModel, AuthViewModel, CourseViewModel, ExploreViewModel, KnowledgeViewModel, ProfileViewModel) |

## For AI Agents

### Working In This Directory
- Use Jetpack Compose with Material 3 for UI development
- Follow the cosmic/galaxy design system defined in `theme/`
- Use `Screen.kt` for route constants, `DevOrbitApp.kt` for NavHost routing
- ViewModels communicate via StateFlow; screens observe with `collectAsStateWithLifecycle()`
- Navigation uses `NavController` from Navigation Compose
- Hilt-injected ViewModels via `hiltViewModel()`

### Testing Requirements
- UI state tests in `app/src/test/` (RepoFilterStateTest, CourseHubNavigationStateTest)
- Compose UI tests (pending)
- Screen rotation handling
- Dark/light theme consistency (cosmic theme is dark-mode-first)

### Common Patterns
- `@Composable` functions with state hoisting
- `ViewModel` + `StateFlow` for reactive UI state
- `remember`/`saveable` for local state persistence
- `LaunchedEffect` for side effects (data loading)
- Navigation Compose for screen routing
- Bottom navigation bar for tab switching (MainScreen.kt)
- Search-first UX in course hub and knowledge graph screens

## Dependencies

### Internal
- `../model/` - Data classes
- `../data/repository/` - Data access (AcademicRepository, etc.)
- `../network/` - API client (ApiService)
- `../domain/engine/` - Business logic engines (KnowledgeGraphEngine, etc.)
- `../data/local/` - Room database access
- `MainActivity.kt` - Activity hosting

### External
- Jetpack Compose (BOM 2024.11.00) - UI framework
- Material 3 - Design system
- Navigation Compose 2.8.4 - Screen navigation
- Hilt + hilt-navigation-compose - DI + ViewModel injection
- Room - Local data via DAOs
- Kotlin Coroutines + StateFlow - Async + reactive state
- Gson - JSON parsing for API responses

<!-- MANUAL: Add new screens, components, ViewModels, and theme files to the tables above when they are created. -->
