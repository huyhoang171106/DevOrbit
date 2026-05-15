import { useState, useEffect } from 'react'
import type { PhaseRequest } from '../../types/api'

type Props = {
  open: boolean
  onClose: () => void
  onSubmit: (data: PhaseRequest) => void
  initial?: PhaseRequest
}

const empty: PhaseRequest = { title: '', description: '', sortOrder: 0 }

export function PhaseDialog({ open, onClose, onSubmit, initial }: Props) {
  const [form, setForm] = useState<PhaseRequest>(initial ?? empty)

  useEffect(() => { setForm(initial ?? empty) }, [initial, open])

  if (!open) return null

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const { name, value } = e.target
    setForm((p) => ({ ...p, [name]: name === 'sortOrder' ? Number(value) : value }))
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold font-heading text-clay-text">
            {initial ? 'Sửa giai đoạn' : 'Thêm giai đoạn'}
          </h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-clay-text-muted hover:bg-glass-surface-hover hover:text-clay-text transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="label">Tiêu đề *</label>
            <input name="title" value={form.title} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">Mô tả</label>
            <textarea name="description" value={form.description ?? ''} onChange={handleChange} rows={2} className="input-field" />
          </div>
          <div>
            <label className="label">Thứ tự</label>
            <input name="sortOrder" type="number" value={form.sortOrder ?? 0} onChange={handleChange} className="input-field" />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost">Hủy</button>
            <button type="submit" className="btn-primary">{initial ? 'Cập nhật' : 'Thêm'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}
