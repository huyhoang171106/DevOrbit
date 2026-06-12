import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { CourseSummary } from '../../types/api'
import type { CourseRelationshipRequest } from '../../types/api'

export function RelationshipsPage() {
  const token = getAdminToken()
  const [filter, setFilter] = useState('')
  const [dialogOpen, setDialogOpen] = useState(false)

  const { data: relationships, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getRelationships(t),
    [],
  )

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
  )

  const filtered = (relationships ?? []).filter((r) =>
    !filter || r.courseCode.toLowerCase().includes(filter.toLowerCase()) ||
    r.relatedCourseCode.toLowerCase().includes(filter.toLowerCase()),
  )

  const handleCreate = async (data: CourseRelationshipRequest) => {
    try {
      await adminApi.createRelationship(token!, data)
      setDialogOpen(false)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  const handleDelete = async (id: number) => {
    if (!token || !confirm('Xoá quan hệ này?')) return
    try {
      await adminApi.deleteRelationship(token, id)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout
      title="Quan hệ môn học"
      description="Quản lý quan hệ tiên quyết và liên kết giữa các môn học"
      action={
        <button onClick={() => setDialogOpen(true)} className="btn-primary self-start">+ Thêm quan hệ</button>
      }
    >
      <div className="mb-6">
        <input
          type="text"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          className="input-field max-w-md"
          placeholder="Lọc theo mã môn học..."
        />
      </div>

      {loading && <AdminSpinner text="Đang tải quan hệ..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}

      {relationships && (
        <div className="glass-card overflow-hidden border border-orbit-border">
          <table className="w-full">
            <thead>
              <tr className="border-b border-orbit-border bg-orbit-surface/50">
                <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Môn học</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Loại</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Môn liên quan</th>
                <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-clay-border">
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-10 text-center body-sm text-ink-secondary">Không có quan hệ nào</td>
                </tr>
              )}
              {filtered.map((rel) => (
                <tr key={rel.id} className="transition-colors hover:bg-orbit-surface/30">
                  <td className="px-4 py-3 text-sm font-medium text-ink-primary">{rel.courseCode} — {rel.courseName}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className="inline-flex px-2 py-0.5 rounded text-xs bg-orbit-accent/10 text-orbit-accent">
                      {rel.relationType}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-ink-secondary">{rel.relatedCourseCode} — {rel.relatedCourseName}</td>
                  <td className="px-4 py-3 text-sm text-right">
                    <button onClick={() => handleDelete(rel.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {dialogOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
            <div className="flex items-center justify-between mb-6">
              <h2 className="heading-5 text-ink-primary">Thêm quan hệ</h2>
              <button onClick={() => setDialogOpen(false)} className="text-ink-secondary hover:text-ink-primary transition-colors">
                <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M18 6L6 18M6 6l12 12" />
                </svg>
              </button>
            </div>
            <form onSubmit={(e) => {
              e.preventDefault()
              const data = new FormData(e.currentTarget)
              handleCreate({
                courseId: Number(data.get('courseId')),
                relatedCourseId: Number(data.get('relatedCourseId')),
                relationType: String(data.get('relationType')) as CourseRelationshipRequest['relationType'],
              })
            }} className="space-y-4">
              <div>
                <label className="label">Môn học</label>
                <select name="courseId" className="input-field" required>
                  <option value="">Chọn môn học</option>
                  {courses?.map((c: CourseSummary) => (
                    <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="label">Loại quan hệ</label>
                <select name="relationType" className="input-field" required>
                  <option value="PREREQUISITE">Tiên quyết</option>
                  <option value="COMPLEMENTARY">Bổ trợ</option>
                  <option value="COREQUISITE">Song hành</option>
                </select>
              </div>
              <div>
                <label className="label">Môn liên quan</label>
                <select name="relatedCourseId" className="input-field" required>
                  <option value="">Chọn môn học</option>
                  {courses?.map((c: CourseSummary) => (
                    <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setDialogOpen(false)} className="btn-ghost text-sm">Huỷ</button>
                <button type="submit" className="btn-primary text-sm">Thêm</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminPageLayout>
  )
}
