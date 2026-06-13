import { useState } from 'react'
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

export function ReviewsPage() {
  const token = getAdminToken()
  const [activeTab, setActiveTab] = useState<'course' | 'repo'>('course')

  const { data: courseReviews, loading: courseLoading, error: courseError, refetch: refetchCourse } = useAdminFetch(
    (t) => adminApi.getCourseReviews(t),
    [],
  )

  const { data: repoReviews, loading: repoLoading, error: repoError, refetch: refetchRepo } = useAdminFetch(
    (t) => adminApi.getRepoReviews(t),
    [],
  )

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
            className={`px-4 py-2 text-sm rounded-lg transition-colors ${
              activeTab === tab.key
                ? 'bg-orbit-accent/15 text-orbit-accent'
                : 'text-ink-secondary hover:text-ink-primary'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'course' && (
        <>
          {courseLoading && <AdminSpinner text="Đang tải đánh giá môn học..." />}
          {courseError && <AdminErrorBanner message={courseError} onRetry={refetchCourse} />}
          {deleteError && <AdminErrorBanner message={deleteError} onRetry={() => setDeleteError(null)} />}
          {courseReviews && <CourseReviewTable reviews={courseReviews} onDelete={handleDeleteCourseReview} />}
        </>
      )}

      {activeTab === 'repo' && (
        <>
          {repoLoading && <AdminSpinner text="Đang tải đánh giá repo..." />}
          {repoError && <AdminErrorBanner message={repoError} onRetry={refetchRepo} />}
          {repoReviews && <RepoReviewTable reviews={repoReviews} onDelete={handleDeleteRepoReview} />}
        </>
      )}
    </AdminPageLayout>
  )
}
