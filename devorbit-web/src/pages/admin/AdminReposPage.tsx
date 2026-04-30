import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { apiAdminGet, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { ApprovedRepoTable } from '../../components/admin/ApprovedRepoTable'
import type { RepoSummary } from '../../types/api'

export function AdminReposPage() {
  useRequireAuth()
  const token = getAdminToken()!

  const { data: repos, loading, refetch } = useApiFetch(
    () => apiAdminGet<RepoSummary[]>('/api/admin/repos', token),
    [token],
  )

  async function handleDeactivate(id: number) {
    if (!confirm('Deactivate this repo?')) return
    try {
      await apiAdminDelete(`/api/admin/repos/${id}`, token)
      refetch()
    } catch (err) {
      console.error(err)
    }
  }

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading...</div>
    </div>
  )

  return (
    <div>
      <h1 className="page-title mb-6">Approved Repositories</h1>
      <ApprovedRepoTable repos={repos ?? []} onDeactivate={handleDeactivate} />
    </div>
  )
}
