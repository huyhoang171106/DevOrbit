<!-- Parent: ../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# components

## Purpose
Reusable React components organized by user role and functionality. Contains the UI building blocks for admin and student interfaces in the DevOrbit web application.

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `admin/` | Admin-specific components (tables, forms, modals) |
| `student/` | Student-facing components (cards, filters) |

## Key Files
| File | Description |
|------|-------------|
| `Layout.tsx` | Main application layout component |
| `admin/ApprovedRepoTable.tsx` | Data table for approved repositories |
| `admin/CandidateTable.tsx` | Repository candidate approval interface |
| `admin/CourseFormDialog.tsx` | Course creation/editing form |
| `admin/ApproveModal.tsx` | Repository approval confirmation |
| `student/CourseCard.tsx` | Course display card component |
| `student/RepoCard.tsx` | Repository display card component |
| `student/RepoFilterBar.tsx` | Repository filtering controls |

## For AI Agents

### Working In This Directory
- Components organized by user role
- Use TypeScript strict mode
- Follow React best practices
- Implement responsive design
- Use Tailwind CSS for styling

### Testing Requirements
- Component unit tests
- Integration tests with API
- Responsive design testing
- Accessibility testing

### Common Patterns
- Functional components with TypeScript
- Props interfaces defined above components
- useState/useEffect for state management
- Tailwind CSS classes for styling
- Export via index files

## Dependencies

### Internal
- `../lib/api.ts` - API client
- `../lib/auth.ts` - Authentication utilities
- `../types/` - TypeScript interfaces
- `../pages/` - Parent page components

### External
- React - UI framework
- TypeScript - Type safety
- Tailwind CSS - Styling
- Lucide React - Icons

<!-- MANUAL: --></content>
<parameter name="file_path">devorbit-web/src/components/AGENTS.md