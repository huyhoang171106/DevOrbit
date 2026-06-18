import { useState, useMemo } from 'react'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'
import { RepoEditDialog } from './RepoEditDialog'
import { getAdminToken } from '../../../lib/auth'
import type { RepoSummary } from '../../../types/api'
import type { ApprovedRepoUpdateRequest } from '../../../types/admin'

function formatDate(iso: string | null | undefined): string {
  if (!iso) return '-'
  const d = new Date(iso)
  if (isNaN(d.getTime())) return '-'
  const dd = String(d.getDate()).padStart(2, '0')
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const yyyy = d.getFullYear()
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  return `${dd}/${mm}/${yyyy} ${hh}:${mi}`
}

export function RepoApprovedTab() {
  const token = getAdminToken()
  const [editingRepo, setEditingRepo] = useState<RepoSummary | null>(null)
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)
  const [evaluating, setEvaluating] = useState(false)
  const [syncingId, setSyncingId] = useState<number | null>(null)

  const handleEvaluateAll = async () => {
    if (!token) return
    setEvaluating(true)
    setActionError(null)
    try {
      await adminApi.evaluateAllRepos(token)
      alert('Đã chạy đánh giá lại toàn bộ repository thành công!')
      refetch()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Đánh giá thất bại')
    } finally {
      setEvaluating(false)
    }
  }

  const handleSync = async (id: number) => {
    if (!token) return
    setSyncingId(id)
    setActionError(null)
    try {
      await adminApi.syncApprovedRepo(token, id)
      alert('Đã đồng bộ và đánh giá lại repo thành công!')
      refetch()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Đồng bộ thất bại')
    } finally {
      setSyncingId(null)
    }
  }

  const { data: repos, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getApprovedRepos(t),
    [],
  )

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
    'courses',
  )

  const [searchQuery, setSearchQuery] = useState('')
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'alpha-asc' | 'alpha-desc'>('newest')

  const filteredRepos = useMemo(() => {
    if (!repos) return []
    const q = searchQuery.toLowerCase().trim()
    let filtered = q ? repos.filter((r) => r.displayName.toLowerCase().includes(q)) : [...repos]
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'newest': return b.id - a.id
        case 'oldest': return a.id - b.id
        case 'alpha-asc': return (a.displayName || '').localeCompare(b.displayName || '')
        case 'alpha-desc': return (b.displayName || '').localeCompare(a.displayName || '')
      }
    })
    return filtered
  }, [repos, searchQuery, sortBy])

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
    setActionError(null)
    try {
      await adminApi.updateApprovedRepo(token, id, { active: false })
      refetch()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Vô hiệu hoá thất bại')
    }
  }

  if (loading) return <AdminSpinner text="Đang tải approved repos..." />
  if (error) return <AdminErrorBanner message={error} onRetry={refetch} />
  if (actionError) return <AdminErrorBanner message={actionError} onRetry={() => setActionError(null)} />

  return (
    <div>
      <div className="flex items-center justify-between gap-3 mb-4">
        <div className="flex items-center gap-3 flex-1">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="input-field flex-1"
            placeholder="Tìm theo tên repo..."
          />
          <select value={sortBy} onChange={(e) => setSortBy(e.target.value as typeof sortBy)} className="input-field w-auto">
            <option value="newest">Mới nhất</option>
            <option value="oldest">Cũ nhất</option>
            <option value="alpha-asc">A-Z</option>
            <option value="alpha-desc">Z-A</option>
          </select>
        </div>
        <button
          onClick={handleEvaluateAll}
          disabled={evaluating}
          className="btn-primary text-xs shrink-0 cursor-pointer"
        >
          {evaluating ? 'Đang đánh giá...' : 'Đánh giá lại toàn bộ'}
        </button>
      </div>

      <div className="glass-card overflow-hidden border border-orbit-border">
        <table className="w-full">
          <thead>
            <tr className="border-b border-orbit-border bg-orbit-surface/50">
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Repo</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Môn học</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Ngôn ngữ</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Stars</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Đã duyệt lúc</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-clay-border">
            {filteredRepos.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-10 text-center body-sm text-ink-secondary">
                  {searchQuery.trim() ? 'Không tìm thấy repo phù hợp' : 'Không có approved repos'}
                </td>
              </tr>
            )}
            {filteredRepos.map((repo) => (
              <tr key={repo.id} className="transition-colors hover:bg-orbit-surface/30">
                <td className="px-4 py-3 text-sm text-center">
                  <a href={repo.githubUrl} target="_blank" rel="noopener noreferrer" className="font-medium text-ink-primary hover:text-orbit-accent transition-colors">
                    {repo.displayName}
                  </a>
                </td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.courseName ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.primaryLanguage ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.stars ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary whitespace-nowrap">{formatDate(repo.approvedAt)}</td>
                <td className="px-4 py-3 text-sm text-center">
                  <div className="flex justify-center gap-2">
                    <button onClick={() => handleEdit(repo)} className="btn-ghost text-xs whitespace-nowrap">Sửa</button>
                    <button
                      onClick={() => handleSync(repo.id)}
                      disabled={syncingId === repo.id}
                      className="btn-ghost text-xs whitespace-nowrap text-orbit-accent"
                    >
                      {syncingId === repo.id ? 'Đang đồng bộ...' : 'Đồng bộ'}
                    </button>
                    <button onClick={() => handleDeactivate(repo.id)} className="btn-ghost text-xs whitespace-nowrap text-red-400">Vô hiệu</button>
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
