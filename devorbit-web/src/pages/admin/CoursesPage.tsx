import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { CourseTable } from '../../components/admin/courses/CourseTable'
import { CourseFormDialog } from '../../components/admin/courses/CourseFormDialog'
import { CourseResourceTabs } from '../../components/admin/courses/CourseResourceTabs'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { CourseSummary } from '../../types/api'
import type { CourseUpsertRequest } from '../../types/admin'

export function CoursesPage() {
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingCourse, setEditingCourse] = useState<CourseSummary | null>(null)
  const [selectedCourse, setSelectedCourse] = useState<CourseSummary | null>(null)
  const [mutationLoading, setMutationLoading] = useState(false)

  const token = getAdminToken()
  const { data: courses, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
  )

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
      console.error(e)
    } finally {
      setMutationLoading(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Xoá môn học này?')) return
    try {
      await adminApi.deleteCourse(token!, id)
      if (selectedCourse?.id === id) setSelectedCourse(null)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout
      title="Môn học"
      description="Quản lý môn học và tài nguyên"
      action={
        <button onClick={openCreate} className="btn-primary self-start">+ Thêm môn học</button>
      }
    >
      {loading && <AdminSpinner text="Đang tải môn học..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}

      {courses && (
        <div className="space-y-6">
          <CourseTable
            courses={courses}
            onEdit={openEdit}
            onDelete={handleDelete}
            onManageResources={setSelectedCourse}
            selectedCourseId={selectedCourse?.id}
          />

          {selectedCourse && (
            <div>
              <h3 className="heading-5 text-ink-primary mb-4">
                Tài nguyên — {selectedCourse.code} {selectedCourse.name}
              </h3>
              <CourseResourceTabs courseId={selectedCourse.id} />
            </div>
          )}
        </div>
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
