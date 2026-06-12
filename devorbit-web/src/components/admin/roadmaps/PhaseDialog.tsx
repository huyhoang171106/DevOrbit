import { useState, useEffect } from 'react'
import type { PhaseRequest } from '../../../types/api'

interface PhaseDialogProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: PhaseRequest) => void
  initial?: Partial<PhaseRequest> | null
  loading?: boolean
}

export function PhaseDialog({ open, onClose, onSubmit, initial, loading }: PhaseDialogProps) {
  const [form, setForm] = useState<PhaseRequest>({
    title: '',
    description: '',
    sortOrder: 0,
  })

  useEffect(() => {
    if (initial) {
      setForm({
        title: initial.title ?? '',
        description: initial.description ?? '',
        sortOrder: initial.sortOrder ?? 0,
      })
    } else {
      setForm({ title: '', description: '', sortOrder: 0 })
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
          <h2 className="heading-5 text-ink-primary">{initial ? 'Sửa giai đoạn' : 'Tạo giai đoạn'}</h2>
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
              rows={2}
            />
          </div>
          <div>
            <label className="label">Thứ tự</label>
            <input
              type="number"
              value={form.sortOrder ?? 0}
              onChange={(e) => setForm((prev) => ({ ...prev, sortOrder: Number(e.target.value) }))}
              className="input-field"
              min={0}
            />
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
