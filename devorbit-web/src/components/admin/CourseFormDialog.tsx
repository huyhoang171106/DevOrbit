import { useState, useEffect } from 'react'

export type CourseFormData = {
  code: string
  name: string
  credits: number
  lectureHours: number
  practiceHours: number
  subjectType: string
  description: string
}

type CourseFormDialogProps = {
  open: boolean
  onClose: () => void
  onSubmit: (data: CourseFormData) => void
  initial?: CourseFormData
}

const emptyForm: CourseFormData = {
  code: '',
  name: '',
  credits: 0,
  lectureHours: 0,
  practiceHours: 0,
  subjectType: '',
  description: '',
}

export function CourseFormDialog({ open, onClose, onSubmit, initial }: CourseFormDialogProps) {
  const [form, setForm] = useState<CourseFormData>(initial ?? emptyForm)

  useEffect(() => {
    setForm(initial ?? emptyForm)
  }, [initial, open])

  if (!open) return null

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: name === 'credits' || name === 'lectureHours' || name === 'practiceHours' ? Number(value) : value,
    }))
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-8 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-4 text-clay-text">
            {initial ? 'Edit Course' : 'Create Course'}
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-clay-text-muted hover:bg-glass-surface-raised transition-colors"
          >
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="label">Code</label>
            <input name="code" value={form.code} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">Name</label>
            <input name="name" value={form.name} onChange={handleChange} className="input-field" required />
          </div>
          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="label">Credits</label>
              <input name="credits" type="number" value={form.credits} onChange={handleChange} className="input-field" />
            </div>
            <div>
              <label className="label">Lecture</label>
              <input name="lectureHours" type="number" value={form.lectureHours} onChange={handleChange} className="input-field" />
            </div>
            <div>
              <label className="label">Practice</label>
              <input name="practiceHours" type="number" value={form.practiceHours} onChange={handleChange} className="input-field" />
            </div>
          </div>
          <div>
            <label className="label">Subject Type</label>
            <select name="subjectType" value={form.subjectType} onChange={handleChange} className="input-field">
              <option value="">Select</option>
              <option value="Cơ sở ngành">Cơ sở ngành</option>
              <option value="Chuyên ngành">Chuyên ngành</option>
              <option value="Đại cương">Đại cương</option>
            </select>
          </div>
          <div>
            <label className="label">Description</label>
            <textarea name="description" value={form.description} onChange={handleChange} rows={3} className="input-field" />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost">
              Cancel
            </button>
            <button type="submit" className="btn-primary">
              {initial ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
