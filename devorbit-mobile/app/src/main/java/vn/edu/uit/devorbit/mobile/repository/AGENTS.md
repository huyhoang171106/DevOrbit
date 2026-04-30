<!-- Parent: ../../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# repository

## Purpose
Data access layer for the DevOrbit Android application implementing repository pattern with offline caching. Provides clean API for data operations with network and local storage abstraction.

## Key Files
| File | Description |
|------|-------------|
| `DevOrbitRepository.kt` | Main repository interface implementation |
| `AuthSessionStore.kt` | Authentication session management |
| `CacheStore.kt` | Local data caching utilities |
| `NetworkModule.kt` | Dependency injection for network components |

## For AI Agents

### Working In This Directory
- Implement repository pattern
- Handle offline-first data access
- Use dependency injection
- Provide clean API to ViewModels
- Implement caching strategies

### Testing Requirements
- Repository unit tests
- Offline functionality testing
- Cache invalidation testing
- Network error handling

### Common Patterns
- Interface + implementation pattern
- suspend functions for async operations
- Flow for reactive data streams
- Hilt/Dagger for dependency injection
- Room/SharedPreferences for local storage

## Dependencies

### Internal
- `../model/` - Data classes
- `../network/` - API service interfaces
- `../ui/` - ViewModels and UI layer

### External
- Kotlin Coroutines - Async programming
- Room - Local database
- Retrofit - Network client
- Hilt - Dependency injection

<!-- MANUAL: -->