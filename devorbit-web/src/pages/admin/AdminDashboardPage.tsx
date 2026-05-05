import { Link } from 'react-router-dom'
import { useRequireAuth } from '../../lib/hooks'

const links = [
  {
    to: '/admin/courses',
    label: 'Courses',
    description: 'Manage course catalog',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
      </svg>
    ),
  },
  {
    to: '/admin/scan',
    label: 'Scan GitHub',
    description: 'Discover repositories via GitHub search',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <circle cx="11" cy="11" r="8" />
        <path d="M21 21l-4.35-4.35" />
      </svg>
    ),
  },
  {
    to: '/admin/candidates',
    label: 'Candidates',
    description: 'Review & approve repo candidates',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M9 12l2 2 4-4" />
        <circle cx="12" cy="12" r="10" />
      </svg>
    ),
  },
  {
    to: '/admin/repos',
    label: 'Approved Repos',
    description: 'View and manage approved repositories',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M22 12A10 10 0 1 1 12 2a10 10 0 0 1 10 10z" />
        <path d="M8 14s1.5 2 4 2 4-2 4-2" />
        <path d="M9 9h.01M15 9h.01" />
      </svg>
    ),
  },
  {
    to: '/admin/roadmaps',
    label: 'Roadmaps',
    description: 'Manage learning roadmaps, phases & items',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l5.447 2.724A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
      </svg>
    ),
  },
  {
    to: '/admin/relationships',
    label: 'Relationships',
    description: 'Define course prerequisites & relations',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M7 17l5-5-5-5M17 17l-5-5 5-5" />
      </svg>
    ),
  },
  {
    to: '/admin/notes',
    label: 'Notes',
    description: 'Browse student notes and code snippets',
    icon: (
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
        <polyline points="14 2 14 8 20 8" />
        <line x1="16" y1="13" x2="8" y2="13" />
        <line x1="16" y1="17" x2="8" y2="17" />
        <polyline points="10 9 9 9 8 9" />
      </svg>
    ),
  },
]

export function AdminDashboardPage() {
  useRequireAuth()

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[40px]">
        <h1 className="display-sm mb-2">Admin Dashboard</h1>
        <p className="body-md max-w-[600px]">Manage courses, scan GitHub, review repository candidates, and more.</p>
      </div>

      <div className="grid gap-[24px] sm:grid-cols-2 lg:grid-cols-3">
        {links.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="glass-card-hover p-8 block group cursor-pointer"
          >
            <div className="inline-flex rounded-full border border-black/10 dark:border-white/10 bg-black/5 dark:bg-white/5 p-3 text-slate-900 dark:text-slate-100 mb-4 group-hover:text-emerald-500 transition-colors">
              {l.icon}
            </div>
            <h2 className="heading-4 text-slate-900 dark:text-slate-100 mb-2 group-hover:text-emerald-500 transition-colors">
              {l.label}
            </h2>
            <p className="body-sm mb-4">{l.description}</p>
            <div className="flex items-center gap-1 body-sm-medium text-emerald-500">
              <span>Open</span>
              <svg className="h-3.5 w-3.5 transition-transform group-hover:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M5 12h14M12 5l7 7-7 7" />
              </svg>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}
