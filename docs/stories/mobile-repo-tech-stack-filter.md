# US-018 Mobile Repo Tech Stack Filter

## Status

implemented

## Lane

normal

## Product Contract

Mobile course detail pages let students narrow the repository list by tech stack, using the `techStacks` values already returned on each repo summary. Selecting the same stack again clears the filter.

## Relevant Product Docs

- `devorbit-mobile/.gsd/SPEC.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- The Repos tab shows available unique tech stack filters for the course.
- Selecting a stack shows only repositories containing that stack.
- Tapping the selected stack again returns to all repositories.
- The Repos tab shows filtered and total result counts.
- An empty filtered result shows a clear empty state.

## Design Notes

- UI surface: `CourseDetailScreen` Repos tab and `RepoListSection`.
- Domain rule: stack names are unique, non-blank, sorted strings derived from `RepoSummary.techStacks`.
- Public API shape does not change.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `RepoFilterStateTest` covers unique stack extraction, filtering, and toggle-clear behavior. |
| Integration | Not required; no API contract change. |
| E2E | Not available in mobile harness. |
| Platform | `:app:testDebugUnitTest` and Android compile tasks through Gradle. |
| Release | Not run for this story. |

## Harness Delta

GitNexus MCP tools were not available in this session, so impact analysis was approximated with repository call-site search before edits.

## Evidence

- `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` passed on 2026-05-15.
