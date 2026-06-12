import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { RepoPipelineTabs } from '../../components/admin/repos/RepoPipelineTabs'

export function ReposPage() {
  return (
    <AdminPageLayout title="Repos" description="Quét kho GitHub, duyệt ứng viên và quản lý repository">
      <RepoPipelineTabs />
    </AdminPageLayout>
  )
}
