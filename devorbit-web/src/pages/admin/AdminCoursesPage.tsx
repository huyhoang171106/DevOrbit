import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiGet, apiAdminPost, apiAdminPut, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { CourseFormDialog, type CourseFormData } from '../../components/admin/CourseFormDialog'
import type { CourseSummary, CourseDetail } from '../../types/api'

export function AdminCoursesPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const navigate = useNavigate()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<CourseFormData | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)

  const { data: courses, loading, refetch: fetchCourses } = useApiFetch(
    () => apiGet<CourseSummary[]>('/api/courses'),
    [],
  )

  async function handleCreate(data: CourseFormData) {
    try {
      await apiAdminPost('/api/admin/courses', token, data)
      setDialogOpen(false)
      fetchCourses()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleUpdate(data: CourseFormData) {
    if (!editingId) return
    try {
      await apiAdminPut(`/api/admin/courses/${editingId}`, token, data)
      setDialogOpen(false)
      setEditing(null)
      fetchCourses()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleDeactivate(id: number) {
    if (!confirm('Deactivate this course?')) return
    try {
      await apiAdminDelete(`/api/admin/courses/${id}`, token)
      fetchCourses()
    } catch (err) {
      console.error(err)
    }
  }

  async function openEdit(c: CourseSummary) {
    setEditingId(c.id)
    try {
      const detail = await apiGet<CourseDetail>(`/api/courses/${c.id}`)
      setEditing({
        code: detail.code,
        name: detail.name,
        credits: detail.credits,
        lectureHours: detail.theoryHours ?? 0,
        practiceHours: detail.practiceHours ?? 0,
        subjectType: detail.subjectType ?? '',
        description: detail.description ?? '',
      })
    } catch {
      setEditing({
        code: c.code,
        name: c.name,
        credits: 0,
        lectureHours: 0,
        practiceHours: 0,
        subjectType: '',
        description: '',
      })
    }
    setDialogOpen(true)
  }

  function openCreate() {
    setEditingId(null)
    setEditing(null)
    setDialogOpen(true)
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-ink-secondary">
          <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading courses...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="display-sm text-ink mb-1">Courses</h1>
          <p className="body-sm text-ink-secondary">Manage your course catalog.</p>
        </div>
        <button onClick={openCreate} className="btn-primary self-start">
          <svg className="mr-1.5 h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M12 5v14M5 12h14" />
          </svg>
          Create Course
        </button>
      </div>

      <div className="glass-card overflow-hidden border border-glass-border p-0">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="border-b border-glass-border bg-glass-surface-raised">
              <th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Code</th>
              <th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Name</th>
              <th className="table-header text-right font-medium text-ink-secondary py-3 px-4">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-glass-border bg-cosmic-surface">
            {(courses ?? []).length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-10 text-center text-ink-secondary body-sm">
                  No courses found.
                </td>
              </tr>
            )}
            {(courses ?? []).map((c) => (
              <tr key={c.id} className="transition-colors hover:bg-glass-surface-raised">
                <td className="table-cell py-3 px-4">
                  <span className="badge-tag">
                    {c.code}
                  </span>
                </td>
                <td className="table-cell text-ink py-3 px-4">{c.name}</td>
                <td className="table-cell text-right py-3 px-4">
                  <div className="flex items-center justify-end gap-2">
                    <button
                      onClick={() => navigate(`/admin/courses/${c.id}/resources`)}
                      className="btn-secondary !py-1 !px-2 !text-xs !bg-glass-surface"
                    >
                      Resources
                    </button>
                    <button
                      onClick={() => openEdit(c)}
                      className="btn-secondary !py-1 !px-2 !text-xs !bg-glass-surface"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeactivate(c.id)}
                      className="btn-secondary !py-1 !px-2 !text-xs !bg-glass-surface !text-red-400 hover:!bg-red-500/10 border border-red-500/30"
                    >
                      Deactivate
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <CourseFormDialog
        open={dialogOpen}
        onClose={() => { setDialogOpen(false); setEditing(null); setEditingId(null) }}
        onSubmit={editing ? handleUpdate : handleCreate}
        initial={editing ?? undefined}
      />
    </div>
  )
}
