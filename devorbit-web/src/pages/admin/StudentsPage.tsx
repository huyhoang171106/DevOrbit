import { useState, useCallback } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { StudentTable } from '../../components/admin/students/StudentTable'
import { StudentDetailDialog } from '../../components/admin/students/StudentDetailDialog'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { AdminStudent } from '../../types/admin'

export function StudentsPage() {
  const token = getAdminToken()
  const [search, setSearch] = useState('')
  const [detailStudent, setDetailStudent] = useState<AdminStudent | null>(null)
  const [detailOpen, setDetailOpen] = useState(false)

  const fetchStudents = useCallback(
    (t: string) => adminApi.getStudents(t, search || undefined),
    [search],
  )

  const { data: students, loading, error, refetch } = useAdminFetch(
    fetchStudents,
    [search],
  )

  const handleToggleActive = async (student: AdminStudent) => {
    if (!token) return
    const action = student.active ? 'vô hiệu hoá' : 'kích hoạt'
    if (!confirm(`${action} sinh viên này?`)) return
    try {
      await adminApi.toggleStudentActive(token, student.id)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout title="Sinh viên" description="Quản lý tài khoản sinh viên">
      <div className="mb-6">
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="input-field max-w-md"
          placeholder="Tìm theo mã số, họ tên hoặc email..."
        />
      </div>

      {loading && <AdminSpinner text="Đang tải sinh viên..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {students && (
        <StudentTable
          students={students}
          onToggleActive={handleToggleActive}
          onViewDetail={(s) => { setDetailStudent(s); setDetailOpen(true) }}
        />
      )}

      <StudentDetailDialog
        open={detailOpen}
        student={detailStudent}
        onClose={() => { setDetailOpen(false); setDetailStudent(null) }}
      />
    </AdminPageLayout>
  )
}
