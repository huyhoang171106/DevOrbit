<!-- Parent: ../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# pages

## Purpose
Page-level React components representing routes in the DevOrbit web application. Organized by user role with admin and student interfaces for different functionality. All pages are lazy-loaded in `src/router.tsx` with a Suspense fallback.

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `admin/` | Admin dashboard and management pages (courses, repos, scanning, roadmaps, relationships, notes, photobooth frames) |
| `student/` | Student-facing pages (courses, repos, photobooth, bookmarks, login) |

## Key Files
| File | Description |
|------|-------------|
| `admin/LoginPage.tsx` | Admin authentication page |
| `admin/AdminDashboardPage.tsx` | Main admin dashboard |
| `admin/AdminCoursesPage.tsx` | Course management interface |
| `admin/AdminCourseResourcesPage.tsx` | Course resources management |
| `admin/AdminScanPage.tsx` | GitHub repository scanning |
| `admin/AdminCandidatesPage.tsx` | Repository candidate approval |
| `admin/AdminReposPage.tsx` | Repository management interface |
| `admin/AdminRoadmapsPage.tsx` | Roadmap management interface |
| `admin/AdminRelationshipsPage.tsx` | Relationship management interface |
| `admin/AdminNotesPage.tsx` | Notes management interface |
| `admin/AdminPhotoboothFramesPage.tsx` | Photobooth frame management |
| `student/HomePage.tsx` | Student home/dashboard |
| `student/CourseListPage.tsx` | Browse available courses |
| `student/CourseDetailPage.tsx` | Individual course information |
| `student/RepoDetailPage.tsx` | Repository detail view |
| `student/StudentLoginPage.tsx` | Student authentication page |
| `student/StudentBookmarksPage.tsx` | Student bookmarks page |
| `student/PhotoboothPage.tsx` | Photobooth experience |
| `student/PhotographPage.tsx` | Photograph page (photobooth entry) |

## For AI Agents

### Working In This Directory
- Pages correspond to routes in `src/router.tsx`
- All pages are lazy-loaded with React.lazy + Suspense
- Implement role-based access control (admin vs student tokens)
- Handle data fetching via TanStack React Query
- Use Zustand for local page state where needed
- Navigate with React Router 7 hooks

### Testing Requirements
- End-to-end page tests (Vitest + jsdom)
- Route protection testing (unauthenticated redirects)
- Data loading and error states
- Error boundary coverage

### Common Patterns
- Page components as route handlers
- TanStack React Query for server data fetching
- Zustand stores for client-side page state
- Loading and error states with Suspense fallback
- Role-based rendering (admin vs student)
- Navigation via React Router 7 `useNavigate` / `Link`

## Dependencies

### Internal
- `../components/` - UI components (admin, student, photobooth)
- `../components/Layout.tsx` - Page layout wrapper
- `../lib/api.ts` - Data fetching
- `../lib/auth.ts` - Authentication state (localStorage tokens)
- `../hooks/` - Custom React hooks
- `../router.tsx` - Route definitions

### External
- React 19 - UI framework
- React Router DOM 7 - Navigation
- TanStack React Query 5 - Server state
- Zustand 5 - Client state
- Framer Motion 12 - Page transitions/animations
- react-force-graph-2d - Knowledge graph visualization (D3 force)

<!-- MANUAL: -->