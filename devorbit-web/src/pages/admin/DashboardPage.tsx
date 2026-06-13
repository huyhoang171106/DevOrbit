import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { StatsRow } from '../../components/admin/dashboard/StatsRow'
import { RecentActivity } from '../../components/admin/dashboard/RecentActivity'
import { QuickActions } from '../../components/admin/dashboard/QuickActions'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'

export function DashboardPage() {
  const { data, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getStats(t),
    [],
  )

  return (
    <AdminPageLayout title="Bảng điều khiển" description="Tổng quan nền tảng DevOrbit">
      {loading && <AdminSpinner text="Đang tải thống kê..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {data && (
        <div className="space-y-6">
          <StatsRow stats={data} />
          <RecentActivity stats={data} />
          <QuickActions />
        </div>
      )}
    </AdminPageLayout>
  )
}
