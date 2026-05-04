import { useState } from 'react'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { apiAdminGet, apiAdminDelete, apiAdminPut } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { ApprovedRepoTable } from '../../components/admin/ApprovedRepoTable'
import type { RepoSummary } from '../../types/api'

export function AdminReposPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [editingRepo, setEditingRepo] = useState<RepoSummary | null>(null)
  const [error, setError] = useState<string | null>(null)

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

  async function handleSave(repo: RepoSummary, form: HTMLFormElement) {
    setError(null)
    const data = new FormData(form)
    const githubUrl = String(data.get('githubUrl') ?? '').trim()
    if (!githubUrl.startsWith('https://github.com/')) {
      setError('GitHub URL must start with https://github.com/')
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
      })
      setEditingRepo(null)
      refetch()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update repo')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-steel">
          <svg className="h-5 w-5 animate-spin text-brand-green" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading repositories...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <h1 className="display-sm text-ink mb-1">Approved Repositories</h1>
        <p className="body-sm text-steel">View and manage approved GitHub repositories.</p>
      </div>

      <ApprovedRepoTable repos={repos ?? []} onEdit={setEditingRepo} onDeactivate={handleDeactivate} />

      {editingRepo && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink/50 p-4 backdrop-blur-sm">
          <form
            className="w-full max-w-2xl space-y-[16px] rounded-[12px] border border-hairline-soft bg-canvas p-[32px] shadow-[0_24px_48px_-8px_rgba(0,0,0,0.12)]"
            onSubmit={(e) => {
              e.preventDefault()
              void handleSave(editingRepo, e.currentTarget)
            }}
          >
            <div className="flex items-center justify-between mb-4">
              <h2 className="heading-4 text-ink">Edit Repository</h2>
              <button
                type="button"
                onClick={() => setEditingRepo(null)}
                className="rounded-lg p-1 text-steel hover:bg-surface-soft hover:text-ink transition-colors"
              >
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            {error && (
              <div className="flex items-center gap-2 rounded-md border border-red-100 bg-red-50 px-4 py-2.5 body-sm text-red-600">
                <svg className="h-4 w-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 8v4M12 16h.01" />
                </svg>
                {error}
              </div>
            )}

            <div>
              <label className="label mb-1.5 block">Display Name</label>
              <input name="displayName" defaultValue={editingRepo.displayName} required className="input-field" placeholder="Display name" />
            </div>

            <div>
              <label className="label mb-1.5 block">Description</label>
              <textarea name="description" defaultValue={editingRepo.description ?? ''} rows={3} className="input-field" placeholder="Description" />
            </div>
            
            <div>
              <label className="label mb-1.5 block">GitHub URL</label>
              <input name="githubUrl" defaultValue={editingRepo.githubUrl} required className="input-field" placeholder="GitHub URL" />
            </div>

            <div className="grid gap-[16px] md:grid-cols-2">
              <div>
                <label className="label mb-1.5 block">Primary Language</label>
                <input name="primaryLanguage" defaultValue={editingRepo.primaryLanguage ?? ''} className="input-field" placeholder="Primary language" />
              </div>
              <div>
                <label className="label mb-1.5 block">Stars</label>
                <input name="stars" type="number" min="0" defaultValue={editingRepo.stars ?? 0} className="input-field" placeholder="Stars" />
              </div>
            </div>

            <div>
              <label className="label mb-1.5 block">Tech Stacks (comma separated)</label>
              <input name="techStacks" defaultValue={editingRepo.techStacks.join(', ')} className="input-field" placeholder="React, Spring Boot" />
            </div>

            <label className="flex items-center gap-2.5 body-sm text-steel cursor-pointer mt-4">
              <input name="active" type="checkbox" defaultChecked className="h-4 w-4 rounded border-hairline text-brand-green focus:ring-brand-green/20" />
              Active
            </label>

            <div className="flex justify-end gap-3 pt-4 mt-4 border-t border-hairline-soft">
              <button type="button" onClick={() => setEditingRepo(null)} className="btn-secondary !bg-surface">
                Cancel
              </button>
              <button type="submit" className="btn-primary">
                Save Changes
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  )
}
