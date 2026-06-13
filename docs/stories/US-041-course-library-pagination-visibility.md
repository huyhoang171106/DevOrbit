# US-041 Course Library Pagination Visibility

## Status

implemented

## Lane

normal

## Product Contract

The course library must visibly render the course and repository cards
belonging to the current pagination page or repository filter. Changing pages
or switching technology/language filters must not leave new cards hidden by the
one-time reveal animation.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- Page 1 continues to show the first 30 courses.
- Selecting page 2 or using the next-page control visibly renders its courses.
- Course card links continue to use each course's own `courseId`.
- Search changes reset pagination to page 1.
- Switching between repository technology/language filters visibly renders
  every matching repository immediately.

## Design Notes

- Keep the fix local to `CourseListPage`; the shared `StaggerReveal` primitive
  has high impact across multiple student pages.
- Remount the reveal container when the page or active search changes so newly
  paginated children receive the visible animation state.
- Remount the course-detail repository reveal container when its filtered set of
  repository IDs changes.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm test -- --run src/pages/student/CourseListPage.test.tsx src/pages/student/CourseDetailPage.test.tsx` |
| Integration | Not required; API contract is unchanged. |
| E2E | Covered by the page-level jsdom regression test. |
| Platform | `devorbit-web`: `npx tsc --noEmit`; `npm run build` |
| Release | Full frontend test suite. |

## Harness Delta

No harness rule changes.

## Evidence

- `devorbit-web`: targeted `CourseListPage.test.tsx` passed 1/1.
- `devorbit-web`: targeted course list and course detail tests passed 2/2.
- `devorbit-web`: full Vitest suite passed 144/144.
- `devorbit-web`: `npx tsc --noEmit` passed.
- `devorbit-web`: `npm run build` passed.
- GitNexus index synchronized after the code change; `CourseListPage` remains
  LOW risk with no upstream impacted symbols. `CourseDetailPage` and
  `handleFilter` are also LOW risk with no upstream impacted symbols.
