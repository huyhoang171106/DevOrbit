import { useState, useEffect } from 'react'
import type { RoadmapRequest } from '../../types/api'

type Props = {
  open: boolean
  onClose: () => void
  onSubmit: (data: RoadmapRequest) => void
  initial?: RoadmapRequest
  studentOptions?: { id: number; code: string; name: string }[]
}

const empty: RoadmapRequest = { studentId: 0, title: '', description: '', markdownContent: '', isPublic: false }

export function RoadmapDialog({ open, onClose, onSubmit, initial, studentOptions }: Props) {
  const [form, setForm] = useState<RoadmapRequest>(initial ?? empty)

  useEffect(() => { setForm(initial ?? empty) }, [initial, open])

  if (!open) return null

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
    const { name, value, type } = e.target
    setForm((p) => ({
      ...p,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : name === 'studentId' ? Number(value) : value,
    }))
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold font-heading text-slate-100">
            {initial ? 'Edit Roadmap' : 'Create Roadmap'}
          </h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-slate-500 hover:bg-white/5 hover:text-slate-300 transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          {studentOptions && (
            <div>
              <label className="label">Student *</label>
              <select name="studentId" value={form.studentId} onChange={handleChange} className="input-field" required>
                <option value={0}>Select student...</option>
                {studentOptions.map((s) => (
                  <option key={s.id} value={s.id}>{s.code} - {s.name}</option>
                ))}
              </select>
            </div>
          )}
          <div>
            <label className="label">Title *</label>
            <input name="title" value={form.title} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">Description</label>
            <textarea name="description" value={form.description ?? ''} onChange={handleChange} rows={2} className="input-field" />
          </div>
          <div>
            <label className="label">Markdown Content</label>
            <textarea name="markdownContent" value={form.markdownContent ?? ''} onChange={handleChange} rows={4} className="input-field font-mono text-xs" />
          </div>
          <label className="flex items-center gap-2 text-sm text-slate-300">
            <input type="checkbox" name="isPublic" checked={form.isPublic ?? false} onChange={handleChange} className="rounded border-white/10 bg-white/5 text-amber-400 focus:ring-amber-400" />
            Public
          </label>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost">Cancel</button>
            <button type="submit" className="btn-primary">{initial ? 'Update' : 'Create'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}
