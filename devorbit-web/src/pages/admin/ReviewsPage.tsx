import { useState, useMemo, useEffect, useRef } from 'react'
import { useLocation } from 'react-router-dom'
import { CaretDown, Check } from '@phosphor-icons/react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { CourseReviewTable } from '../../components/admin/reviews/CourseReviewTable'
import { RepoReviewTable } from '../../components/admin/reviews/RepoReviewTable'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'

const TABS = [
  { key: 'course', label: 'Đánh giá môn học' },
  { key: 'repo', label: 'Đánh giá Repo' },
] as const

const SORT_OPTIONS = [
  { value: 'newest', label: 'Mới nhất' },
  { value: 'oldest', label: 'Cũ nhất' },
  { value: 'student', label: 'Tên sinh viên' },
  { value: 'course', label: 'Tên môn học' },
] as const

export function ReviewsPage() {
  const token = getAdminToken()
  const [activeTab, setActiveTab] = useState<'course' | 'repo'>('course')
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'student' | 'course'>('newest')
  const [search, setSearch] = useState('')
  const location = useLocation()
  const [sortOpen, setSortOpen] = useState(false)
  const sortRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const tab = new URLSearchParams(location.search).get('tab')
    if (tab === 'repo' || tab === 'course') setActiveTab(tab)
  }, [location.search])

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (sortRef.current && !sortRef.current.contains(e.target as Node)) {
        setSortOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  const { data: courseReviews, loading: courseLoading, error: courseError, refetch: refetchCourse } = useAdminFetch(
    (t) => adminApi.getCourseReviews(t),
    [],
  )

  const { data: repoReviews, loading: repoLoading, error: repoError, refetch: refetchRepo } = useAdminFetch(
    (t) => adminApi.getRepoReviews(t),
    [],
  )

  const sortedCourseReviews = useMemo(() => {
    if (!courseReviews) return null
    const q = search.trim().toLowerCase()
    let filtered = q
      ? courseReviews.filter(
          (r) => r.studentName.toLowerCase().includes(q) || r.courseName.toLowerCase().includes(q) || (r.comment && r.comment.toLowerCase().includes(q))
        )
      : courseReviews
    const sorted = [...filtered]
    switch (sortBy) {
      case 'oldest':
        return sorted.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      case 'student':
        return sorted.sort((a, b) => a.studentName.localeCompare(b.studentName))
      case 'course':
        return sorted.sort((a, b) => a.courseName.localeCompare(b.courseName))
      default:
        return sorted.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    }
  }, [courseReviews, sortBy, search])

  const sortedRepoReviews = useMemo(() => {
    if (!repoReviews) return null
    const q = search.trim().toLowerCase()
    let filtered = q
      ? repoReviews.filter(
          (r) => r.studentName.toLowerCase().includes(q) || r.repoName.toLowerCase().includes(q) || (r.comment && r.comment.toLowerCase().includes(q))
        )
      : repoReviews
    const sorted = [...filtered]
    switch (sortBy) {
      case 'oldest':
        return sorted.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      case 'student':
        return sorted.sort((a, b) => a.studentName.localeCompare(b.studentName))
      case 'course':
        return sorted.sort((a, b) => a.repoName.localeCompare(b.repoName))
      default:
        return sorted.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    }
  }, [repoReviews, sortBy, search])

  const [deleteError, setDeleteError] = useState<string | null>(null)

  const handleDeleteCourseReview = async (id: number) => {
    if (!token || !confirm('Xoá đánh giá này?')) return
    setDeleteError(null)
    try {
      await adminApi.deleteCourseReview(token, id)
      refetchCourse()
    } catch (e) {
      setDeleteError(e instanceof Error ? e.message : 'Xoá thất bại')
    }
  }

  const handleDeleteRepoReview = async (id: number) => {
    if (!token || !confirm('Xoá đánh giá này?')) return
    setDeleteError(null)
    try {
      await adminApi.deleteRepoReview(token, id)
      refetchRepo()
    } catch (e) {
      setDeleteError(e instanceof Error ? e.message : 'Xoá thất bại')
    }
  }

  return (
    <AdminPageLayout title="Đánh giá" description="Duyệt đánh giá môn học và repository">
      <div className="flex gap-1 mb-6">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-4 py-2 text-sm rounded-2xl transition-colors ${
              activeTab === tab.key
                ? 'bg-orbit-accent/15 text-orbit-accent'
                : 'text-orbit-text-muted hover:text-orbit-text'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div className="flex items-center gap-4 mb-4 flex-wrap">
        <div className="flex items-center gap-2">
          <label className="text-[12px] font-bold uppercase tracking-wider text-orbit-text-muted">Sắp xếp:</label>
          <div ref={sortRef} className="relative">
            <button
              onClick={() => setSortOpen(!sortOpen)}
              className="flex items-center gap-2 bg-orbit-bg border border-orbit-border rounded-2xl px-3 py-2 text-[13px] text-orbit-text outline-none hover:border-orbit-accent/40 transition-colors"
            >
              {SORT_OPTIONS.find((o) => o.value === sortBy)?.label}
              <CaretDown className={`h-3.5 w-3.5 text-zinc-500 transition-transform duration-200 ${sortOpen ? 'rotate-180' : ''}`} weight="bold" />
            </button>
            {sortOpen && (
              <div className="absolute left-0 top-full mt-1 w-[180px] rounded-2xl border border-orbit-border/50 bg-orbit-surface shadow-diffusion overflow-hidden z-50">
                {SORT_OPTIONS.map((opt) => (
                  <button
                    key={opt.value}
                    onClick={() => { setSortBy(opt.value as typeof sortBy); setSortOpen(false) }}
                    className={`w-full flex items-center justify-between px-4 py-2.5 text-[13px] transition-colors ${
                      sortBy === opt.value
                        ? 'text-orbit-accent bg-orbit-accent/5'
                        : 'text-zinc-400 hover:text-zinc-200 hover:bg-orbit-surface/50'
                    }`}
                  >
                    {opt.value === 'course'
                      ? (activeTab === 'course' ? 'Tên môn học' : 'Tên repo')
                      : opt.label}
                    {sortBy === opt.value && <Check className="h-3.5 w-3.5" weight="bold" />}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm theo tên sinh viên, môn học, nội dung..."
          className="flex-1 min-w-[200px] bg-orbit-bg border border-orbit-border rounded-2xl px-3 py-2 text-[13px] text-orbit-text outline-none focus:border-orbit-accent/60 placeholder:text-orbit-text-muted"
        />
      </div>

      {activeTab === 'course' && (
        <>
          {courseLoading && <AdminSpinner text="Đang tải đánh giá môn học..." />}
          {courseError && <AdminErrorBanner message={courseError} onRetry={refetchCourse} />}
          {deleteError && <AdminErrorBanner message={deleteError} onRetry={() => setDeleteError(null)} />}
          {sortedCourseReviews && <CourseReviewTable reviews={sortedCourseReviews} onDelete={handleDeleteCourseReview} />}
        </>
      )}

      {activeTab === 'repo' && (
        <>
          {repoLoading && <AdminSpinner text="Đang tải đánh giá repo..." />}
          {repoError && <AdminErrorBanner message={repoError} onRetry={refetchRepo} />}
          {sortedRepoReviews && <RepoReviewTable reviews={sortedRepoReviews} onDelete={handleDeleteRepoReview} />}
        </>
      )}
    </AdminPageLayout>
  )
}
