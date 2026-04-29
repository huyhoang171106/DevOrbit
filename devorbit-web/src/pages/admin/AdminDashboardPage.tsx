import { useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { isAuthenticated } from '../../lib/auth'

const links = [
  {
    to: '/admin/courses',
    label: 'Courses',
    description: 'Manage course catalog',
    icon: (
      <svg className="h-8 w-8 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
      </svg>
    ),
  },
  {
    to: '/admin/scan',
    label: 'Scan GitHub',
    description: 'Discover repositories via GitHub search',
    icon: (
      <svg className="h-8 w-8 text-cyan-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
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
      <svg className="h-8 w-8 text-violet-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
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
      <svg className="h-8 w-8 text-green-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M22 12A10 10 0 1 1 12 2a10 10 0 0 1 10 10z" />
        <path d="M8 14s1.5 2 4 2 4-2 4-2" />
        <path d="M9 9h.01M15 9h.01" />
      </svg>
    ),
  },
]

export function AdminDashboardPage() {
  const navigate = useNavigate()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  return (
    <div>
      <h1 className="page-title mb-8">Admin Dashboard</h1>
      <div className="grid gap-5 sm:grid-cols-2">
        {links.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="glass-card group p-6 transition-all duration-300 hover:scale-[1.02]"
          >
            <div className="mb-3">{l.icon}</div>
            <h2 className="text-lg font-semibold text-slate-100 transition-colors group-hover:text-amber-400">
              {l.label}
            </h2>
            <p className="mt-1 text-sm text-slate-400">{l.description}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
