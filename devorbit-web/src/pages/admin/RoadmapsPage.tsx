import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { RoadmapTree } from '../../components/admin/roadmaps/RoadmapTree'
import { RoadmapDialog } from '../../components/admin/roadmaps/RoadmapDialog'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { RoadmapRequest } from '../../types/api'

export function RoadmapsPage() {
  const token = getAdminToken()
  const [createDialogOpen, setCreateDialogOpen] = useState(false)
  const { data: roadmaps, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getRoadmaps(t),
    [],
  )

  const handleCreate = async (data: RoadmapRequest) => {
    try {
      await adminApi.createRoadmap(token!, data)
      setCreateDialogOpen(false)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout
      title="Lộ trình học tập"
      description="Quản lý lộ trình, giai đoạn và mục học tập"
      action={
        <button onClick={() => setCreateDialogOpen(true)} className="btn-primary self-start">+ Tạo lộ trình</button>
      }
    >
      {loading && <AdminSpinner text="Đang tải lộ trình..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {roadmaps && <RoadmapTree roadmaps={roadmaps} onRefetch={refetch} />}

      <RoadmapDialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        onSubmit={handleCreate}
      />
    </AdminPageLayout>
  )
}
