import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { m as motion } from 'framer-motion'
import { BookOpen, GithubLogo, BookmarkSimple, Trash, ArrowSquareOut } from '@phosphor-icons/react'
import { apiStudentGet, apiStudentDelete } from '../../lib/api'
import { isStudentAuthenticated } from '../../lib/auth'
import { Avatar } from '../../components/shared/Avatar'
import type { StudentProfileResponse, StudentBookmark } from '../../types/api'

export function StudentProfilePage() {
  const [profile, setProfile] = useState<StudentProfileResponse | null>(null)
  const [bookmarks, setBookmarks] = useState<StudentBookmark[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!isStudentAuthenticated()) return
    Promise.all([
      apiStudentGet<StudentProfileResponse>('/api/student/me'),
      apiStudentGet<StudentBookmark[]>('/api/student/bookmarks'),
    ])
      .then(([profileData, bookmarksData]) => {
        setProfile(profileData)
        setBookmarks(bookmarksData)
      })
      .finally(() => setLoading(false))
  }, [])

  async function handleRemove(bookmarkId: number) {
    try {
      await apiStudentDelete(`/api/student/bookmarks/${bookmarkId}`)
      setBookmarks((prev) => prev.filter((b) => b.id !== bookmarkId))
    } catch {
      // silently fail
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 text-[14px] font-medium text-orbit-text-secondary">
          <svg className="h-5 w-5 animate-spin text-orbit-accent" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Đang tải...
        </div>
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <Link to="/student/login" className="btn-primary">Đăng nhập</Link>
      </div>
    )
  }

  return (
    <div className="w-full">
      <div className="mx-auto max-w-[1200px] px-6 md:px-10 py-12 md:py-20">

        {/* Profile Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex flex-col md:flex-row items-center md:items-start gap-6 md:gap-10 pb-10 border-b border-orbit-border mb-10"
        >
          <Avatar name={profile.fullName} size={128} className="ring-4 ring-orbit-accent/20 rounded-full" />
          <div className="text-center md:text-left">
            <h1 className="font-heading text-3xl md:text-4xl font-black text-orbit-text tracking-tight">
              {profile.fullName}
            </h1>
            <p className="text-[15px] text-orbit-accent font-mono font-medium mt-1">
              @{profile.studentCode}
            </p>
            <p className="text-[14px] text-orbit-text-secondary mt-2 max-w-lg">
              {profile.email}
            </p>
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-3 mt-5">
              <div className="flex items-center gap-2 text-[13px] text-orbit-text-secondary">
                <BookmarkSimple className="h-4 w-4" weight="fill" />
                <span className="font-medium text-orbit-text">{bookmarks.length}</span>
                <span>bookmarks</span>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Bookmarks Section — GitHub-style repo cards */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="font-heading text-xl font-bold text-orbit-text tracking-tight">
              Bookmarks
            </h2>
            <span className="text-[13px] text-orbit-text-muted font-mono">
              {bookmarks.length} {bookmarks.length === 1 ? 'mục' : 'mục'}
            </span>
          </div>

          {bookmarks.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-orbit-border p-16 text-center">
              <BookmarkSimple className="h-12 w-12 text-orbit-text-muted mx-auto mb-4" weight="light" />
              <p className="text-[15px] font-medium text-orbit-text-secondary">
                Chưa có bookmark nào
              </p>
              <p className="text-[13px] text-orbit-text-muted mt-1">
                Lưu các khóa học và repository để theo dõi sau.
              </p>
              <Link to="/courses" className="btn-primary text-[12px] px-6 py-3 mt-6 inline-flex">
                Duyệt khóa học
              </Link>
            </div>
          ) : (
            <div className="grid gap-4">
              {bookmarks.map((bookmark, i) => (
                <motion.div
                  key={bookmark.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.04 }}
                  className="group relative rounded-2xl border border-orbit-border bg-orbit-surface/50 p-5 hover:border-orbit-accent/30 hover:bg-orbit-surface transition-[border-color,background-color] duration-200"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2.5 mb-2">
                        {bookmark.targetType === 'REPO' ? (
                          <GithubLogo className="h-4 w-4 text-orbit-text-muted flex-shrink-0" weight="fill" />
                        ) : (
                          <BookOpen className="h-4 w-4 text-orbit-text-muted flex-shrink-0" weight="fill" />
                        )}
                        <a
                          href={bookmark.url}
                          target={bookmark.targetType === 'REPO' ? '_blank' : undefined}
                          rel={bookmark.targetType === 'REPO' ? 'noreferrer' : undefined}
                          className="text-[15px] font-bold text-orbit-accent hover:text-orbit-accent/80 hover:underline transition-colors truncate"
                        >
                          {bookmark.title}
                        </a>
                        <ArrowSquareOut className="h-3 w-3 text-orbit-text-muted flex-shrink-0" weight="bold" />
                      </div>
                      {bookmark.subtitle && (
                        <p className="text-[13px] text-orbit-text-secondary leading-relaxed line-clamp-2 mb-3">
                          {bookmark.subtitle}
                        </p>
                      )}
                      <div className="flex items-center gap-3">
                        <span className={`inline-flex items-center gap-1.5 text-[11px] font-semibold uppercase tracking-wider px-2.5 py-1 rounded-full ${
                          bookmark.targetType === 'REPO'
                            ? 'bg-violet-500/10 text-violet-400 border border-violet-500/20'
                            : 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                        }`}>
                          <span className={`h-2 w-2 rounded-full ${
                            bookmark.targetType === 'REPO' ? 'bg-violet-400' : 'bg-emerald-400'
                          }`} />
                          {bookmark.targetType}
                        </span>
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleRemove(bookmark.id)}
                      className="flex-shrink-0 p-2 rounded-lg text-orbit-text-muted hover:text-red-400 hover:bg-red-500/10 opacity-0 group-hover:opacity-100 transition-all duration-200 cursor-pointer"
                      title="Xóa bookmark"
                    >
                      <Trash className="h-4 w-4" weight="regular" />
                    </button>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
