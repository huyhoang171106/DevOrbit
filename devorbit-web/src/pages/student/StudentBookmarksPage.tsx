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

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-steel">
          <svg className="h-5 w-5 animate-spin text-brand-green" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading bookmarks...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <p className="body-sm text-brand-green mb-[8px] font-medium tracking-wide uppercase">My Learning</p>
        <h1 className="display-sm text-ink">Bookmarks</h1>
        <p className="mt-[8px] max-w-2xl body-sm text-steel">
          Your saved Phase-2 learning items. Public browsing remains available without login.
        </p>
      </div>

      {bookmarks.length === 0 ? (
        <div className="card-base p-[48px] text-center">
          <svg className="mx-auto mb-[16px] h-[48px] w-[48px] text-steel/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
          </svg>
          <p className="body-md font-medium text-ink">No bookmarks yet.</p>
          <p className="mt-1 body-sm text-steel">Save a course or repository from the catalog.</p>
          <Link to="/courses" className="btn-secondary mt-[24px] inline-flex">
            Browse Courses
          </Link>
        </div>
      ) : (
        <div className="grid gap-[16px] sm:grid-cols-2">
          {bookmarks.map((bookmark) => (
            <a
              key={bookmark.id}
              href={bookmark.url}
              className="card-base group block p-[24px] cursor-pointer"
              target={bookmark.targetType === 'REPO' ? '_blank' : undefined}
              rel={bookmark.targetType === 'REPO' ? 'noreferrer' : undefined}
            >
              <span className="badge-tag uppercase tracking-wider font-semibold bg-brand-green/10 text-brand-green border-brand-green/20">
                {bookmark.targetType}
              </span>
              <h2 className="mt-[16px] heading-4 text-ink group-hover:text-brand-green transition-colors">
                {bookmark.title}
              </h2>
              {bookmark.subtitle && (
                <p className="mt-[4px] body-sm text-steel">{bookmark.subtitle}</p>
              )}
              <div className="mt-[16px] flex items-center gap-[4px] code-sm text-steel group-hover:text-brand-green transition-colors font-medium">
                <span>Visit</span>
                <svg className="h-[14px] w-[14px] transition-transform group-hover:translate-x-[2px]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
              </div>
            </a>
          ))}
        </div>
      )}

      <Link
        to="/courses"
        className="mt-[32px] inline-flex items-center gap-[6px] rounded-[6px] px-3 py-1.5 body-sm font-medium text-steel transition-colors hover:bg-surface-soft hover:text-ink"
      >
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Back to Courses
      </Link>
    </div>
  )
}
