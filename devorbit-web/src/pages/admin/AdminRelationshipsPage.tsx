import { useState } from 'react'
import { apiGet, apiAdminGet, apiAdminPost, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { RelationshipDialog } from '../../components/admin/RelationshipDialog'
import type { CourseRelationshipResponse, CourseSummary } from '../../types/api'

const typeLabels: Record<string, string> = {
  PREREQUISITE: 'Prerequisite',
  COMPLEMENTARY: 'Complementary',
  COREQUISITE: 'Corequisite',
}

export function AdminRelationshipsPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [dialogOpen, setDialogOpen] = useState(false)
  const [filterCourse, setFilterCourse] = useState('')

  const { data: rels, loading, refetch } = useApiFetch(
    () => apiAdminGet<CourseRelationshipResponse[]>('/api/admin/courses/relationships', token),
    [token],
  )

  const { data: courses } = useApiFetch(
    () => apiGet<CourseSummary[]>('/api/courses'),
    [],
  )

  async function handleCreate(data: { courseId: number; relatedCourseId: number; relationType: string }) {
    try {
      await apiAdminPost('/api/admin/courses/relationships', token, data)
      setDialogOpen(false)
      refetch()
    } catch (e) { console.error(e) }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this relationship?')) return
    try {
      await apiAdminDelete(`/api/admin/courses/relationships/${id}`, token)
      refetch()
    } catch (e) { console.error(e) }
  }

  const filtered = (rels ?? []).filter((r) => {
    if (!filterCourse) return true
    const q = filterCourse.toLowerCase()
    return r.courseCode.toLowerCase().includes(q) || r.relatedCourseCode.toLowerCase().includes(q)
  })

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="display-sm text-clay-text mb-1">Course Relationships</h1>
          <p className="body-sm text-clay-text-muted">Define prerequisites, complementary, and corequisite relationships.</p>
        </div>
        <button onClick={() => setDialogOpen(true)} className="btn-primary self-start text-sm px-4 py-2">
          <svg className="mr-2 h-4 w-4 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M5 12h14" /></svg>
          Add Relationship
        </button>
      </div>

      {/* Filter */}
      <div className="mb-[24px]">
        <input
          placeholder="Filter by course code..."
          value={filterCourse}
          onChange={(e) => setFilterCourse(e.target.value)}
          className="input-field max-w-xs"
        />
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-[96px]">
          <div className="flex items-center gap-3 body-sm text-clay-text-muted">
            <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Loading relationships...
          </div>
        </div>
      ) : (
        <div className="glass-card overflow-hidden border border-clay-border p-0">
          <table className="min-w-full text-sm">
            <thead>
              <tr className="border-b border-clay-border bg-glass-surface-raised">
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Course</th>
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Type</th>
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Related Course</th>
                <th className="table-header text-right font-medium text-clay-text-muted py-3 px-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-clay-border bg-clay-bg">
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-10 text-center body-sm text-clay-text-muted">No relationships found.</td>
                </tr>
              )}
              {filtered.map((r) => (
                <tr key={r.id} className="transition-colors hover:bg-glass-surface-raised">
                  <td className="table-cell py-3 px-4">
                    <span className="code-sm text-clay-text-muted bg-clay-surface border border-clay-border px-1.5 py-0.5 rounded-md">{r.courseCode}</span>
                    <span className="ml-2 body-sm text-clay-text font-medium">{r.courseName}</span>
                  </td>
                  <td className="table-cell py-3 px-4">
                    <span className={`badge-tag ${r.relationType === 'PREREQUISITE' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' : 'bg-clay-surface text-clay-text-muted'}`}>
                      {typeLabels[r.relationType] ?? r.relationType}
                    </span>
                  </td>
                  <td className="table-cell py-3 px-4">
                    <span className="code-sm text-clay-text-muted bg-clay-surface border border-clay-border px-1.5 py-0.5 rounded-md">{r.relatedCourseCode}</span>
                    <span className="ml-2 body-sm text-clay-text font-medium">{r.relatedCourseName}</span>
                  </td>
                  <td className="table-cell text-right py-3 px-4">
                    <button onClick={() => handleDelete(r.id)} className="btn-secondary !py-1 !px-3 !bg-clay-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <RelationshipDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onSubmit={handleCreate}
        courses={courses ?? []}
      />
    </div>
  )
}
