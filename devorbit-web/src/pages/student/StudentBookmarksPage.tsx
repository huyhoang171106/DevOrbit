import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import type { StudentBookmark } from '../../types/api'

export function StudentBookmarksPage() {
  const navigate = useNavigate()
  const [bookmarks, setBookmarks] = useState<StudentBookmark[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiGet<StudentBookmark[]>('/api/student/bookmarks')
      .then(setBookmarks)
      .catch(() => navigate('/courses'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-ink-secondary">
          <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Đang tải đánh dấu...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <p className="body-sm text-emerald-400 mb-[8px] font-medium tracking-wide uppercase">Lộ trình học tập</p>
        <h1 className="display-sm text-clay-text">Đã đánh dấu</h1>
        <p className="mt-[8px] max-w-2xl body-sm text-ink-secondary">
          Các mục học tập bạn đã lưu. Bạn vẫn có thể duyệt công khai mà không cần đăng nhập.
        </p>
      </div>

      {bookmarks.length === 0 ? (
        <div className="glass-card p-[48px] text-center">
          <svg className="mx-auto mb-[16px] h-[48px] w-[48px] text-ink-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
          </svg>
          <p className="body-md font-medium text-clay-text">Chưa có đánh dấu nào.</p>
          <p className="mt-1 body-sm text-ink-secondary">Lưu một khóa học hoặc repository từ danh mục.</p>
          <Link to="/courses" className="btn-secondary mt-[24px] inline-flex">
            Duyệt khóa học
          </Link>
        </div>
      ) : (
        <div className="grid gap-[16px] sm:grid-cols-2">
          {bookmarks.map((bookmark) => (
            <a
              key={bookmark.id}
              href={bookmark.url}
              className="glass-card group block p-[24px] cursor-pointer"
              target={bookmark.targetType === 'REPO' ? '_blank' : undefined}
              rel={bookmark.targetType === 'REPO' ? 'noreferrer' : undefined}
            >
              <span className="badge-tag uppercase tracking-wider font-semibold bg-emerald-500/10 text-emerald-400 border-emerald-500/20">
                {bookmark.targetType}
              </span>
              <h2 className="mt-[16px] heading-4 text-clay-text group-hover:text-emerald-400 transition-colors">
                {bookmark.title}
              </h2>
              {bookmark.subtitle && (
                <p className="mt-[4px] body-sm text-ink-secondary">{bookmark.subtitle}</p>
              )}
              <div className="mt-[16px] flex items-center gap-[4px] code-sm text-ink-secondary group-hover:text-emerald-400 transition-colors font-medium">
                <span>Truy cập</span>
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
        className="mt-[32px] inline-flex items-center gap-[6px] rounded-[6px] px-3 py-1.5 body-sm font-medium text-ink-secondary transition-colors hover:bg-glass-surface-raised hover:text-clay-text"
      >
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Quay lại khóa học
      </Link>
    </div>
  )
}
