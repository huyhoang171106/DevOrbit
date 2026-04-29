import { useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { isAuthenticated } from '../../lib/auth'

const links = [
  { to: '/admin/courses', label: 'Courses', description: 'Manage course catalog' },
  { to: '/admin/scan', label: 'Scan GitHub', description: 'Discover repositories via GitHub search' },
  { to: '/admin/candidates', label: 'Candidates', description: 'Review & approve repo candidates' },
  { to: '/admin/repos', label: 'Approved Repos', description: 'View and manage approved repositories' },
]

export function AdminDashboardPage() {
  const navigate = useNavigate()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Admin Dashboard</h1>
      <div className="grid gap-4 sm:grid-cols-2">
        {links.map((l) => (
          <Link
            key={l.to}
            to={l.to}
            className="rounded-lg border bg-white p-6 shadow-sm transition hover:shadow-md"
          >
            <h2 className="text-lg font-semibold text-blue-600">{l.label}</h2>
            <p className="mt-1 text-sm text-gray-600">{l.description}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
