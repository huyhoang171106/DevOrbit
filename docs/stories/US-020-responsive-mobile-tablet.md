# US-020 Web Responsive Mobile and Tablet Support

## Status

completed

## Lane

normal

## Product Contract

Make the `devorbit-web` client application fully responsive and interactive on mobile devices and tablet viewports. Ensure seamless rendering and complete feature access without layout breakages or horizontal overflows. Provide touch gesture support for photo adjustments and the interactive curriculum planner.

## Relevant Product Docs

- `devorbit-web/README.md`
- `design-system/devorbit/MASTER.md`

## Acceptance Criteria

- **Home Page**: Section padding scales down on mobile, Hero carousel shrinks gracefully without clipping.
- **Course List Page**: Search inputs, headers, and pagination buttons are fully responsive and wrap correctly without overflow.
- **Course Detail Page**: Sidebar (containing bookmark actions and CourseKnowledgeGraph) is ordered on top on mobile screens, and stats cards display cleanly in 2 columns.
- **Kanban Curriculum Board**: English level and graduation path selectors stack vertically. Guide text wraps, and total credits badges center. Fixed bottom bar buttons hide text labels on mobile to prevent overflow. DnD-kit uses `TouchSensor` with delay to allow drag-and-drop on mobile.
- **Prerequisite Warning Modal**: Modal dialog is capped to full-screen width with padding on small mobile screens.
- **Photobooth Photo Adjuster**: ImageAdjuster canvas handles touch events (`onTouchStart`, `onTouchMove`, `onTouchEnd`) so mobile/tablet users can drag and align photos with their fingers.
- **Student Bookmarks Page**: Page side margins shrink on mobile viewports.

## Design Notes

- **UI surfaces**:
  - `devorbit-web/src/pages/student/HomePage.tsx`
  - `devorbit-web/src/components/student/HeroStoryCarousel.tsx`
  - `devorbit-web/src/pages/student/CourseListPage.tsx`
  - `devorbit-web/src/pages/student/CourseDetailPage.tsx`
  - `devorbit-web/src/components/student/KanbanBoard.tsx`
  - `devorbit-web/src/pages/student/knowledge-graph/GalaxyPage.tsx`
  - `devorbit-web/src/components/photobooth/PhotoUploadSection.tsx`
  - `devorbit-web/src/pages/student/StudentBookmarksPage.tsx`

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | - |
| Integration | - |
| E2E | - |
| Platform | Web production build compiles successfully via `npm run build` |

## Harness Delta

None

## Evidence

- Verified that `devorbit-web` compiled successfully via `npm run build` with zero errors or warnings (build finished in 9.04 seconds).
- Documented full changes and verification plan in `walkthrough.md` under the app data artifacts directory.
