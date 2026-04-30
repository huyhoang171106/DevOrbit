<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# devorbit-mobile

## Purpose
Kotlin Android application for the DevOrbit platform, providing mobile access for students to browse courses, explore GitHub repositories, and access learning resources. Features offline caching, intuitive navigation, and seamless integration with the DevOrbit API.

## Key Files
| File | Description |
|------|-------------|
| `build.gradle.kts` | Root Gradle build configuration |
| `app/build.gradle.kts` | App module Gradle configuration |
| `settings.gradle.kts` | Gradle settings |
| `gradle.properties` | Gradle properties |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt` | Main Android activity |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/ui/DevOrbitApp.kt` | Main application composable |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `app/src/main/java/vn/edu/uit/devorbit/mobile/model/` | Data models (Course, Repo, Auth, TechStack) |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/network/` | API client and network configuration |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/repository/` | Data repositories with caching |
| `app/src/main/java/vn/edu/uit/devorbit/mobile/ui/` | UI screens and composables |
| `app/src/main/res/` | Android resources (layouts, drawables, values) |
| `gradle/` | Gradle wrapper files |

## For AI Agents

### Working In This Directory
- Use Android Studio or Gradle for building
- Follow Kotlin Android development best practices
- Repository pattern for data management
- Offline-first approach with caching
- Compose UI framework for modern Android development

### Testing Requirements
- Test on multiple Android versions
- Verify offline functionality
- Test API integration and error handling
- Check authentication persistence

### Common Patterns
- Jetpack Compose for UI development
- Repository pattern for data access
- Kotlin coroutines for async operations
- SharedPreferences/DataStore for local storage
- Retrofit/OkHttp for network calls

## Dependencies

### Internal
- `../devorbit-api/` - Backend API endpoints
- `app/src/main/java/vn/edu/uit/devorbit/mobile/network/ApiService.kt` - API integration

### External
- Kotlin - Programming language
- Jetpack Compose - UI framework
- Retrofit - HTTP client
- Room - Local database (likely)
- Hilt/Dagger - Dependency injection
- Kotlin Coroutines - Async programming

<!-- MANUAL: -->