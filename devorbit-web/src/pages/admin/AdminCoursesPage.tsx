import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiGet, apiAdminPost, apiAdminPut, apiAdminDelete } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { CourseFormDialog, type CourseFormData } from '../../components/admin/CourseFormDialog'
import type { CourseSummary, CourseDetail } from '../../types/api'

export function AdminCoursesPage() {
  const navigate = useNavigate()
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<CourseFormData | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)

  const token = getAdminToken()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  const fetchCourses = useCallback(async () => {
    try {
      const data = await apiGet<CourseSummary[]>('/api/courses')
      setCourses(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchCourses()
  }, [fetchCourses])

  async function handleCreate(data: CourseFormData) {
    if (!token) return
    try {
      await apiAdminPost('/api/admin/courses', token, data)
      setDialogOpen(false)
      fetchCourses()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleUpdate(data: CourseFormData) {
    if (!token || !editingId) return
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
    if (!token) return
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

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading...</div>
    </div>
  )

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="page-title">Courses</h1>
        <button onClick={openCreate} className="btn-primary">
          Create Course
        </button>
      </div>
      <div className="glass-card overflow-hidden">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="border-b border-white/5">
              <th className="table-header">Code</th>
              <th className="table-header">Name</th>
              <th className="table-header">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {courses.map((c, i) => (
              <tr key={c.id} className={i % 2 === 1 ? 'bg-white/[0.02]' : ''}>
                <td className="table-cell font-medium text-slate-100">{c.code}</td>
                <td className="table-cell text-slate-300">{c.name}</td>
                <td className="table-cell">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(c)}
                      className="btn-ghost px-3 py-1 text-xs"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeactivate(c.id)}
                      className="btn-danger px-3 py-1 text-xs"
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
