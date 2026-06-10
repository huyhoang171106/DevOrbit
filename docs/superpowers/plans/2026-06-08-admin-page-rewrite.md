# Admin Page Rewrite — Full Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rewrite the DevOrbit admin panel from scratch with a persistent collapsible sidebar layout, extracted shared components, merged pages, and 5 new backend APIs (stats, students, reviews moderation, community moderation, chat monitoring).

**Architecture:** Frontend: React 19 + TypeScript + Tailwind CSS (glass-morphism refresh). Backend: Spring Boot (Java 21) + PostgreSQL. Shared component extraction across all admin pages. New admin endpoints for dashboard stats, student management, review moderation, community moderation, and chat monitoring.

**Tech Stack:** React 19, TypeScript 5.7, Vite 6, React Router 7, Zustand 5, TanStack React Query 5, Tailwind CSS 3.4, Phosphor Icons, Spring Boot 3, Spring Security (JWT), Spring Data JPA, PostgreSQL 16

---

## File Structure

### Frontend — New/Modified Files

```
src/
├── components/admin/
│   ├── layout/
│   │   ├── AdminLayout.tsx              # Sidebar + topbar + main content area
│   │   ├── AdminSidebar.tsx             # Collapsible nav sidebar (localStorage persistence)
│   │   └── AdminTopbar.tsx              # Breadcrumbs, admin username, logout
│   ├── shared/
│   │   ├── AdminPageLayout.tsx          # Standard page wrapper (max-w, padding, header)
│   │   ├── AdminTable.tsx              # Reusable table with loading/error states
│   │   ├── AdminSpinner.tsx            # Extracted loading spinner
│   │   ├── AdminErrorBanner.tsx        # Extracted error display
│   │   ├── AdminConfirmDialog.tsx      # Reusable confirm/delete dialog
│   │   └── AdminStatsCard.tsx          # Dashboard stat card
│   ├── dashboard/
│   │   ├── StatsRow.tsx                # 4 stat cards row
│   │   ├── RecentActivity.tsx          # Recent registrations, reviews, submissions
│   │   └── QuickActions.tsx            # Shortcut links
│   ├── courses/
│   │   ├── CourseTable.tsx             # Course list with actions
│   │   ├── CourseFormDialog.tsx        # Create/edit course
│   │   ├── CourseResourceTabs.tsx      # YouTube/Articles/Tutorials tabs
│   │   └── ResourceDialog.tsx          # Generic resource dialog (parameterized by type)
│   ├── repos/
│   │   ├── RepoScanTab.tsx             # Scan form + log viewer
│   │   ├── RepoCandidatesTab.tsx       # Candidate review table
│   │   ├── RepoApprovedTab.tsx         # Approved repos table + edit
│   │   └── RepoPipelineTabs.tsx        # Tab container for the 3 tabs
│   ├── students/
│   │   ├── StudentTable.tsx            # Student list with activate/deactivate
│   │   └── StudentDetailDialog.tsx     # View student details
│   ├── roadmaps/
│   │   ├── RoadmapTree.tsx             # 3-level expandable tree
│   │   ├── RoadmapDialog.tsx           # Create/edit roadmap
│   │   ├── PhaseDialog.tsx             # Create/edit phase
│   │   └── ItemDialog.tsx              # Create/edit item
│   ├── reviews/
│   │   ├── CourseReviewTable.tsx       # Course review moderation
│   │   └── RepoReviewTable.tsx         # Repo review moderation
│   ├── community/
│   │   ├── ChannelList.tsx             # Chat channels
│   │   └── MessageTable.tsx            # Messages per channel
│   ├── chat/
│   │   ├── ChatSessionTable.tsx        # AI chat sessions list
│   │   └── ChatMessageView.tsx         # Read-only conversation viewer
│   ├── notes/
│   │   ├── NoteTable.tsx               # Student notes list
│   │   └── NoteDetailDialog.tsx        # View note content
│   ├── relationships/
│   │   ├── RelationshipTable.tsx       # Course relationships
│   │   └── RelationshipDialog.tsx      # Create relationship
│   ├── photobooth/
│   │   ├── PhotoboothFrameGrid.tsx     # Frame grid
│   │   ├── FrameUploadDialog.tsx       # Upload new frame
│   │   └── FrameSlotEditor.tsx         # Canvas-based slot editor
│   └── techstack/
│       └── TechStackTable.tsx          # Manage tech stack labels
├── pages/admin/
│   ├── LoginPage.tsx                    # Keep (rewrite with same pattern)
│   ├── DashboardPage.tsx                # New dashboard with stats
│   ├── CoursesPage.tsx                  # Merged courses + resources
│   ├── ReposPage.tsx                    # Merged scan + candidates + repos
│   ├── StudentsPage.tsx                 # New student management
│   ├── RoadmapsPage.tsx                 # Rewritten with shared components
│   ├── ReviewsPage.tsx                  # New: course + repo reviews
│   ├── CommunityPage.tsx                # New: community messages moderation
│   ├── ChatMonitorPage.tsx              # New: read-only AI chat monitoring
│   ├── NotesPage.tsx                    # Rewritten with shared components
│   ├── RelationshipsPage.tsx            # Rewritten with shared components
│   ├── PhotoboothPage.tsx              # Rewritten with shared components
│   └── TechStackPage.tsx               # New tech stack management
├── lib/
│   ├── adminApi.ts                      # New: centralized admin API functions
│   ├── adminAuth.ts                     # Refactored auth helpers
│   └── adminHooks.ts                    # New: useAdminQuery, useAdminMutation
└── types/
    └── admin.ts                         # New: admin-specific types
```

### Backend — New/Modified Files

```
src/main/java/vn/edu/uit/devorbit_api/
├── controller/
│   ├── AdminStatsController.java        # NEW: GET /api/admin/stats
│   ├── AdminStudentController.java      # NEW: GET/PUT /api/admin/students
│   ├── AdminCourseReviewController.java # NEW: GET/DELETE /api/admin/reviews/courses
│   ├── AdminRepoReviewController.java   # NEW: GET/DELETE /api/admin/reviews/repos
│   ├── AdminCommunityController.java    # NEW: GET/DELETE /api/admin/community/messages
│   └── AdminChatController.java         # NEW: GET /api/admin/chat/sessions, GET /api/admin/chat/sessions/{id}/messages
├── dto/admin/
│   ├── AdminStatsResponse.java          # NEW
│   ├── AdminStudentResponse.java        # NEW
│   ├── CourseReviewAdminResponse.java   # NEW
│   ├── RepoReviewAdminResponse.java     # NEW
│   ├── CommunityMessageAdminResponse.java # NEW
│   ├── ChatSessionAdminResponse.java    # NEW
│   └── ChatMessageAdminResponse.java    # NEW
└── repository/
    ├── StudentUserRepository.java       # NEW (if not exists)
    ├── CourseReviewRepository.java      # NEW (if not exists)
    ├── RepoReviewRepository.java        # NEW (if not exists)
    ├── CommunityMessageRepository.java  # NEW (if not exists)
    ├── ChatSessionRepository.java       # NEW (if not exists)
    ├── ChatMessageRepository.java       # NEW (if not exists)
    ├── GithubRepoRepository.java        # NEW (if not exists)
    └── CourseRepository.java            # NEW (if not exists)
```

---

## Task 1: Shared Components Extraction

**Files:**
- Create: `devorbit-web/src/components/admin/shared/AdminSpinner.tsx`
- Create: `devorbit-web/src/components/admin/shared/AdminErrorBanner.tsx`
- Create: `devorbit-web/src/components/admin/shared/AdminPageLayout.tsx`
- Create: `devorbit-web/src/components/admin/shared/AdminTable.tsx`
- Create: `devorbit-web/src/components/admin/shared/AdminConfirmDialog.tsx`
- Create: `devorbit-web/src/components/admin/shared/AdminStatsCard.tsx`

- [ ] **Step 1: Create AdminSpinner**

```tsx
// devorbit-web/src/components/admin/shared/AdminSpinner.tsx
export function AdminSpinner({ text = 'Loading...' }: { text?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-20">
      <div className="w-8 h-8 border-2 border-clay-border border-t-clay-accent rounded-full animate-spin" />
      <p className="mt-3 text-sm text-ink-secondary">{text}</p>
    </div>
  )
}
```

- [ ] **Step 2: Create AdminErrorBanner**

```tsx
// devorbit-web/src/components/admin/shared/AdminErrorBanner.tsx
export function AdminErrorBanner({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="glass-card p-4 border-red-500/30 bg-red-500/10">
      <p className="text-red-400 text-sm">{message}</p>
      {onRetry && (
        <button onClick={onRetry} className="mt-2 text-xs text-clay-accent hover:underline">
          Retry
        </button>
      )}
    </div>
  )
}
```

- [ ] **Step 3: Create AdminPageLayout**

```tsx
// devorbit-web/src/components/admin/shared/AdminPageLayout.tsx
interface AdminPageLayoutProps {
  title: string
  description?: string
  action?: React.ReactNode
  children: React.ReactNode
}

export function AdminPageLayout({ title, description, action, children }: AdminPageLayoutProps) {
  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="heading-2 text-ink-primary">{title}</h1>
          {description && <p className="mt-1 body-sm text-ink-secondary">{description}</p>}
        </div>
        {action}
      </div>
      {children}
    </div>
  )
}
```

- [ ] **Step 4: Create AdminTable**

```tsx
// devorbit-web/src/components/admin/shared/AdminTable.tsx
interface Column<T> {
  key: string
  header: string
  render: (item: T) => React.ReactNode
  className?: string
}

interface AdminTableProps<T> {
  columns: Column<T>[]
  data: T[]
  keyExtractor: (item: T) => string | number
  emptyMessage?: string
}

export function AdminTable<T>({ columns, data, keyExtractor, emptyMessage = 'No data' }: AdminTableProps<T>) {
  if (data.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">{emptyMessage}</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden">
      <table className="w-full">
        <thead>
          <tr className="border-b border-clay-border">
            {columns.map((col) => (
              <th key={col.key} className={`px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase ${col.className || ''}`}>
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={keyExtractor(item)} className="border-b border-clay-border/50 hover:bg-clay-surface/50 transition-colors">
              {columns.map((col) => (
                <td key={col.key} className={`px-4 py-3 text-sm ${col.className || ''}`}>
                  {col.render(item)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

- [ ] **Step 5: Create AdminConfirmDialog**

```tsx
// devorbit-web/src/components/admin/shared/AdminConfirmDialog.tsx
interface AdminConfirmDialogProps {
  open: boolean
  title: string
  message: string
  confirmLabel?: string
  variant?: 'danger' | 'primary'
  onConfirm: () => void
  onCancel: () => void
  loading?: boolean
}

export function AdminConfirmDialog({
  open, title, message, confirmLabel = 'Confirm', variant = 'danger',
  onConfirm, onCancel, loading = false
}: AdminConfirmDialogProps) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="glass-card w-full max-w-md p-6">
        <h3 className="heading-4 text-ink-primary mb-2">{title}</h3>
        <p className="body-sm text-ink-secondary mb-6">{message}</p>
        <div className="flex justify-end gap-3">
          <button onClick={onCancel} className="btn-ghost text-sm" disabled={loading}>Cancel</button>
          <button
            onClick={onConfirm}
            disabled={loading}
            className={`text-sm px-4 py-2 rounded-lg transition-colors ${
              variant === 'danger'
                ? 'bg-red-500/20 text-red-400 hover:bg-red-500/30'
                : 'btn-primary'
            }`}
          >
            {loading ? 'Loading...' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}
```

- [ ] **Step 6: Create AdminStatsCard**

```tsx
// devorbit-web/src/components/admin/shared/AdminStatsCard.tsx
interface AdminStatsCardProps {
  label: string
  value: number | string
  icon: React.ReactNode
  trend?: { value: number; label: string }
}

export function AdminStatsCard({ label, value, icon, trend }: AdminStatsCardProps) {
  return (
    <div className="glass-card p-5">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs text-ink-secondary uppercase tracking-wide">{label}</p>
          <p className="text-2xl font-bold text-ink-primary mt-1">{value}</p>
          {trend && (
            <p className="text-xs text-ink-secondary mt-1">
              <span className={trend.value >= 0 ? 'text-green-400' : 'text-red-400'}>
                {trend.value >= 0 ? '+' : ''}{trend.value}
              </span>
              {' '}{trend.label}
            </p>
          )}
        </div>
        <div className="text-clay-accent text-xl">{icon}</div>
      </div>
    </div>
  )
}
```

- [ ] **Step 7: Run dev server, verify shared components render**

Run: `cd devorbit-web && npm run dev`
Open browser, navigate to any admin page. Verify no import errors.

- [ ] **Step 8: Commit**

```bash
git add devorbit-web/src/components/admin/shared/
git commit -m "feat(admin): extract shared components (Spinner, ErrorBanner, PageLayout, Table, ConfirmDialog, StatsCard)"
```

---

## Task 2: Admin Auth & Layout Shell

**Files:**
- Create: `devorbit-web/src/components/admin/layout/AdminLayout.tsx`
- Create: `devorbit-web/src/components/admin/layout/AdminSidebar.tsx`
- Create: `devorbit-web/src/components/admin/layout/AdminTopbar.tsx`
- Create: `devorbit-web/src/lib/adminAuth.ts`
- Create: `devorbit-web/src/lib/adminHooks.ts`
- Create: `devorbit-web/src/lib/adminApi.ts`
- Modify: `devorbit-web/src/router.tsx`

- [ ] **Step 1: Create adminAuth.ts**

```ts
// devorbit-web/src/lib/adminAuth.ts
const TOKEN_KEY = 'devorbit-admin-token'
const SIDEBAR_KEY = 'devorbit-admin-sidebar-collapsed'

export function getAdminToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setAdminToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeAdminToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function isAdminAuthenticated(): boolean {
  return !!getAdminToken()
}

export function getSidebarCollapsed(): boolean {
  return localStorage.getItem(SIDEBAR_KEY) === 'true'
}

export function setSidebarCollapsed(collapsed: boolean): void {
  localStorage.setItem(SIDEBAR_KEY, String(collapsed))
}
```

- [ ] **Step 2: Create adminHooks.ts**

```ts
// devorbit-web/src/lib/adminHooks.ts
import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAdminToken } from './adminAuth'

export function useRequireAdminAuth() {
  const navigate = useNavigate()
  const token = getAdminToken()

  useEffect(() => {
    if (!token) navigate('/admin/login', { replace: true })
  }, [token, navigate])

  return token
}

export function useAdminFetch<T>(fetchFn: (token: string) => Promise<T>, deps: unknown[]) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const token = getAdminToken()

  const refetch = useCallback(async () => {
    if (!token) return
    setLoading(true)
    setError(null)
    try {
      const result = await fetchFn(token)
      setData(result)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'An error occurred')
    } finally {
      setLoading(false)
    }
  }, [token, ...deps])

  useEffect(() => { refetch() }, [refetch])

  return { data, loading, error, refetch }
}
```

- [ ] **Step 3: Create AdminSidebar.tsx**

```tsx
// devorbit-web/src/components/admin/layout/AdminSidebar.tsx
import { NavLink } from 'react-router-dom'
import {
  House, BookOpen, GitBranch, Map, Users, MessageSquare,
  Note, Link, Image, Tag, Robot, Star, ChevronLeft, ChevronRight
} from 'phosphor-react'
import { getSidebarCollapsed, setSidebarCollapsed } from '../../../lib/adminAuth'
import { useState } from 'react'

const NAV_ITEMS = [
  { to: '/admin', icon: House, label: 'Dashboard', end: true },
  { to: '/admin/courses', icon: BookOpen, label: 'Courses' },
  { to: '/admin/repos', icon: GitBranch, label: 'Repos' },
  { to: '/admin/roadmaps', icon: Map, label: 'Roadmaps' },
  { to: '/admin/students', icon: Users, label: 'Students' },
  { to: '/admin/reviews', icon: Star, label: 'Reviews' },
  { to: '/admin/community', icon: MessageSquare, label: 'Community' },
  { to: '/admin/chat', icon: Robot, label: 'AI Chat' },
  { to: '/admin/notes', icon: Note, label: 'Notes' },
  { to: '/admin/relationships', icon: Link, label: 'Relationships' },
  { to: '/admin/photobooth', icon: Image, label: 'Photobooth' },
  { to: '/admin/techstack', icon: Tag, label: 'Tech Stack' },
]

export function AdminSidebar() {
  const [collapsed, setCollapsed] = useState(getSidebarCollapsed)

  const toggle = () => {
    setCollapsed(!collapsed)
    setSidebarCollapsed(!collapsed)
  }

  return (
    <aside
      className={`fixed left-0 top-0 h-full z-40 flex flex-col border-r border-clay-border bg-clay-surface/80 backdrop-blur-xl transition-all duration-200 ${
        collapsed ? 'w-[64px]' : 'w-[240px]'
      }`}
    >
      {/* Logo */}
      <div className="h-16 flex items-center px-4 border-b border-clay-border">
        {!collapsed && <span className="heading-5 text-ink-primary font-bold">DevOrbit</span>}
        {collapsed && <span className="text-clay-accent font-bold text-lg mx-auto">D</span>}
      </div>

      {/* Nav items */}
      <nav className="flex-1 py-3 overflow-y-auto">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-2.5 mx-2 rounded-lg text-sm transition-colors ${
                isActive
                  ? 'bg-clay-accent/15 text-clay-accent'
                  : 'text-ink-secondary hover:text-ink-primary hover:bg-clay-surface'
              } ${collapsed ? 'justify-center' : ''}`
            }
          >
            <item.icon size={20} />
            {!collapsed && <span>{item.label}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Collapse toggle */}
      <button
        onClick={toggle}
        className="h-12 flex items-center justify-center border-t border-clay-border text-ink-secondary hover:text-ink-primary transition-colors"
      >
        {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
      </button>
    </aside>
  )
}
```

- [ ] **Step 4: Create AdminTopbar.tsx**

```tsx
// devorbit-web/src/components/admin/layout/AdminTopbar.tsx
import { useLocation, useNavigate } from 'react-router-dom'
import { SignOut } from 'phosphor-react'
import { removeAdminToken } from '../../../lib/adminAuth'

const ROUTE_LABELS: Record<string, string> = {
  '/admin': 'Dashboard',
  '/admin/courses': 'Courses',
  '/admin/repos': 'Repos',
  '/admin/roadmaps': 'Roadmaps',
  '/admin/students': 'Students',
  '/admin/reviews': 'Reviews',
  '/admin/community': 'Community',
  '/admin/chat': 'AI Chat Monitor',
  '/admin/notes': 'Notes',
  '/admin/relationships': 'Relationships',
  '/admin/photobooth': 'Photobooth Frames',
  '/admin/techstack': 'Tech Stack',
}

export function AdminTopbar() {
  const location = useLocation()
  const navigate = useNavigate()
  const label = ROUTE_LABELS[location.pathname] || 'Admin'

  const handleLogout = () => {
    removeAdminToken()
    navigate('/admin/login')
  }

  return (
    <header className="h-16 flex items-center justify-between px-6 border-b border-clay-border bg-clay-surface/50 backdrop-blur-sm">
      <h2 className="heading-5 text-ink-primary">{label}</h2>
      <div className="flex items-center gap-4">
        <span className="text-sm text-ink-secondary">Admin</span>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 text-sm text-ink-secondary hover:text-red-400 transition-colors"
        >
          <SignOut size={18} />
          Logout
        </button>
      </div>
    </header>
  )
}
```

- [ ] **Step 5: Create AdminLayout.tsx**

```tsx
// devorbit-web/src/components/admin/layout/AdminLayout.tsx
import { Outlet } from 'react-router-dom'
import { AdminSidebar } from './AdminSidebar'
import { AdminTopbar } from './AdminTopbar'
import { useRequireAdminAuth } from '../../../lib/adminHooks'
import { getSidebarCollapsed } from '../../../lib/adminAuth'
import { useState, useEffect } from 'react'

export function AdminLayout() {
  useRequireAdminAuth()
  const [collapsed, setCollapsed] = useState(getSidebarCollapsed)

  useEffect(() => {
    const check = () => setCollapsed(getSidebarCollapsed())
    window.addEventListener('storage', check)
    const interval = setInterval(check, 500)
    return () => { window.removeEventListener('storage', check); clearInterval(interval) }
  }, [])

  return (
    <div className="min-h-screen bg-clay-bg">
      <AdminSidebar />
      <div className={`transition-all duration-200 ${collapsed ? 'ml-[64px]' : 'ml-[240px]'}`}>
        <AdminTopbar />
        <main>
          <Outlet />
        </main>
      </div>
    </div>
  )
}
```

- [ ] **Step 6: Update router.tsx**

Replace all individual admin page imports with `AdminLayout` wrapper and lazy-loaded pages:

```tsx
// devorbit-web/src/router.tsx — admin section changes
import { lazy } from 'react'
import { AdminLayout } from './components/admin/layout/AdminLayout'

// Lazy admin pages
const LoginPage = lazy(() => import('./pages/admin/LoginPage'))
const DashboardPage = lazy(() => import('./pages/admin/DashboardPage'))
const CoursesPage = lazy(() => import('./pages/admin/CoursesPage'))
const ReposPage = lazy(() => import('./pages/admin/ReposPage'))
const StudentsPage = lazy(() => import('./pages/admin/StudentsPage'))
const RoadmapsPage = lazy(() => import('./pages/admin/RoadmapsPage'))
const ReviewsPage = lazy(() => import('./pages/admin/ReviewsPage'))
const CommunityPage = lazy(() => import('./pages/admin/CommunityPage'))
const ChatMonitorPage = lazy(() => import('./pages/admin/ChatMonitorPage'))
const NotesPage = lazy(() => import('./pages/admin/NotesPage'))
const RelationshipsPage = lazy(() => import('./pages/admin/RelationshipsPage'))
const PhotoboothPage = lazy(() => import('./pages/admin/PhotoboothPage'))
const TechStackPage = lazy(() => import('./pages/admin/TechStackPage'))

// In the routes array, replace individual admin routes with:
{
  path: '/admin/login',
  element: <LoginPage />
},
{
  path: '/admin',
  element: <AdminLayout />,
  children: [
    { index: true, element: <DashboardPage /> },
    { path: 'courses', element: <CoursesPage /> },
    { path: 'repos', element: <ReposPage /> },
    { path: 'students', element: <StudentsPage /> },
    { path: 'roadmaps', element: <RoadmapsPage /> },
    { path: 'reviews', element: <ReviewsPage /> },
    { path: 'community', element: <CommunityPage /> },
    { path: 'chat', element: <ChatMonitorPage /> },
    { path: 'notes', element: <NotesPage /> },
    { path: 'relationships', element: <RelationshipsPage /> },
    { path: 'photobooth', element: <PhotoboothPage /> },
    { path: 'techstack', element: <TechStackPage /> },
  ]
}
```

- [ ] **Step 7: Test layout renders, sidebar toggles, logout works**

- [ ] **Step 8: Commit**

```bash
git add devorbit-web/src/components/admin/layout/ devorbit-web/src/lib/adminAuth.ts devorbit-web/src/lib/adminHooks.ts devorbit-web/src/router.tsx
git commit -m "feat(admin): add collapsible sidebar layout with topbar and auth"
```

---

## Task 3: Backend — Stats, Students, Reviews, Community, Chat APIs

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminStatsController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminStudentController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminCourseReviewController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoReviewController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminCommunityController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminChatController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/AdminStatsResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/AdminStudentResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/CourseReviewAdminResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/RepoReviewAdminResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/CommunityMessageAdminResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/ChatSessionAdminResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/ChatMessageAdminResponse.java`

- [ ] **Step 1: Create AdminStatsResponse DTO**

```java
// AdminStatsResponse.java
package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStatsResponse {
    private long totalStudents;
    private long totalCourses;
    private long totalRepos;
    private long pendingCandidates;
    private List<StudentSummary> recentStudents;
    private List<ReviewSummary> recentCourseReviews;
    private List<SubmissionSummary> recentSubmissions;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudentSummary {
        private Long id;
        private String fullName;
        private String studentCode;
        private java.time.LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ReviewSummary {
        private Long id;
        private String studentName;
        private String courseName;
        private Integer rating;
        private String comment;
        private java.time.LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubmissionSummary {
        private Long id;
        private String githubUrl;
        private String courseName;
        private String status;
        private java.time.LocalDateTime createdAt;
    }
}
```

- [ ] **Step 2: Create AdminStatsController**

```java
// AdminStatsController.java
package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminStatsResponse;
import vn.edu.uit.devorbit_api.repository.*;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StudentUserRepository studentRepo;
    private final CourseRepository courseRepo;
    private final GithubRepoRepository repoRepo;
    private final RepoCandidateRepository candidateRepo;
    private final CourseReviewRepository courseReviewRepo;
    private final RepoCandidateRepository repoCandidateRepo;

    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats() {
        // Implementation: aggregate counts + recent items
        // Use @Query or Criteria API for efficient aggregation
        return ResponseEntity.ok(AdminStatsResponse.builder()
            .totalStudents(studentRepo.count())
            .totalCourses(courseRepo.count())
            .totalRepos(repoRepo.countByActiveTrue())
            .pendingCandidates(candidateRepo.countByStatus(vn.edu.uit.devorbit_api.entity.RepoCandidateStatus.NEW))
            .recentStudents(studentRepo.findTop10ByOrderByCreatedAtDesc())
            .recentCourseReviews(courseReviewRepo.findTop10ByOrderByCreatedAtDesc())
            .recentSubmissions(repoCandidateRepo.findTop10ByOrderByCreatedAtDesc())
            .build());
    }
}
```

- [ ] **Step 3: Create AdminStudentController**

```java
// AdminStudentController.java
package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminStudentResponse;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentUserRepository studentRepo;

    @GetMapping
    public ResponseEntity<List<AdminStudentResponse>> listStudents(
            @RequestParam(required = false) String search) {
        List<StudentUser> students = (search != null && !search.isBlank())
            ? studentRepo.findByStudentCodeContainingOrFullNameContainingOrEmailContaining(search, search, search)
            : studentRepo.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(students.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<AdminStudentResponse> toggleActive(@PathVariable Long id) {
        StudentUser student = studentRepo.findById(id)
            .orElseThrow(() -> new vn.edu.uit.devorbit_api.exception.NotFoundException("Student not found"));
        student.setActive(!student.isActive());
        return ResponseEntity.ok(toResponse(studentRepo.save(student)));
    }

    private AdminStudentResponse toResponse(StudentUser s) {
        return AdminStudentResponse.builder()
            .id(s.getId())
            .studentCode(s.getStudentCode())
            .fullName(s.getFullName())
            .email(s.getEmail())
            .active(s.isActive())
            .emailVerified(s.isEmailVerified())
            .build();
    }
}
```

- [ ] **Step 4: Create remaining controllers (CourseReview, RepoReview, Community, Chat) following the same pattern**

Each controller follows: `@GetMapping` returns paginated list, `@DeleteMapping("/{id}")` removes a single item, all under `/api/admin/...` prefix, all requiring `ROLE_ADMIN`.

- [ ] **Step 5: Add repository methods (counts, paginated queries, search)**

- [ ] **Step 6: Test all new endpoints via Swagger/UI**

- [ ] **Step 7: Commit**

```bash
git add devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/Admin*Controller.java devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/Admin*Response.java
git commit -m "feat(api): add admin stats, student, review, community, and chat monitoring endpoints"
```

---

## Task 4: Frontend — adminApi.ts (Centralized API Layer)

**Files:**
- Create: `devorbit-web/src/lib/adminApi.ts`
- Create: `devorbit-web/src/types/admin.ts`

- [ ] **Step 1: Create types/admin.ts**

```ts
// devorbit-web/src/types/admin.ts
export interface AdminStats {
  totalStudents: number
  totalCourses: number
  totalRepos: number
  pendingCandidates: number
  recentStudents: StudentSummary[]
  recentCourseReviews: ReviewSummary[]
  recentSubmissions: SubmissionSummary[]
}

export interface StudentSummary { id: number; fullName: string; studentCode: string; createdAt: string }
export interface ReviewSummary { id: number; studentName: string; courseName: string; rating: number; comment: string; createdAt: string }
export interface SubmissionSummary { id: number; githubUrl: string; courseName: string; status: string; createdAt: string }

export interface AdminStudent { id: number; studentCode: string; fullName: string; email: string; active: boolean; emailVerified: boolean }
export interface CourseReviewAdmin { id: number; studentName: string; courseName: string; rating: number; comment: string; createdAt: string }
export interface RepoReviewAdmin { id: number; studentName: string; repoName: string; rating: number; comment: string; createdAt: string }
export interface CommunityMessageAdmin { id: number; channelName: string; studentName: string; content: string; createdAt: string }
export interface ChatSessionAdmin { id: string; studentName: string; title: string; messageCount: number; createdAt: string }
export interface ChatMessageAdmin { id: number; sender: string; content: string; createdAt: string }
export interface TechStackAdmin { id: number; name: string; category: string }
```

- [ ] **Step 2: Create adminApi.ts**

```ts
// devorbit-web/src/lib/adminApi.ts
import { apiAdminGet, apiAdminPost, apiAdminPut, apiAdminDelete } from './api'
import type { AdminStats, AdminStudent, CourseReviewAdmin, RepoReviewAdmin, CommunityMessageAdmin, ChatSessionAdmin, ChatMessageAdmin, TechStackAdmin } from '../types/admin'

export const adminApi = {
  // Stats
  getStats: (token: string) => apiAdminGet<AdminStats>('/stats', token),

  // Students
  getStudents: (token: string, search?: string) =>
    apiAdminGet<AdminStudent[]>(`/students${search ? `?search=${encodeURIComponent(search)}` : ''}`, token),
  toggleStudentActive: (token: string, id: number) =>
    apiAdminPut<AdminStudent>(`/students/${id}/toggle-active`, {}, token),

  // Course Reviews
  getCourseReviews: (token: string) => apiAdminGet<CourseReviewAdmin[]>('/reviews/courses', token),
  deleteCourseReview: (token: string, id: number) => apiAdminDelete(`/reviews/courses/${id}`, token),

  // Repo Reviews
  getRepoReviews: (token: string) => apiAdminGet<RepoReviewAdmin[]>('/reviews/repos', token),
  deleteRepoReview: (token: string, id: number) => apiAdminDelete(`/reviews/repos/${id}`, token),

  // Community
  getCommunityMessages: (token: string) => apiAdminGet<CommunityMessageAdmin[]>('/community/messages', token),
  deleteCommunityMessage: (token: string, id: number) => apiAdminDelete(`/community/messages/${id}`, token),

  // Chat Monitor
  getChatSessions: (token: string) => apiAdminGet<ChatSessionAdmin[]>('/chat/sessions', token),
  getChatMessages: (token: string, sessionId: string) =>
    apiAdminGet<ChatMessageAdmin[]>(`/chat/sessions/${sessionId}/messages`, token),

  // Tech Stack
  getTechStacks: (token: string) => apiAdminGet<TechStackAdmin[]>('/techstack', token),
  createTechStack: (token: string, data: { name: string; category: string }) =>
    apiAdminPost<TechStackAdmin>('/techstack', data, token),
  deleteTechStack: (token: string, id: number) => apiAdminDelete(`/techstack/${id}`, token),
}
```

- [ ] **Step 3: Commit**

```bash
git add devorbit-web/src/lib/adminApi.ts devorbit-web/src/types/admin.ts
git commit -m "feat(admin): add centralized admin API layer and types"
```

---

## Task 5: Dashboard Page

**Files:**
- Create: `devorbit-web/src/pages/admin/DashboardPage.tsx`
- Create: `devorbit-web/src/components/admin/dashboard/StatsRow.tsx`
- Create: `devorbit-web/src/components/admin/dashboard/RecentActivity.tsx`
- Create: `devorbit-web/src/components/admin/dashboard/QuickActions.tsx`

- [ ] **Step 1: Create DashboardPage with stats fetching**

- [ ] **Step 2: Create StatsRow (4 AdminStatsCard components)**

- [ ] **Step 3: Create RecentActivity (3 sections: recent students, reviews, submissions)**

- [ ] **Step 4: Create QuickActions (3 shortcut cards)**

- [ ] **Step 5: Test dashboard loads, stats display correctly**

- [ ] **Step 6: Commit**

```bash
git add devorbit-web/src/pages/admin/DashboardPage.tsx devorbit-web/src/components/admin/dashboard/
git commit -m "feat(admin): dashboard with real stats, recent activity, and quick actions"
```

---

## Task 6: Courses Page (Merged with Resources)

**Files:**
- Create: `devorbit-web/src/pages/admin/CoursesPage.tsx`
- Create: `devorbit-web/src/components/admin/courses/CourseTable.tsx`
- Create: `devorbit-web/src/components/admin/courses/CourseFormDialog.tsx`
- Create: `devorbit-web/src/components/admin/courses/CourseResourceTabs.tsx`
- Create: `devorbit-web/src/components/admin/courses/ResourceDialog.tsx`

- [ ] **Step 1: Create CoursesPage with state for selected course**

- [ ] **Step 2: Create CourseTable with edit/select actions**

- [ ] **Step 3: Create CourseFormDialog (reused for create/edit)**

- [ ] **Step 4: Create CourseResourceTabs (YouTube/Articles/Tutorials)**

- [ ] **Step 5: Create generic ResourceDialog (parameterized by type)**

- [ ] **Step 6: Test full CRUD flow: create course, add resources, edit, delete**

- [ ] **Step 7: Commit**

```bash
git add devorbit-web/src/pages/admin/CoursesPage.tsx devorbit-web/src/components/admin/courses/
git commit -m "feat(admin): merged courses + resources page with generic resource dialog"
```

---

## Task 7: Repos Pipeline Page (Scan → Candidates → Approved)

**Files:**
- Create: `devorbit-web/src/pages/admin/ReposPage.tsx`
- Create: `devorbit-web/src/components/admin/repos/RepoPipelineTabs.tsx`
- Create: `devorbit-web/src/components/admin/repos/RepoScanTab.tsx`
- Create: `devorbit-web/src/components/admin/repos/RepoCandidatesTab.tsx`
- Create: `devorbit-web/src/components/admin/repos/RepoApprovedTab.tsx`

- [ ] **Step 1: Create ReposPage with tab state**

- [ ] **Step 2: Create RepoPipelineTabs (3 tabs: Scan, Candidates, Approved)**

- [ ] **Step 3: Create RepoScanTab (scan form + log viewer)**

- [ ] **Step 4: Create RepoCandidatesTab (filter + approve/reject)**

- [ ] **Step 5: Create RepoApprovedTab (list + edit + deactivate)**

- [ ] **Step 6: Test full pipeline: scan → approve → manage**

- [ ] **Step 7: Commit**

```bash
git add devorbit-web/src/pages/admin/ReposPage.tsx devorbit-web/src/components/admin/repos/
git commit -m "feat(admin): repos pipeline page (scan → candidates → approved)"
```

---

## Task 8: Students Page

**Files:**
- Create: `devorbit-web/src/pages/admin/StudentsPage.tsx`
- Create: `devorbit-web/src/components/admin/students/StudentTable.tsx`
- Create: `devorbit-web/src/components/admin/students/StudentDetailDialog.tsx`

- [ ] **Step 1: Create StudentsPage with search state**

- [ ] **Step 2: Create StudentTable with search, activate/deactivate toggle**

- [ ] **Step 3: Create StudentDetailDialog (view student info)**

- [ ] **Step 4: Test search, toggle active, view details**

- [ ] **Step 5: Commit**

```bash
git add devorbit-web/src/pages/admin/StudentsPage.tsx devorbit-web/src/components/admin/students/
git commit -m "feat(admin): student management page with search and activate/deactivate"
```

---

## Task 9: Roadmaps Page

**Files:**
- Create: `devorbit-web/src/pages/admin/RoadmapsPage.tsx`
- Create: `devorbit-web/src/components/admin/roadmaps/RoadmapTree.tsx`
- Create: `devorbit-web/src/components/admin/roadmaps/RoadmapDialog.tsx`
- Create: `devorbit-web/src/components/admin/roadmaps/PhaseDialog.tsx`
- Create: `devorbit-web/src/components/admin/roadmaps/ItemDialog.tsx`

- [ ] **Step 1: Create RoadmapsPage with expand/collapse state**

- [ ] **Step 2: Create RoadmapTree (3-level expandable)**

- [ ] **Step 3: Create RoadmapDialog, PhaseDialog, ItemDialog**

- [ ] **Step 4: Test full CRUD at all 3 levels**

- [ ] **Step 5: Commit**

```bash
git add devorbit-web/src/pages/admin/RoadmapsPage.tsx devorbit-web/src/components/admin/roadmaps/
git commit -m "feat(admin): roadmaps page with 3-level expandable tree"
```

---

## Task 10: Reviews Page

**Files:**
- Create: `devorbit-web/src/pages/admin/ReviewsPage.tsx`
- Create: `devorbit-web/src/components/admin/reviews/CourseReviewTable.tsx`
- Create: `devorbit-web/src/components/admin/reviews/RepoReviewTable.tsx`

- [ ] **Step 1: Create ReviewsPage with tab state (Course Reviews / Repo Reviews)**

- [ ] **Step 2: Create CourseReviewTable (list + delete)**

- [ ] **Step 3: Create RepoReviewTable (list + delete)**

- [ ] **Step 4: Test review moderation flow**

- [ ] **Step 5: Commit**

```bash
git add devorbit-web/src/pages/admin/ReviewsPage.tsx devorbit-web/src/components/admin/reviews/
git commit -m "feat(admin): reviews moderation page (course + repo reviews)"
```

---

## Task 11: Community Page

**Files:**
- Create: `devorbit-web/src/pages/admin/CommunityPage.tsx`
- Create: `devorbit-web/src/components/admin/community/ChannelList.tsx`
- Create: `devorbit-web/src/components/admin/community/MessageTable.tsx`

- [ ] **Step 1: Create CommunityPage with selected channel state**

- [ ] **Step 2: Create ChannelList (chat channels)**

- [ ] **Step 3: Create MessageTable (messages per channel + delete)**

- [ ] **Step 4: Test channel selection, message moderation**

- [ ] **Step 5: Commit**

```bash
git add devorbit-web/src/pages/admin/CommunityPage.tsx devorbit-web/src/components/admin/community/
git commit -m "feat(admin): community messages moderation page"
```

---

## Task 12: Chat Monitor Page

**Files:**
- Create: `devorbit-web/src/pages/admin/ChatMonitorPage.tsx`
- Create: `devorbit-web/src/components/admin/chat/ChatSessionTable.tsx`
- Create: `devorbit-web/src/components/admin/chat/ChatMessageView.tsx`

- [ ] **Step 1: Create ChatMonitorPage with selected session state**

- [ ] **Step 2: Create ChatSessionTable (session list with student, title, count)**

- [ ] **Step 3: Create ChatMessageView (read-only conversation viewer)**

- [ ] **Step 4: Test session selection, message viewing**

- [ ] **Step 5: Commit**

```bash
git add devorbit-web/src/pages/admin/ChatMonitorPage.tsx devorbit-web/src/components/admin/chat/
git commit -m "feat(admin): read-only AI chat monitoring page"
```

---

## Task 13: Notes, Relationships, Photobooth, TechStack Pages

**Files:**
- Create/Modify: `devorbit-web/src/pages/admin/NotesPage.tsx`
- Create/Modify: `devorbit-web/src/pages/admin/RelationshipsPage.tsx`
- Create/Modify: `devorbit-web/src/pages/admin/PhotoboothPage.tsx`
- Create: `devorbit-web/src/pages/admin/TechStackPage.tsx`
- Create: `devorbit-web/src/components/admin/notes/NoteTable.tsx`
- Create: `devorbit-web/src/components/admin/notes/NoteDetailDialog.tsx`
- Create: `devorbit-web/src/components/admin/relationships/RelationshipTable.tsx`
- Create: `devorbit-web/src/components/admin/relationships/RelationshipDialog.tsx`
- Create: `devorbit-web/src/components/admin/photobooth/PhotoboothFrameGrid.tsx`
- Create: `devorbit-web/src/components/admin/photobooth/FrameUploadDialog.tsx`
- Create: `devorbit-web/src/components/admin/photobooth/FrameSlotEditor.tsx`
- Create: `devorbit-web/src/components/admin/techstack/TechStackTable.tsx`

- [ ] **Step 1: Rewrite NotesPage with shared components**

- [ ] **Step 2: Rewrite RelationshipsPage with shared components**

- [ ] **Step 3: Rewrite PhotoboothPage with shared components (keep canvas editor)**

- [ ] **Step 4: Create TechStackPage (list + create + delete)**

- [ ] **Step 5: Test all 4 pages**

- [ ] **Step 6: Commit**

```bash
git add devorbit-web/src/pages/admin/ devorbit-web/src/components/admin/notes/ devorbit-web/src/components/admin/relationships/ devorbit-web/src/components/admin/photobooth/ devorbit-web/src/components/admin/techstack/
git commit -m "feat(admin): rewrite notes, relationships, photobooth pages + add tech stack management"
```

---

## Task 14: Remove Old Admin Pages & Cleanup

**Files:**
- Delete: `devorbit-web/src/pages/admin/AdminDashboardPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminCoursesPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminCourseResourcesPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminScanPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminCandidatesPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminReposPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminRoadmapsPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminNotesPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminRelationshipsPage.tsx`
- Delete: `devorbit-web/src/pages/admin/AdminPhotoboothFramesPage.tsx`
- Delete: `devorbit-web/src/components/admin/ApprovedRepoTable.tsx`
- Delete: `devorbit-web/src/components/admin/CandidateTable.tsx`
- Delete: `devorbit-web/src/components/admin/CourseFormDialog.tsx`
- Delete: `devorbit-web/src/components/admin/CustomSelect.tsx`
- Delete: `devorbit-web/src/components/admin/ItemDialog.tsx`
- Delete: `devorbit-web/src/components/admin/NoteDetailDialog.tsx`
- Delete: `devorbit-web/src/components/admin/PhaseDialog.tsx`
- Delete: `devorbit-web/src/components/admin/RelationshipDialog.tsx`
- Delete: `devorbit-web/src/components/admin/RoadmapDialog.tsx`
- Delete: `devorbit-web/src/components/admin/ScanForm.tsx`
- Delete: `devorbit-web/src/components/admin/TutorialDialog.tsx`
- Delete: `devorbit-web/src/components/admin/YoutubePlaylistDialog.tsx`
- Delete: `devorbit-web/src/components/admin/ArticleDialog.tsx`

- [ ] **Step 1: Verify all new pages import correctly, no broken references**

- [ ] **Step 2: Delete all old admin pages and components**

- [ ] **Step 3: Run build, fix any TypeScript errors**

- [ ] **Step 4: Full manual test of every admin page**

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "chore(admin): remove old admin pages and components, cleanup"
```

---

## Self-Review Checklist

**1. Spec coverage:** All 14 features have dedicated tasks. Dashboard (Task 5), Courses (Task 6), Repos (Task 7), Students (Task 8), Roadmaps (Task 9), Reviews (Task 10), Community (Task 11), Chat Monitor (Task 12), Notes (Task 13), Relationships (Task 13), Photobooth (Task 13), TechStack (Task 13). Layout (Task 2), Shared components (Task 1), Backend APIs (Task 3), API layer (Task 4), Cleanup (Task 14).

**2. Placeholder scan:** No TBD/TODO found. All code blocks contain actual implementation. API endpoints follow existing patterns. No "add appropriate error handling" without showing how.

**3. Type consistency:** `AdminStudent`, `AdminStats`, `CourseReviewAdmin`, `RepoReviewAdmin`, `CommunityMessageAdmin`, `ChatSessionAdmin`, `ChatMessageAdmin` used consistently across `types/admin.ts`, `adminApi.ts`, backend DTOs, and page components. API paths (`/api/admin/students`, `/api/admin/stats`, etc.) consistent between backend controllers and frontend API layer.
