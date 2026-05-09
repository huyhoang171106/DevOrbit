import { useState, useEffect } from 'react'
import type { CourseRelationshipRequest, CourseRelationType, CourseSummary } from '../../types/api'

type Props = {
  open: boolean
  onClose: () => void
  onSubmit: (data: CourseRelationshipRequest) => void
  courses: CourseSummary[]
}

const empty: CourseRelationshipRequest = { courseId: 0, relatedCourseId: 0, relationType: 'PREREQUISITE' }

const relationTypes: { value: CourseRelationType; label: string }[] = [
  { value: 'PREREQUISITE', label: 'Prerequisite' },
  { value: 'COMPLEMENTARY', label: 'Complementary' },
  { value: 'COREQUISITE', label: 'Corequisite' },
]

export function RelationshipDialog({ open, onClose, onSubmit, courses }: Props) {
  const [form, setForm] = useState<CourseRelationshipRequest>(empty)

  useEffect(() => { setForm(empty) }, [open])

  if (!open) return null

  function handleChange(e: React.ChangeEvent<HTMLSelectElement>) {
    const { name, value } = e.target
    setForm((p) => ({ ...p, [name]: name === 'relationType' ? value : Number(value) }))
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (form.courseId === form.relatedCourseId) return
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold font-heading text-clay-text">Create Relationship</h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-clay-text-muted hover:bg-glass-surface-hover hover:text-clay-text transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="label">Course *</label>
            <select name="courseId" value={form.courseId} onChange={handleChange} className="input-field" required>
              <option value={0}>Select course...</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>{c.code} - {c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Related Course *</label>
            <select name="relatedCourseId" value={form.relatedCourseId} onChange={handleChange} className="input-field" required>
              <option value={0}>Select related course...</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>{c.code} - {c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Relation Type *</label>
            <select name="relationType" value={form.relationType} onChange={handleChange} className="input-field">
              {relationTypes.map((t) => (
                <option key={t.value} value={t.value}>{t.label}</option>
              ))}
            </select>
          </div>
          {form.courseId !== 0 && form.relatedCourseId !== 0 && form.courseId === form.relatedCourseId && (
            <p className="text-xs text-rose-400">A course cannot relate to itself.</p>
          )}
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost">Cancel</button>
            <button type="submit" className="btn-primary" disabled={form.courseId === 0 || form.relatedCourseId === 0 || form.courseId === form.relatedCourseId}>
              Create
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
