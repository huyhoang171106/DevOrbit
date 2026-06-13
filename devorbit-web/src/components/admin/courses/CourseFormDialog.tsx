import { useState, useEffect } from 'react'
import type { CourseUpsertRequest } from '../../../types/admin'
import type { CourseSummary } from '../../../types/api'

interface CourseFormDialogProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: CourseUpsertRequest) => void
  initial?: CourseSummary | null
  loading?: boolean
}

const emptyForm: CourseUpsertRequest = {
  code: '',
  name: '',
  credits: 0,
  subjectType: 'CHUYEN_NGANH',
  isOpen: true,
  managementUnit: '',
  description: '',
}

export function CourseFormDialog({ open, onClose, onSubmit, initial, loading }: CourseFormDialogProps) {
  const [form, setForm] = useState<CourseUpsertRequest>(emptyForm)

  useEffect(() => {
    if (initial) {
      setForm({
        code: initial.code,
        name: initial.name,
        credits: initial.credits ?? 0,
        subjectType: initial.loaiMonHoc ?? 'CHUYEN_NGANH',
        isOpen: true,
        managementUnit: '',
        description: initial.description ?? '',
      })
    } else {
      setForm(emptyForm)
    }
  }, [initial, open])

  if (!open) return null

  const handleChange = (field: keyof CourseUpsertRequest, value: string | number | boolean | null) => {
    setForm((prev) => ({ ...prev, [field]: value }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">
            {initial ? 'Sửa môn học' : 'Thêm môn học'}
          </h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Mã môn học *</label>
              <input
                type="text"
                value={form.code}
                onChange={(e) => handleChange('code', e.target.value)}
                className="input-field"
                required
              />
            </div>
            <div>
              <label className="label">Số tín chỉ *</label>
              <input
                type="number"
                value={form.credits}
                onChange={(e) => handleChange('credits', Number(e.target.value))}
                className="input-field"
                min={0}
                required
              />
            </div>
          </div>
          <div>
            <label className="label">Tên môn học *</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => handleChange('name', e.target.value)}
              className="input-field"
              required
            />
          </div>
          <div>
            <label className="label">Mô tả</label>
            <textarea
              value={form.description ?? ''}
              onChange={(e) => handleChange('description', e.target.value)}
              className="input-field"
              rows={3}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Loại môn học</label>
              <select
                value={form.subjectType}
                onChange={(e) => handleChange('subjectType', e.target.value)}
                className="input-field"
              >
                <option value="DAI_CUONG">Đại cương</option>
                <option value="CO_SO">Cơ sở</option>
                <option value="CHUYEN_NGANH">Chuyên ngành</option>
              </select>
            </div>
            <div>
              <label className="label">Đơn vị quản lý</label>
              <input
                type="text"
                value={form.managementUnit ?? ''}
                onChange={(e) => handleChange('managementUnit', e.target.value)}
                className="input-field"
              />
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost text-sm" disabled={loading}>Huỷ</button>
            <button type="submit" className="btn-primary text-sm" disabled={loading}>
              {loading ? 'Đang lưu...' : initial ? 'Cập nhật' : 'Thêm'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
