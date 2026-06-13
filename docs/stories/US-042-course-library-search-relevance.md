# US-042 Course Library Search Relevance

## Status

implemented

## Lane

normal

## Product Contract

Course-library search must find courses and repositories from Vietnamese
queries with or without accents. Results update while the user types, exact
phrase matches rank before related matches, and pressing Enter or the search
button immediately applies the complete query. When the normalized query
matches a complete course title or course code, search narrows to exact course
matches instead of continuing to show loosely related courses or repositories.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- `nhập môn` and `nhap mon` both match courses containing "Nhập môn".
- Partial input such as `nhập` and `nhập m` shows relevant results.
- Exact phrase matches rank before looser related matches.
- A complete course title or code locks results to exact course matches only.
- Shorter partial phrases remain broad to support discovery while typing.
- Search compares normalized course, repository, language, technology, README,
  and file-tree text consistently.
- Enter and the search button apply the current input immediately.
- All matching repositories are shown rather than truncated to a fixed limit.
- The empty state is not shown while repository data is still loading.

## Design Notes

- Keep search client-side over the existing course and discovery-repository
  payloads; no API contract changes are required.
- Normalize both query terms and searchable fields before scoring.
- Preserve debounced live search while adding explicit submit behavior.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `repoSearch.test.ts`, `CourseListPage.test.tsx` |
| Integration | Not required; API contract is unchanged. |
| E2E | Page-level jsdom search interaction. |
| Platform | TypeScript and Vite production build. |
| Release | Full frontend test suite. |

## Harness Delta

No harness rule changes.

## Evidence

- `devorbit-web`: targeted search tests passed 13/13.
- `devorbit-web`: full Vitest suite passed 153/153.
- `devorbit-web`: `npx tsc --noEmit` passed.
- `devorbit-web`: `npm run build` passed.
- GitNexus index synchronized after the code change; detected scope is limited
  to course-library search and its existing page flows.
