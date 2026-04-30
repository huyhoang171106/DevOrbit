import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { apiStudentGet } from '../../lib/api'
import { getStudentToken } from '../../lib/auth'
import type { StudentBookmark } from '../../types/api'

export function StudentBookmarksPage() {
  const token = getStudentToken()
  const [bookmarks, setBookmarks] = useState<StudentBookmark[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!token) return
    apiStudentGet<StudentBookmark[]>('/api/student/bookmarks', token)
      .then(setBookmarks)
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [token])

  if (!token) return <Navigate to="/student/login" replace />
  if (loading) return <div className="py-20 text-center text-sm text-slate-500 animate-pulse">Loading bookmarks...</div>

  return (
    <div>
      <div className="mb-8">
        <p className="mb-2 text-xs font-semibold uppercase tracking-[0.3em] text-cyan-300">My Learning</p>
        <h1 className="page-title">Bookmarks</h1>
        <p className="mt-2 max-w-2xl text-sm text-slate-400">Your saved Phase-2 learning items. Public browsing remains available without login.</p>
      </div>
      {bookmarks.length === 0 ? (
        <div className="glass-card p-8 text-center text-slate-400">
          No bookmarks yet. Save a course or repository from the catalog.
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {bookmarks.map((bookmark) => (
            <a
              key={bookmark.id}
              href={bookmark.url}
              className="glass-card block p-5 transition-transform hover:-translate-y-1"
              target={bookmark.targetType === 'REPO' ? '_blank' : undefined}
              rel={bookmark.targetType === 'REPO' ? 'noreferrer' : undefined}
            >
              <span className="rounded-full bg-cyan-400/10 px-3 py-1 text-xs font-semibold text-cyan-300">{bookmark.targetType}</span>
              <h2 className="mt-4 font-heading text-lg font-semibold text-slate-100">{bookmark.title}</h2>
              {bookmark.subtitle && <p className="mt-1 text-sm text-slate-500">{bookmark.subtitle}</p>}
            </a>
          ))}
        </div>
      )}
      <Link to="/courses" className="mt-8 inline-block text-sm text-slate-400 hover:text-amber-400">Back to Courses</Link>
    </div>
  )
}
