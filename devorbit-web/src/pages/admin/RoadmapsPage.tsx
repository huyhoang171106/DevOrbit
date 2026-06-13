import { useState } from 'react'
import { MapTrifold } from '@phosphor-icons/react'
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
  const [createError, setCreateError] = useState<string | null>(null)
  const { data: roadmaps, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getRoadmaps(t),
    [],
  )

  const handleCreate = async (data: RoadmapRequest) => {
    setCreateError(null)
    try {
      await adminApi.createRoadmap(token!, data)
      setCreateDialogOpen(false)
      refetch()
    } catch (e) {
      setCreateError(e instanceof Error ? e.message : 'Tạo lộ trình thất bại')
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
      {createError && <AdminErrorBanner message={createError} onRetry={() => setCreateError(null)} />}
      {roadmaps && roadmaps.length === 0 && (
        <div className="orbit-card p-8 text-center max-w-xl mx-auto">
          <div className="h-12 w-12 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mx-auto mb-4">
            <MapTrifold className="h-6 w-6 text-orbit-accent" weight="duotone" />
          </div>
          <h3 className="font-heading font-bold text-lg text-orbit-text mb-2">Chưa có lộ trình</h3>
          <p className="text-sm text-ink-secondary mb-6 max-w-md mx-auto leading-relaxed">
            Lộ trình học tập là cấu trúc 3 tầng: <strong>Lộ trình</strong> → <strong>Giai đoạn</strong> → <strong>Mục</strong> (môn học hoặc repo).<br />
            Bấm "<strong>+ Tạo lộ trình</strong>" để bắt đầu, sau đó thêm giai đoạn và mục vào bên trong.
          </p>
          <p className="text-xs text-ink-muted">
            Lộ trình cũng có thể được AI Tutor bên sinh viên tự động sinh ra dựa trên nhu cầu học tập.
          </p>
        </div>
      )}
      {roadmaps && roadmaps.length > 0 && <RoadmapTree roadmaps={roadmaps} onRefetch={refetch} />}

      <RoadmapDialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        onSubmit={handleCreate}
      />
    </AdminPageLayout>
  )
}
