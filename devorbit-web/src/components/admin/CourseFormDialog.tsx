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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="w-full max-w-lg rounded-lg bg-white p-6 shadow-xl">
        <h2 className="mb-4 text-lg font-semibold">{initial ? 'Edit Course' : 'Create Course'}</h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm font-medium text-gray-700">Code</label>
            <input name="code" value={form.code} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Name</label>
            <input name="name" value={form.name} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm" required />
          </div>
          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700">Credits</label>
              <input name="credits" type="number" value={form.credits} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Lecture</label>
              <input name="lectureHours" type="number" value={form.lectureHours} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Practice</label>
              <input name="practiceHours" type="number" value={form.practiceHours} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Subject Type</label>
            <select name="subjectType" value={form.subjectType} onChange={handleChange} className="mt-1 w-full rounded border px-3 py-2 text-sm">
              <option value="">Select</option>
              <option value="Cơ sở ngành">Cơ sở ngành</option>
              <option value="Chuyên ngành">Chuyên ngành</option>
              <option value="Đại cương">Đại cương</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Description</label>
            <textarea name="description" value={form.description} onChange={handleChange} rows={3} className="mt-1 w-full rounded border px-3 py-2 text-sm" />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="rounded bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200">
              Cancel
            </button>
            <button type="submit" className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700">
              {initial ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
