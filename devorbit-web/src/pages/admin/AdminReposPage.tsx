import { useState, useEffect } from 'react'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { apiAdminGet, apiAdminDelete, apiAdminPut } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { ApprovedRepoTable } from '../../components/admin/ApprovedRepoTable'
import type { RepoSummary, CourseSummary } from '../../types/api'

export function AdminReposPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [editingRepo, setEditingRepo] = useState<RepoSummary | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: repos, loading, error: fetchError, refetch } = useApiFetch(
    () => apiAdminGet<RepoSummary[]>('/api/admin/repos', token),
    [token],
  )

  const [allCourses, setAllCourses] = useState<CourseSummary[]>([])

  useEffect(() => {
    apiAdminGet<CourseSummary[]>('/api/admin/courses', token)
      .then(setAllCourses)
      .catch(() => { })
  }, [token])

  async function handleDeactivate(id: number) {
    if (!confirm('Hủy kích hoạt repo này?')) return
    try {
      await apiAdminDelete(`/api/admin/repos/${id}`, token)
      refetch()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleSave(repo: RepoSummary, form: HTMLFormElement) {
    setError(null)
    const data = new FormData(form)
    const githubUrl = String(data.get('githubUrl') ?? '').trim()
    if (!githubUrl.startsWith('https://github.com/')) {
      setError('URL GitHub phải bắt đầu bằng https://github.com/')
      return
    }
    try {
      await apiAdminPut(`/api/admin/repos/${repo.id}`, token, {
        displayName: String(data.get('displayName') ?? '').trim(),
        description: String(data.get('description') ?? '').trim(),
        githubUrl,
        primaryLanguage: String(data.get('primaryLanguage') ?? '').trim(),
        stars: Number(data.get('stars') ?? 0),
        active: data.get('active') === 'on',
        techStacks: String(data.get('techStacks') ?? '').split(',').map((s) => s.trim()).filter(Boolean),
        courseId: data.get('courseId') ? Number(data.get('courseId')) : null,
      })
      setEditingRepo(null)
      refetch()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Cập nhật kho thất bại')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-ink-secondary">
          <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Đang tải danh sách repository...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <h1 className="display-sm text-clay-text mb-1">Repository đã duyệt</h1>
        <p className="body-sm text-ink-secondary">Xem và quản lý các repository GitHub đã được phê duyệt.</p>
      </div>

      {fetchError && (
        <div className="mb-6 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 px-5 py-4 body-sm text-red-400">
          <svg className="h-5 w-5 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 8v4M12 16h.01" />
          </svg>
          <span className="flex-1">{fetchError}</span>
          <button onClick={refetch} className="btn-secondary !py-1 !px-3 !text-xs flex-shrink-0">
            Thử lại
          </button>
        </div>
      )}

      <ApprovedRepoTable repos={repos ?? []} onEdit={setEditingRepo} onDeactivate={handleDeactivate} />

      {editingRepo && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-clay-bg/80 p-4 backdrop-blur-sm">
          <form
            className="w-full max-w-2xl space-y-4 border border-clay-border bg-clay-surface p-8"
            onSubmit={(e) => {
              e.preventDefault()
              void handleSave(editingRepo, e.currentTarget)
            }}
          >
            <div className="flex items-center justify-between mb-4">
              <h2 className="heading-4 text-clay-text">Sửa Repository</h2>
              <button
                type="button"
                onClick={() => setEditingRepo(null)}
                className="rounded-lg p-1 text-ink-secondary hover:bg-glass-surface-raised hover:text-clay-text transition-colors"
              >
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            {error && (
              <div className="flex items-center gap-2 rounded-md border border-red-500/30 bg-red-500/10 px-4 py-2.5 body-sm text-red-400">
                <svg className="h-4 w-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 8v4M12 16h.01" />
                </svg>
                {error}
              </div>
            )}

            <div>
              <label className="label mb-1.5 block">Tên hiển thị</label>
              <input name="displayName" defaultValue={editingRepo.displayName} required className="input-field" placeholder="Tên hiển thị" />
            </div>

            <div>
              <label className="label mb-1.5 block">Mô tả</label>
              <textarea name="description" defaultValue={editingRepo.description ?? ''} rows={3} className="input-field" placeholder="Mô tả" />
            </div>

            <div>
              <label className="label mb-1.5 block">GitHub URL</label>
              <input name="githubUrl" defaultValue={editingRepo.githubUrl} required className="input-field" placeholder="GitHub URL" />
            </div>

            <div className="grid gap-[16px] md:grid-cols-2">
              <div>
                <label className="label mb-1.5 block">Ngôn ngữ chính</label>
                <input name="primaryLanguage" defaultValue={editingRepo.primaryLanguage ?? ''} className="input-field" placeholder="Ngôn ngữ chính" />
              </div>
              <div>
                <label className="label mb-1.5 block">Stars</label>
                <input name="stars" type="number" min="0" defaultValue={editingRepo.stars ?? 0} className="input-field" placeholder="Sao" />
              </div>
            </div>

            <div>
              <label className="label mb-1.5 block">Tech Stacks (phân tách bằng dấu phẩy)</label>
              <input name="techStacks" defaultValue={editingRepo.techStacks.join(', ')} className="input-field" placeholder="React, Spring Boot" />
            </div>

            <div>
              <label className="label mb-1.5 block">Môn học</label>
              <select name="courseId" defaultValue={editingRepo.courseId ?? ''} className="input-field">
                <option value="">Không có (không thuộc môn học nào)</option>
                {allCourses.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
                ))}
              </select>
            </div>

            <label className="flex items-center gap-2.5 body-sm text-ink-secondary cursor-pointer mt-4">
              <input name="active" type="checkbox" defaultChecked className="h-4 w-4 rounded border-glass-border text-emerald-400 focus:ring-emerald-500/20" />
              Hoạt động
            </label>

            <div className="flex justify-end gap-3 pt-4 mt-4 border-t border-glass-border">
              <button type="button" onClick={() => setEditingRepo(null)} className="btn-secondary !bg-glass-surface">
                Hủy
              </button>
              <button type="submit" className="btn-primary">
                Lưu thay đổi
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  )
}
