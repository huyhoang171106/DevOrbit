<!-- Parent: ../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# components

## Purpose
Reusable React components organized by role and feature. Contains the UI building blocks for admin interfaces, student experiences, photobooth functionality, and shared layout elements in the DevOrbit web application.

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `admin/` | Admin-specific components (tables, forms, modals, dialogs) |
| `student/` | Student-facing components (cards, Kanban board, knowledge graph, discovery feed) |
| `photobooth/` | Photobooth components (frame selection, photo upload, preview canvas, download, unlock) |

## Key Files
| File | Description |
|------|-------------|
| `Layout.tsx` | Main application layout wrapper |
| `ParticleNetwork.tsx` | Interactive particle canvas background effect |
| `ErrorBoundary.tsx` | React error boundary for route-level crash recovery |
| `admin/CandidateTable.tsx` | Repository candidate approval interface |
| `admin/ApproveModal.tsx` | Repository approval confirmation modal |
| `admin/ApprovedRepoTable.tsx` | Data table for approved repositories |
| `admin/ArticleDialog.tsx` | Article creation/editing dialog |
| `admin/CourseFormDialog.tsx` | Course creation/editing form dialog |
| `admin/CustomSelect.tsx` | Reusable custom select dropdown |
| `admin/ItemDialog.tsx` | Generic item creation/editing dialog |
| `admin/NoteDetailDialog.tsx` | Note detail view/edit dialog |
| `admin/PhaseDialog.tsx` | Course phase creation/editing dialog |
| `admin/RelationshipDialog.tsx` | Relationship creation/editing dialog |
| `admin/RoadmapDialog.tsx` | Roadmap creation/editing dialog |
| `admin/ScanForm.tsx` | GitHub repository scanning form |
| `admin/TutorialDialog.tsx` | Tutorial creation/editing dialog |
| `admin/YoutubePlaylistDialog.tsx` | YouTube playlist creation/editing dialog |
| `student/CareerOrientationData.tsx` | Career orientation data display |
| `student/CourseCard.tsx` | Course display card component |
| `student/CourseKnowledgeGraph.tsx` | Course-specific knowledge graph visualization |
| `student/curriculumData.ts` | Curriculum data utilities |
| `student/DiscoveryFeed.tsx` | Discovery feed component |
| `student/FileTreeExplorer.tsx` | File tree explorer for repository browsing |
| `student/KanbanBoard.tsx` | Kanban board component |
| `student/KanbanCard.tsx` | Kanban board card |
| `student/KanbanColumn.tsx` | Kanban board column |
| `student/RepoCard.tsx` | Repository display card component |
| `student/RepoFilterBar.tsx` | Repository filtering controls |
| `photobooth/DocumentUnlock.tsx` | Document unlock screen |
| `photobooth/DownloadSection.tsx` | Photo download section |
| `photobooth/FrameSelector.tsx` | Frame selection interface |
| `photobooth/PhotoUploadSection.tsx` | Photo upload section |
| `photobooth/PreviewCanvas.tsx` | Photo preview with frame overlay |

## For AI Agents

### Working In This Directory
- Components organized by role (admin/student/photobooth)
- Use TypeScript strict mode
- Follow React 19 best practices
- Implement responsive design with Tailwind CSS 3.4
- Animations via Framer Motion 12

### Testing Requirements
- Component unit tests (Vitest + @testing-library/react)
- Integration tests with API
- Responsive design testing
- Accessibility testing

### Common Patterns
- Functional components with TypeScript
- Props interfaces defined above components
- TanStack React Query for data fetching
- Zustand stores for local state
- Tailwind CSS classes for styling
- Framer Motion for animations

## Dependencies

### Internal
- `../lib/api.ts` - API client
- `../lib/auth.ts` - Authentication utilities
- `../lib/colors.ts` - Color utilities
- `../hooks/` - Custom React hooks
- `../types/` - TypeScript interfaces
- `../pages/` - Parent page components

### External
- React - UI framework
- TypeScript - Type safety
- Tailwind CSS - Styling
- @phosphor-icons/react - Icons
- Framer Motion - Animations
- @dnd-kit - Drag and drop

<!-- MANUAL: --></content>
<parameter name="file_path">devorbit-web/src/components/AGENTS.md