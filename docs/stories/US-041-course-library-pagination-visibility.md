# US-041 Course Library Pagination Visibility

## Status

implemented

## Lane

normal

## Product Contract

The course library must visibly render the course cards belonging to every
pagination page. Changing from page 1 to a later page must not leave the new
cards hidden by the one-time reveal animation.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- Page 1 continues to show the first 30 courses.
- Selecting page 2 or using the next-page control visibly renders its courses.
- Course card links continue to use each course's own `courseId`.
- Search changes reset pagination to page 1.

## Design Notes

- Keep the fix local to `CourseListPage`; the shared `StaggerReveal` primitive
  has high impact across multiple student pages.
- Remount the reveal container when the page or active search changes so newly
  paginated children receive the visible animation state.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `devorbit-web`: `npm test -- --run src/pages/student/CourseListPage.test.tsx` |
| Integration | Not required; API contract is unchanged. |
| E2E | Covered by the page-level jsdom regression test. |
| Platform | `devorbit-web`: `npx tsc --noEmit`; `npm run build` |
| Release | Full frontend test suite. |

## Harness Delta

No harness rule changes.

## Evidence

- `devorbit-web`: targeted `CourseListPage.test.tsx` passed 1/1.
- `devorbit-web`: full Vitest suite passed 143/143.
- `devorbit-web`: `npx tsc --noEmit` passed.
- `devorbit-web`: `npm run build` passed.
- GitNexus index synchronized after the code change; `CourseListPage` remains
  LOW risk with no upstream impacted symbols.
