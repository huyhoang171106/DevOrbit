import { useState, useEffect } from 'react'
import type { RepoSummary, CourseSummary } from '../../../types/api'
import type { ApprovedRepoUpdateRequest } from '../../../types/admin'

interface RepoEditDialogProps {
  open: boolean
  repo: RepoSummary | null
  courses: CourseSummary[]
  onClose: () => void
  onSave: (id: number, data: ApprovedRepoUpdateRequest) => Promise<void>
}

export function RepoEditDialog({ open, repo, courses, onClose, onSave }: RepoEditDialogProps) {
  const [form, setForm] = useState<ApprovedRepoUpdateRequest>({})
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [techStackInput, setTechStackInput] = useState('')
  const techStacks = form.techStacks ?? []

  const addTechStack = () => {
    const trimmed = techStackInput.trim()
    if (trimmed && !techStacks.includes(trimmed)) {
      setForm((prev) => ({ ...prev, techStacks: [...(prev.techStacks ?? []), trimmed] }))
    }
    setTechStackInput('')
  }

  const removeTechStack = (stack: string) => {
    setForm((prev) => ({ ...prev, techStacks: (prev.techStacks ?? []).filter((s) => s !== stack) }))
  }

  useEffect(() => {
    if (repo) {
      setForm({
        displayName: repo.displayName,
        description: repo.description,
        githubUrl: repo.githubUrl,
        primaryLanguage: repo.primaryLanguage,
        courseId: repo.courseId,
        techStacks: repo.techStacks ?? [],
      })
    }
  }, [repo, open])

  if (!open || !repo) return null

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.githubUrl?.startsWith('https://github.com/')) {
      setError('GitHub URL phải bắt đầu bằng https://github.com/')
      return
    }
    setSaving(true)
    setError(null)
    try {
      await onSave(repo.id, form)
      onClose()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Lưu thất bại')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">Sửa Repo</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        {error && (
          <div className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-sm text-red-400">{error}</div>
        )}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Tên hiển thị</label>
            <input
              type="text"
              value={form.displayName ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, displayName: e.target.value }))}
              className="input-field"
            />
          </div>
          <div>
            <label className="label">GitHub URL *</label>
            <input
              type="url"
              value={form.githubUrl ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, githubUrl: e.target.value }))}
              className="input-field"
              required
            />
          </div>
          <div>
            <label className="label">Ngôn ngữ chính</label>
            <input
              type="text"
              value={form.primaryLanguage ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, primaryLanguage: e.target.value }))}
              className="input-field"
            />
          </div>
          <div>
            <label className="label">Tech Stacks</label>
            <div className="flex gap-2 mb-2">
              <input
                type="text"
                value={techStackInput}
                onChange={(e) => setTechStackInput(e.target.value)}
                className="input-field flex-1"
                placeholder="VD: React, Spring Boot"
                onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addTechStack() } }}
              />
              <button onClick={addTechStack} className="btn-primary text-xs px-3">Thêm</button>
            </div>
            {techStacks.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {techStacks.map((stack) => (
                  <span key={stack} className="inline-flex items-center gap-1 px-2 py-1 rounded bg-orbit-accent/10 text-xs text-orbit-accent">
                    {stack}
                    <button onClick={() => removeTechStack(stack)} className="hover:text-red-400">&times;</button>
                  </span>
                ))}
              </div>
            )}
          </div>
          <div>
            <label className="label">Môn học</label>
            <select
              value={form.courseId ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, courseId: e.target.value ? Number(e.target.value) : null }))}
              className="input-field"
            >
              <option value="">Không có</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Mô tả</label>
            <textarea
              value={form.description ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
              className="input-field"
              rows={2}
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost text-sm" disabled={saving}>Huỷ</button>
            <button type="submit" className="btn-primary text-sm" disabled={saving}>
              {saving ? 'Đang lưu...' : 'Lưu'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
