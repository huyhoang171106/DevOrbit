<!-- Parent: ../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# pages

## Purpose
Page-level React components representing routes in the DevOrbit web application. Organized by user role with admin and student interfaces for different functionality.

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `admin/` | Admin dashboard and management pages |
| `student/` | Student browsing and interaction pages |

## Key Files
| File | Description |
|------|-------------|
| `admin/LoginPage.tsx` | Admin authentication page |
| `admin/AdminDashboardPage.tsx` | Main admin dashboard |
| `admin/AdminCoursesPage.tsx` | Course management interface |
| `admin/AdminReposPage.tsx` | Repository management interface |
| `admin/AdminCandidatesPage.tsx` | Repository candidate approval |
| `admin/AdminScanPage.tsx` | GitHub repository scanning |
| `student/HomePage.tsx` | Student home/dashboard |
| `student/CourseListPage.tsx` | Browse available courses |
| `student/CourseDetailPage.tsx` | Individual course information |
| `student/RepoListSection.tsx` | Repository browsing interface |

## For AI Agents

### Working In This Directory
- Pages correspond to routes in the application
- Implement role-based access control
- Handle data fetching and state management
- Use React Router for navigation
- Follow page-level component patterns

### Testing Requirements
- End-to-end page tests
- Route protection testing
- Data loading states
- Error boundary testing

### Common Patterns
- Page components as route handlers
- useEffect for data fetching on mount
- Loading and error states
- Role-based rendering
- Navigation between pages

## Dependencies

### Internal
- `../components/` - UI components
- `../lib/api.ts` - Data fetching
- `../lib/auth.ts` - Authentication state
- `../App.tsx` - Routing configuration

### External
- React Router DOM - Navigation
- React - UI framework
- API integration - Data fetching

<!-- MANUAL: -->