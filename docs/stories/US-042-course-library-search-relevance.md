# US-042 Course Library Search Relevance

## Status

implemented

## Lane

normal

## Product Contract

Course-library search must find courses and repositories from Vietnamese
queries with or without accents. Results update while the user types, exact
phrase matches rank before related matches, and pressing Enter or the search
button immediately applies the complete query. A complete title or code match
ranks first without hiding other courses that directly contain the same phrase.

## Relevant Product Docs

- `README.md`
- `docs/ARCHITECTURE.md`

## Acceptance Criteria

- `nhập môn` and `nhap mon` both match courses containing "Nhập môn".
- Partial input such as `nhập` and `nhập m` shows relevant results.
- A direct phrase match narrows results to names or codes containing that phrase.
- A complete course title or code ranks first while longer names containing the
  same direct phrase remain visible.
- Shorter partial phrases remain responsive while the user is typing.
- If no complete phrase matches, each independently matching keyword can return
  courses whose name or code directly contains that word.
- Complete words use exact word matching; prefix matching is used only when no
  complete keyword matches yet, preserving useful results while typing.
- Keyword matching respects word boundaries, so `anh` does not match inside
  `hành`, `văn` does not match `vận`, and `java` does not match `javascript`.
- Repository results follow the same direct phrase and word-boundary rules over
  repo name, course name/code, language, or technology stack.
- Descriptions, README content, file trees, aliases, and semantic expansions do
  not create additional matches.
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

- `devorbit-web`: targeted search and navigation tests passed 23/23.
- `devorbit-web`: full Vitest suite passed 162/162 with `--maxWorkers=1`;
  parallel execution can exhaust the existing 5-second router-test timeout.
- `devorbit-web`: `npx tsc --noEmit` passed.
- `devorbit-web`: `npm run build` passed.
- GitNexus index synchronization is included in final validation.
