<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# devorbit-web

## Purpose
React web frontend for the DevOrbit platform, providing user interfaces for both administrators and students. Built with React 19, TypeScript, and Vite for fast development and deployment. Features role-based access control with separate admin and student interfaces for managing courses, repositories, and approvals.

## Key Files
| File | Description |
|------|-------------|
| `package.json` | NPM dependencies and scripts (React 19, Vite, TypeScript) |
| `vite.config.ts` | Vite build configuration |
| `tailwind.config.js` | Tailwind CSS configuration |
| `Dockerfile` | Containerization for the web application |
| `index.html` | Main HTML entry point |
| `src/main.tsx` | React application entry point |
| `src/App.tsx` | Main application component with routing |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `src/pages/admin/` | Admin dashboard pages (courses, repos, candidates, scanning) |
| `src/pages/student/` | Student-facing pages (courses, repositories, home) |
| `src/components/admin/` | Admin-specific UI components (tables, forms, modals) |
| `src/components/student/` | Student-specific UI components (cards, filters) |
| `src/lib/` | Utility functions (API client, auth, hooks) |
| `src/types/` | TypeScript type definitions |
| `dist/` | Built application assets |

## For AI Agents

### Working In This Directory
- Use `npm run dev` for development server
- Components are organized by user role (admin/student)
- API calls go through `src/lib/api.ts`
- Authentication handled via `src/lib/auth.ts`
- Follow TypeScript strict mode

### Testing Requirements
- Test all user roles (admin vs student)
- Verify API integration with backend
- Test responsive design with Tailwind CSS
- Check authentication flows

### Common Patterns
- Role-based component organization
- React Router for navigation
- Tailwind CSS for styling
- TypeScript interfaces in `src/types/`
- Custom hooks in `src/lib/hooks.ts`

## Dependencies

### Internal
- `../devorbit-api/` - Backend API endpoints
- `src/lib/api.ts` - API client integration

### External
- React 19 - UI framework
- TypeScript - Type safety
- Vite - Build tool
- Tailwind CSS - Styling
- React Router DOM - Navigation
- Native fetch API - HTTP client (src/lib/api.ts)

<!-- MANUAL: -->