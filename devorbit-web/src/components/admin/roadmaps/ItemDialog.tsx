import { useState, useEffect } from 'react'
import type { ItemRequest, RoadmapItemTargetType } from '../../../types/api'

interface ItemDialogProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: ItemRequest) => void
  initial?: Partial<ItemRequest> | null
  loading?: boolean
}

export function ItemDialog({ open, onClose, onSubmit, initial, loading }: ItemDialogProps) {
  const [form, setForm] = useState<ItemRequest>({
    targetType: 'COURSE' as RoadmapItemTargetType,
    targetId: 0,
    title: '',
    note: '',
    sortOrder: 0,
  })

  useEffect(() => {
    if (initial) {
      setForm({
        targetType: initial.targetType ?? 'COURSE',
        targetId: initial.targetId ?? 0,
        title: initial.title ?? '',
        note: initial.note ?? '',
        sortOrder: initial.sortOrder ?? 0,
      })
    } else {
      setForm({
        targetType: 'COURSE',
        targetId: 0,
        title: '',
        note: '',
        sortOrder: 0,
      })
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
          <h2 className="heading-5 text-ink-primary">{initial ? 'Sửa mục' : 'Tạo mục'}</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Loại mục tiêu</label>
            <select
              value={form.targetType}
              onChange={(e) => setForm((prev) => ({ ...prev, targetType: e.target.value as RoadmapItemTargetType }))}
              className="input-field"
            >
              <option value="COURSE">Môn học</option>
              <option value="REPO">Repo</option>
            </select>
          </div>
          <div>
            <label className="label">ID mục tiêu *</label>
            <input
              type="number"
              value={form.targetId || ''}
              onChange={(e) => setForm((prev) => ({ ...prev, targetId: Number(e.target.value) }))}
              className="input-field"
              min={1}
              required
            />
          </div>
          <div>
            <label className="label">Tiêu đề</label>
            <input
              type="text"
              value={form.title ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))}
              className="input-field"
            />
          </div>
          <div>
            <label className="label">Ghi chú</label>
            <textarea
              value={form.note ?? ''}
              onChange={(e) => setForm((prev) => ({ ...prev, note: e.target.value }))}
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
