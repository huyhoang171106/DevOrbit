import { useState } from 'react'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'
import { RepoEditDialog } from './RepoEditDialog'
import { getAdminToken } from '../../../lib/auth'
import type { RepoSummary } from '../../../types/api'
import type { ApprovedRepoUpdateRequest } from '../../../types/admin'

export function RepoApprovedTab() {
  const token = getAdminToken()
  const [editingRepo, setEditingRepo] = useState<RepoSummary | null>(null)
  const [editDialogOpen, setEditDialogOpen] = useState(false)

  const { data: repos, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getApprovedRepos(t),
    [],
  )

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
  )

  const handleEdit = (repo: RepoSummary) => {
    setEditingRepo(repo)
    setEditDialogOpen(true)
  }

  const handleSave = async (id: number, data: ApprovedRepoUpdateRequest) => {
    await adminApi.updateApprovedRepo(token!, id, data)
    refetch()
  }

  const handleDeactivate = async (id: number) => {
    if (!token || !confirm('Vô hiệu hoá repo này?')) return
    try {
      await adminApi.updateApprovedRepo(token, id, { active: false })
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  if (loading) return <AdminSpinner text="Đang tải approved repos..." />
  if (error) return <AdminErrorBanner message={error} onRetry={refetch} />

  return (
    <div>
      <div className="glass-card overflow-hidden border border-orbit-border">
        <table className="w-full">
          <thead>
            <tr className="border-b border-orbit-border bg-orbit-surface/50">
              <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Repo</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Môn học</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Ngôn ngữ</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Stars</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-clay-border">
            {(!repos || repos.length === 0) && (
              <tr>
                <td colSpan={5} className="px-4 py-10 text-center body-sm text-ink-secondary">Không có approved repos</td>
              </tr>
            )}
            {repos?.map((repo) => (
              <tr key={repo.id} className="transition-colors hover:bg-orbit-surface/30">
                <td className="px-4 py-3 text-sm">
                  <span className="font-medium text-ink-primary">{repo.displayName}</span>
                </td>
                <td className="px-4 py-3 text-sm text-ink-secondary">{repo.courseName ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-ink-secondary">{repo.primaryLanguage ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-ink-secondary">{repo.stars ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-right">
                  <div className="flex justify-end gap-2">
                    <button onClick={() => handleEdit(repo)} className="btn-ghost text-xs">Sửa</button>
                    <button onClick={() => handleDeactivate(repo.id)} className="btn-ghost text-xs text-red-400">Vô hiệu</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <RepoEditDialog
        open={editDialogOpen}
        repo={editingRepo}
        courses={courses ?? []}
        onClose={() => { setEditDialogOpen(false); setEditingRepo(null) }}
        onSave={handleSave}
      />
    </div>
  )
}
