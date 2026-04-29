import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiGet, apiAdminPost, apiAdminPut, apiAdminDelete } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { CourseFormDialog, type CourseFormData } from '../../components/admin/CourseFormDialog'
import type { CourseSummary } from '../../types/api'

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

  function openEdit(c: CourseSummary) {
    setEditingId(c.id)
    setEditing({
      code: c.code,
      name: c.name,
      credits: 0,
      lectureHours: 0,
      practiceHours: 0,
      subjectType: '',
      description: '',
    })
    setDialogOpen(true)
  }

  function openCreate() {
    setEditingId(null)
    setEditing(null)
    setDialogOpen(true)
  }

  if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Courses</h1>
        <button
          onClick={openCreate}
          className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          Create Course
        </button>
      </div>
      <div className="overflow-x-auto rounded-lg border bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left font-medium text-gray-600">Code</th>
              <th className="px-4 py-3 text-left font-medium text-gray-600">Name</th>
              <th className="px-4 py-3 text-left font-medium text-gray-600">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {courses.map((c) => (
              <tr key={c.id} className="even:bg-gray-50">
                <td className="px-4 py-3 font-medium">{c.code}</td>
                <td className="px-4 py-3">{c.name}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(c)}
                      className="rounded bg-gray-100 px-3 py-1 text-xs font-medium text-gray-700 hover:bg-gray-200"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeactivate(c.id)}
                      className="rounded bg-red-600 px-3 py-1 text-xs font-medium text-white hover:bg-red-700"
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
