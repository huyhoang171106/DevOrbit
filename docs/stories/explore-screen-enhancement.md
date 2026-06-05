# US-035 Mobile Course Search, Bookmarks, and Explore Enhancement

## Status

implemented

## Lane

normal

## Product Contract

Mobile Courses supports server-backed search/filtering and course bookmarks. Mobile Explore supports repository search, tech stack filtering, typed tech stack parsing, empty/loading/error states, and navigation into the existing repo detail screen.

## Relevant Product Docs

- `mobile-plan-4screens.md`
- `docs/ARCHITECTURE.md`
- `devorbit-mobile/AGENTS.md`

## Acceptance Criteria

- [x] Course list search calls `/api/courses?q=...`.
- [x] Course subject chips call `/api/courses?subjectType=...`.
- [x] Course detail keeps repo tech stack filtering available.
- [x] Course detail has a bookmark action backed by `/api/student/bookmarks`.
- [x] Bookmark repository uses Student Bookmark API instead of in-memory mock data.
- [x] Explore search calls `/api/discovery/repos?q=...` and resets to recent repos when cleared.
- [x] Explore tech stack chips filter visible repos by parsed `techStacks`.
- [x] Explore repo cards display course, language, stars, and tech stacks.
- [x] Explore repo click opens the existing `RepoDetailScreen`.
- [x] Explore shows loading, error retry, and empty states.
- [ ] AI Subject Q&A mobile chat UI is not part of this slice.
- [ ] Course prerequisite/objective/grading display is not part of this slice.

## Design Notes

- Reused existing Compose screens and `RepoDetailScreen` instead of introducing a new Explore-specific detail screen.
- Added typed DTOs for discovery repos, bookmarks, Subject Q&A, and roadmap endpoint coverage.
- Kept repo filtering client-side after the server search result is loaded.
- Used API-backed bookmarks; no local bookmark persistence was added.

## Modified Files

| File | Change |
| --- | --- |
| `network/ApiService.kt` | Added query params and missing endpoint declarations. |
| `data/repository/DiscoveryRepositoryImpl.kt` | Typed repo parsing and repo search. |
| `data/repository/BookmarkRepositoryImpl.kt` | Switched from mock list to Student Bookmark API. |
| `ui/viewmodel/CourseViewModel.kt` | Course search/filter and course bookmark toggle state. |
| `ui/screen/courses/CourseListScreen.kt` | Search bar and subject filter chips. |
| `ui/CourseDetailScreen.kt` | Course bookmark action. |
| `ui/viewmodel/ExploreViewModel.kt` | Search, filter, error/loading, repo detail state. |
| `ui/screen/explore/ExploreScreen.kt` | Search, stack filters, states, repo cards, detail reuse. |

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `CourseSearchFilterStateTest`, `ExploreFilterStateTest`, `DiscoveryMappersTest`, existing mobile tests. |
| Integration | Not run; API integration depends on live backend/JWT. |
| E2E | Not available in mobile harness. |
| Platform | `:app:testDebugUnitTest` through Gradle. |

## Evidence

- `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` passed.
- Commits: `7e01b5d`, `6122db9`, `bbcc294`, `be174b4`.

## Harness Delta

CodeGraph and wiki tools were not exposed in this session. Impact analysis was approximated with local controller/repository/ViewModel inspection and Gradle compile/test proof.
