import { useState, useMemo } from 'react'
import { MagnifyingGlass } from '@phosphor-icons/react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { CourseTable } from '../../components/admin/courses/CourseTable'
import { CourseFormDialog } from '../../components/admin/courses/CourseFormDialog'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { CourseSummary } from '../../types/api'
import type { CourseUpsertRequest } from '../../types/admin'

export function CoursesPage() {
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingCourse, setEditingCourse] = useState<CourseSummary | null>(null)
  const [mutationLoading, setMutationLoading] = useState(false)
  const [mutationError, setMutationError] = useState<string | null>(null)
  const [search, setSearch] = useState('')

  const token = getAdminToken()
  const { data: courses, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
    'courses',
  )

  const filteredCourses = useMemo(() => {
    if (!courses) return null
    const q = search.trim().toLowerCase()
    if (!q) return courses
    return courses.filter(
      (c) => c.code.toLowerCase().includes(q) || c.name.toLowerCase().includes(q)
    )
  }, [courses, search])

  const openCreate = () => {
    setEditingCourse(null)
    setDialogOpen(true)
  }

  const openEdit = (course: CourseSummary) => {
    setEditingCourse(course)
    setDialogOpen(true)
  }

  const handleSubmit = async (data: CourseUpsertRequest) => {
    setMutationLoading(true)
    setMutationError(null)
    try {
      if (editingCourse) {
        await adminApi.updateCourse(token!, editingCourse.id, data)
      } else {
        await adminApi.createCourse(token!, data)
      }
      setDialogOpen(false)
      setEditingCourse(null)
      refetch()
    } catch (e) {
      setMutationError(e instanceof Error ? e.message : 'Thao tác thất bại')
    } finally {
      setMutationLoading(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Xoá môn học này?')) return
    try {
      await adminApi.deleteCourse(token!, id)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout
      title="Môn học"
      description="Quản lý môn học"
      action={
        <div className="flex items-center gap-3">
          <div className="relative">
            <MagnifyingGlass className="absolute left-3 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-zinc-500" weight="regular" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Tìm theo mã, tên môn học..."
              className="bg-orbit-bg border border-orbit-border rounded-2xl pl-8 pr-3 py-2 text-[13px] text-orbit-text outline-none placeholder:text-zinc-500 focus:border-orbit-accent/60 w-[320px] transition-colors"
            />
          </div>
          <button onClick={openCreate} className="btn-primary px-5 py-2.5 text-[12px]">+ Thêm môn học</button>
        </div>
      }
    >
      {loading && <AdminSpinner text="Đang tải môn học..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {mutationError && <AdminErrorBanner message={mutationError} onRetry={() => setMutationError(null)} />}

      {filteredCourses && (
        <CourseTable
          courses={filteredCourses}
          onEdit={openEdit}
          onDelete={handleDelete}
        />
      )}

      <CourseFormDialog
        open={dialogOpen}
        onClose={() => { setDialogOpen(false); setEditingCourse(null) }}
        onSubmit={handleSubmit}
        initial={editingCourse}
        loading={mutationLoading}
      />
    </AdminPageLayout>
  )
}
