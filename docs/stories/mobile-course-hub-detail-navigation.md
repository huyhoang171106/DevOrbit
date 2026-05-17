# US-019 Mobile Course Hub Detail Navigation

## Status

implemented

## Lane

normal

## Product Contract

Mobile Course Hub list items open a course detail view. The detail view loads repositories, tutorials, videos, and articles through the existing API service and lets students open a repository detail view from the repo list. Back navigation returns from repo detail to course detail, then from course detail to the course list.

## Relevant Product Docs

- `devorbit-mobile/.gsd/SPEC.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- Tapping a course in Course Hub opens that course detail surface.
- Course detail loads repo and knowledge resources using existing mobile API methods.
- Tapping a repo opens `RepoDetailScreen`.
- Back from repo returns to the same course detail; back from course detail returns to Course Hub.
- If remote repo loading fails, cached repos are shown when available.

## Design Notes

- UI surface: `CourseHubScreen`, `CourseDetailScreen`, `RepoDetailScreen`.
- State owner: `CourseViewModel`.
- Repository: `AcademicRepository.loadCourseDetail(courseId)` combines repo/resource API calls and caches repos.
- API contract does not change.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `CourseHubNavigationStateTest` covers list/detail/repo back-stack behavior. |
| Integration | Not required; existing API client methods are reused. |
| E2E | Not available in mobile harness. |
| Platform | `:app:testDebugUnitTest` compiles and runs mobile unit tests. |
| Release | Not run for this story. |

## Harness Delta

GitNexus MCP tools were not available in this session, so impact analysis was approximated with repository call-site search before edits.

## Evidence

- `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` passed on 2026-05-15.
