import { useState, useEffect } from 'react'
import type { RoadmapRequest } from '../../../types/api'

interface RoadmapDialogProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: RoadmapRequest) => void
  initial?: Partial<RoadmapRequest> | null
  loading?: boolean
}

export function RoadmapDialog({ open, onClose, onSubmit, initial, loading }: RoadmapDialogProps) {
  const [form, setForm] = useState<RoadmapRequest>({
    studentId: 0,
    title: '',
    description: '',
    isPublic: false,
  })

  useEffect(() => {
    if (initial) {
      setForm({
        studentId: initial.studentId ?? 0,
        title: initial.title ?? '',
        description: initial.description ?? '',
        isPublic: initial.isPublic ?? false,
      })
    } else {
      setForm({ studentId: 0, title: '', description: '', isPublic: false })
    }
  }, [initial, open])

  if (!open) return null

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">{initial ? 'Sửa lộ trình' : 'Tạo lộ trình'}</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Tiêu đề *</label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))}
              className="input-field"
              required
            />
          </div>
          <div>
            <label className="label">Mô tả</label>
            <textarea
              value={form.description ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
              className="input-field"
              rows={3}
            />
          </div>
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="isPublic"
              checked={form.isPublic ?? false}
              onChange={(e) => setForm((prev) => ({ ...prev, isPublic: e.target.checked }))}
              className="rounded"
            />
            <label htmlFor="isPublic" className="text-sm text-ink-secondary">Công khai</label>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost text-sm" disabled={loading}>Huỷ</button>
            <button type="submit" className="btn-primary text-sm" disabled={loading}>
              {loading ? 'Đang lưu...' : initial ? 'Cập nhật' : 'Tạo'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
