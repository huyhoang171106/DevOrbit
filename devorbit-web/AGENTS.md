<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# devorbit-web

## Purpose
React web frontend for the DevOrbit platform, providing user interfaces for both administrators and students. Built with React 19, TypeScript, and Vite for fast development and deployment. Features role-based access control with separate admin and student interfaces for managing courses, repositories, approvals, photobooth frames, knowledge graphs, roadmaps, and relationships.

## Key Files
| File | Description |
|------|-------------|
| `package.json` | NPM dependencies and scripts (React 19, Vite 6, TypeScript) |
| `vite.config.ts` | Vite build configuration with proxy to backend API |
| `tailwind.config.js` | Tailwind CSS 3.4 configuration |
| `postcss.config.js` | PostCSS configuration for Tailwind |
| `Dockerfile` | Containerization for the web application |
| `index.html` | Main HTML entry point |
| `src/main.tsx` | React application entry point |
| `src/router.tsx` | Route definitions with lazy loading for all pages |
| `src/App.tsx` | Main application component (QueryClientProvider, BrowserRouter, Layout) |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `src/pages/admin/` | Admin dashboard pages (courses, repos, candidates, scanning, roadmaps, relationships, notes, frames) |
| `src/pages/student/` | Student-facing pages (courses, repositories, photobooth, bookmarks) |
| `src/components/admin/` | Admin-specific UI components (tables, forms, modals, dialogs) |
| `src/components/student/` | Student-facing components (cards, KanbanBoard, knowledge graph, discovery feed) |
| `src/components/photobooth/` | Photobooth components (frame selection, photo upload, preview, download) |
| `src/lib/` | Utility functions (API client, auth, colors, theme, frame definitions, photo compositing) |
| `src/lib/frames/` | Photobooth frame definitions and service |
| `src/hooks/` | Custom React hooks (knowledge graph, course list, AI queries, mouse parallax) |
| `src/types/` | TypeScript type definitions (API, frames) |
| `dist/` | Built application assets |

## For AI Agents

### Working In This Directory
- Use `npm run dev` for development server
- Use `npm run build` for production build (tsc + vite)
- Components are organized by user role (admin/student)
- API calls go through `src/lib/api.ts`
- Authentication handled via `src/lib/auth.ts` (localStorage tokens)
- State management via Zustand 5 stores and TanStack React Query 5
- Follow TypeScript strict mode
- Routing via React Router 7 with lazy-loaded pages in `src/router.tsx`

### Testing Requirements
- Test all user roles (admin vs student)
- Verify API integration with backend
- Test responsive design with Tailwind CSS
- Check authentication flows
- Run `npm test` for Vitest suite

### Common Patterns
- Role-based component organization
- React Router 7 for navigation
- TanStack React Query 5 for server state
- Zustand 5 for client state
- Tailwind CSS 3.4 for styling
- TypeScript interfaces in `src/types/`
- Custom hooks in `src/hooks/`
- Lazy-loaded pages with Suspense fallback

## Dependencies

### Internal
- `../devorbit-api/` - Backend API endpoints
- `src/lib/api.ts` - API client integration

### External
- React 19 - UI framework
- TypeScript ~5.7 - Type safety
- Vite 6 - Build tool
- Tailwind CSS 3.4 - Styling
- React Router DOM 7 - Navigation
- TanStack React Query 5 - Server state management
- Zustand 5 - Client state management
- Framer Motion 12 - Animations
- @phosphor-icons/react - Icon library
- @dnd-kit - Drag and drop (core + sortable + utilities)
- react-force-graph-2d - 2D knowledge graph visualization
- Three.js + @react-three/fiber + @react-three/drei - installed for legacy 3D scene images (pending cleanup); GalaxyPage uses a 2D KanbanBoard
- @supabase/supabase-js - Supabase client
- @ai-sdk/openai-compatible + ai - AI SDK
- lucide-react - Icon library
- @fontsource/baloo-2, @fontsource/inter, @fontsource/geist-sans, @fontsource/geist-mono - Font packages

<!-- MANUAL: -->