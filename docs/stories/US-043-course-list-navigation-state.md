# US-043 Course List Navigation State

## Status

implemented

## Lane

normal

## Product Contract

The course-library pagination page must survive navigation into a course and
browser Back navigation.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- Selecting page 2 writes `?page=2` to the course-library URL.
- Opening a course and navigating Back restores page 2.
- Selecting the `Danh mục` link in course detail restores the originating page.
- Invalid or out-of-range page values are corrected safely.
- Starting a new search resets pagination to page 1.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `CourseListPage.test.tsx` |
| Integration | Not required. |
| E2E | Memory-router navigation regression test. |
| Platform | TypeScript and Vite production build. |
| Release | Full frontend test suite. |

## Harness Delta

No harness rule changes.

## Evidence

- `devorbit-web`: targeted search and navigation tests passed 23/23.
- `devorbit-web`: full Vitest suite passed 162/162 with `--maxWorkers=1`.
- `devorbit-web`: `npx tsc --noEmit` passed.
- `devorbit-web`: `npm run build` passed.
