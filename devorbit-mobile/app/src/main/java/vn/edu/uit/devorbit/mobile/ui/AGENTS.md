<!-- Parent: ../../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# ui

## Purpose
Jetpack Compose UI screens and composables for the DevOrbit Android application. Contains the user interface components for course browsing, repository exploration, and authentication flows.

## Key Files
| File | Description |
|------|-------------|
| `DevOrbitApp.kt` | Main application composable with navigation |
| `MainActivity.kt` | Android activity entry point |
| `LoginScreen.kt` | User authentication interface |
| `CourseListScreen.kt` | Course browsing screen |
| `CourseDetailScreen.kt` | Individual course details |
| `RepoListSection.kt` | Repository browsing component |
| `RepoDetailScreen.kt` | Repository details and information |
| `RepoFilterSheet.kt` | Repository filtering options |

## For AI Agents

### Working In This Directory
- Use Jetpack Compose for UI development
- Follow Material Design guidelines
- Implement responsive layouts
- Handle navigation between screens
- Use Kotlin coroutines for async operations

### Testing Requirements
- UI component tests
- Navigation flow testing
- Screen rotation handling
- Dark/light theme testing

### Common Patterns
- @Composable functions
- State hoisting with ViewModels
- remember/saveable for state persistence
- LaunchedEffect for side effects
- Navigation component for routing

## Dependencies

### Internal
- `../model/` - Data classes
- `../repository/` - Data access
- `../network/` - API client
- MainActivity.kt - Activity hosting

### External
- Jetpack Compose - UI framework
- Material Components - Design system
- Navigation Compose - Screen navigation
- Kotlin Coroutines - Async programming

<!-- MANUAL: -->