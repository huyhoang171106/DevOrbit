import { useState, useRef, useEffect } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { CourseRelationshipRequest } from '../../types/api'

export function RelationshipsPage() {
  const token = getAdminToken()
  const [filter, setFilter] = useState('')
  const [dialogOpen, setDialogOpen] = useState(false)

  const TYPE_LABELS: Record<string, string> = {
    PREREQUISITE: 'Tiên quyết',
    COMPLEMENTARY: 'Bổ trợ',
    COREQUISITE: 'Song hành',
  }

  const { data: relationships, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getRelationships(t),
    [],
  )

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
    'courses',
  )

  const filtered = (relationships ?? []).filter((r) =>
    !filter || r.courseCode.toLowerCase().includes(filter.toLowerCase()) ||
    r.relatedCourseCode.toLowerCase().includes(filter.toLowerCase()),
  )

  const [createError, setCreateError] = useState<string | null>(null)

  const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null)
  const [courseSearch, setCourseSearch] = useState('')
  const [courseOpen, setCourseOpen] = useState(false)
  const [selectedRelatedId, setSelectedRelatedId] = useState<number | null>(null)
  const [relatedSearch, setRelatedSearch] = useState('')
  const [relatedOpen, setRelatedOpen] = useState(false)
  const [relationType, setRelationType] = useState('PREREQUISITE')
  const courseRef = useRef<HTMLDivElement>(null)
  const relatedRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (courseRef.current && !courseRef.current.contains(e.target as Node)) setCourseOpen(false)
      if (relatedRef.current && !relatedRef.current.contains(e.target as Node)) setRelatedOpen(false)
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  const courseList = courses ?? []
  const filteredCourses = courseList.filter(
    (c) => c.id !== selectedRelatedId && (!courseSearch || c.code.toLowerCase().includes(courseSearch.toLowerCase()) || c.name.toLowerCase().includes(courseSearch.toLowerCase()))
  )
  const filteredRelated = courseList.filter(
    (c) => c.id !== selectedCourseId && (!relatedSearch || c.code.toLowerCase().includes(relatedSearch.toLowerCase()) || c.name.toLowerCase().includes(relatedSearch.toLowerCase()))
  )

  const handleCreate = async () => {
    if (!selectedCourseId || !selectedRelatedId) {
      setCreateError('Vui lòng chọn đầy đủ môn học và môn liên quan')
      return
    }
    if (selectedCourseId === selectedRelatedId) {
      setCreateError('Môn học và môn liên quan không được giống nhau')
      return
    }
    setCreateError(null)
    try {
      await adminApi.createRelationship(token!, {
        courseId: selectedCourseId,
        relatedCourseId: selectedRelatedId,
        relationType: relationType as CourseRelationshipRequest['relationType'],
      })
      setDialogOpen(false)
      setSelectedCourseId(null)
      setCourseSearch('')
      setSelectedRelatedId(null)
      setRelatedSearch('')
      refetch()
    } catch (e) {
      setCreateError(e instanceof Error ? e.message : 'Thêm quan hệ thất bại')
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
      {createError && <AdminErrorBanner message={createError} onRetry={() => setCreateError(null)} />}

      {relationships && (
        <div className="glass-card overflow-hidden border border-orbit-border">
          <table className="w-full">
            <thead>
              <tr className="border-b border-orbit-border bg-orbit-surface/50">
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Môn học</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Loại</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Môn liên quan</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
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
                  <td className="px-4 py-3 text-sm text-center font-medium text-ink-primary">{rel.courseCode} — {rel.courseName}</td>
                  <td className="px-4 py-3 text-sm text-center">
                    <span className="inline-flex px-2 py-0.5 rounded text-xs bg-orbit-accent/10 text-orbit-accent">
                      {TYPE_LABELS[rel.relationType] ?? rel.relationType}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary">{rel.relatedCourseCode} — {rel.relatedCourseName}</td>
                  <td className="px-4 py-3 text-sm text-center">
                    <div className="flex items-center justify-center">
                      <button onClick={() => handleDelete(rel.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                    </div>
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
              <button onClick={() => { setDialogOpen(false); setSelectedCourseId(null); setCourseSearch(''); setSelectedRelatedId(null); setRelatedSearch('') }} className="text-ink-secondary hover:text-ink-primary transition-colors">
                <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M18 6L6 18M6 6l12 12" />
                </svg>
              </button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="label">Môn học</label>
                <div ref={courseRef} className="relative">
                  <input
                    type="text"
                    value={courseSearch}
                    onChange={(e) => setCourseSearch(e.target.value)}
                    onFocus={() => setCourseOpen(true)}
                    placeholder="Gõ tên hoặc mã môn..."
                    className="input-field w-full"
                  />
                  {courseOpen && (
                    <div className="absolute left-0 top-full mt-1 w-full max-h-[240px] overflow-y-auto rounded-2xl border border-orbit-border/50 bg-orbit-surface shadow-diffusion z-50" onWheel={(e) => e.stopPropagation()}>
                      {filteredCourses.length === 0 ? (
                        <p className="px-4 py-3 text-sm text-ink-secondary text-center">Không tìm thấy</p>
                      ) : (
                        filteredCourses.map((c) => (
                          <button
                            key={c.id}
                            type="button"
                            onClick={() => { setSelectedCourseId(c.id); setCourseSearch(`${c.code} — ${c.name}`); setCourseOpen(false) }}
                            className={`w-full text-left px-4 py-2.5 text-sm transition-colors ${selectedCourseId === c.id ? 'bg-orbit-accent/10 text-orbit-accent' : 'text-ink-secondary hover:bg-orbit-surface/50'}`}
                          >
                            {c.code} — {c.name}
                          </button>
                        ))
                      )}
                    </div>
                  )}
                </div>
              </div>
              <div>
                <label className="label">Loại quan hệ</label>
                <select value={relationType} onChange={(e) => setRelationType(e.target.value)} className="input-field w-full">
                  <option value="PREREQUISITE">Tiên quyết</option>
                  <option value="COMPLEMENTARY">Bổ trợ</option>
                  <option value="COREQUISITE">Song hành</option>
                </select>
              </div>
              <div>
                <label className="label">Môn liên quan</label>
                <div ref={relatedRef} className="relative">
                  <input
                    type="text"
                    value={relatedSearch}
                    onChange={(e) => setRelatedSearch(e.target.value)}
                    onFocus={() => setRelatedOpen(true)}
                    placeholder="Gõ tên hoặc mã môn..."
                    className="input-field w-full"
                  />
                  {relatedOpen && (
                    <div className="absolute left-0 top-full mt-1 w-full max-h-[240px] overflow-y-auto rounded-2xl border border-orbit-border/50 bg-orbit-surface shadow-diffusion z-50" onWheel={(e) => e.stopPropagation()}>
                      {filteredRelated.length === 0 ? (
                        <p className="px-4 py-3 text-sm text-ink-secondary text-center">Không tìm thấy</p>
                      ) : (
                        filteredRelated.map((c) => (
                          <button
                            key={c.id}
                            type="button"
                            onClick={() => { setSelectedRelatedId(c.id); setRelatedSearch(`${c.code} — ${c.name}`); setRelatedOpen(false) }}
                            className={`w-full text-left px-4 py-2.5 text-sm transition-colors ${selectedRelatedId === c.id ? 'bg-orbit-accent/10 text-orbit-accent' : 'text-ink-secondary hover:bg-orbit-surface/50'}`}
                          >
                            {c.code} — {c.name}
                          </button>
                        ))
                      )}
                    </div>
                  )}
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => { setDialogOpen(false); setSelectedCourseId(null); setCourseSearch(''); setSelectedRelatedId(null); setRelatedSearch('') }} className="btn-ghost text-sm">Huỷ</button>
                <button type="button" onClick={handleCreate} className="btn-primary text-sm">Thêm</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </AdminPageLayout>
  )
}
