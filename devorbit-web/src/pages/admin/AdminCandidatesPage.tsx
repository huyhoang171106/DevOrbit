import { useState } from 'react'
import { apiAdminGet, apiAdminPost } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { CandidateTable } from '../../components/admin/CandidateTable'
import { ApproveModal } from '../../components/admin/ApproveModal'
import type { RepoCandidate } from '../../types/api'

export function AdminCandidatesPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [approvingId, setApprovingId] = useState<number | null>(null)
  const [selectedSubject, setSelectedSubject] = useState<string>('all')
  const [reviewer, setReviewer] = useState<string>('all')

  const { data: candidates, loading, refetch: fetchCandidates } = useApiFetch(
    () => apiAdminGet<RepoCandidate[]>('/api/admin/repo-candidates', token),
    [token],
  )

  const { data: courses } = useApiFetch(
    () => apiAdminGet<any[]>('/api/courses', token),
    [token]
  )

  const mappedCandidates = (candidates ?? []).map(c => {
    let course = (courses ?? []).find(course => course.id === (c as any).courseId)
    
    // Smart detection: try to guess from githubName if no explicit course link exists
    let inferredCode = null
    if (!course) {
      // Look for patterns like IT001, CS101, etc. in the name
      const match = c.githubName.match(/[A-Z]{2,3}\d{3}/i)
      if (match) {
        inferredCode = match[0].toUpperCase()
        // Try to find a course that matches this code exactly
        const existingCourse = (courses ?? []).find(f => f.code.toUpperCase() === inferredCode)
        if (existingCourse) course = existingCourse
      }
    }

    return {
      ...c,
      courseCode: (c as any).courseCode || course?.code || inferredCode
    }
  })

  const filteredCandidates = selectedSubject === 'all'
    ? mappedCandidates
    : mappedCandidates.filter(c => c.courseCode === selectedSubject)

  const uniqueSubjects = Array.from(new Set(mappedCandidates.map(c => c.courseCode).filter(Boolean) as string[]))

  // Split logic for 3 reviewers: Bảo (0), Bắc (1), An (2)
  const finalCandidates = filteredCandidates.filter((_, index) => {
    if (reviewer === 'all') return true
    if (reviewer === 'Bảo') return index % 3 === 0
    if (reviewer === 'Bắc') return index % 3 === 1
    if (reviewer === 'An') return index % 3 === 2
    return true
  })

  async function handleApprove(id: number) {
    setApprovingId(id)
  }

  async function handleConfirmApprove(id: number, description: string, techStacks: string[], reviewNote: string) {
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/approve`, token, {
        description,
        techStacks,
        reviewNote,
      })
      setApprovingId(null)
      fetchCandidates()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleReject(id: number) {
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/reject`, token, {})
      fetchCandidates()
    } catch (err) {
      console.error(err)
    }
  }

  const selectedCandidate = approvingId != null
    ? (candidates ?? []).find((c) => c.id === approvingId) ?? null
    : null

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-steel">
          <svg className="h-5 w-5 animate-spin text-brand-green" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading candidates...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1440px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 className="display-sm text-ink mb-1">Repo Candidates</h1>
          <p className="body-sm text-steel">
            Review and approve repositories discovered from GitHub scans.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-4">
          <div className="flex items-center gap-3">
            <label className="text-xs font-medium text-steel">Assignee:</label>
            <div className="flex bg-surface-soft border border-hairline rounded-lg p-0.5">
              {['all', 'Bảo', 'Bắc', 'An'].map((p) => (
                <button
                  key={p}
                  onClick={() => setReviewer(p)}
                  className={`px-3 py-1 text-[11px] font-semibold rounded-md transition-all ${
                    reviewer === p
                      ? 'bg-canvas text-brand-green shadow-sm border border-hairline-soft'
                      : 'text-steel hover:text-ink'
                  }`}
                >
                  {p === 'all' ? 'All' : p}
                </button>
              ))}
            </div>
          </div>

          <div className="h-6 w-px bg-hairline" />

          <div className="flex items-center gap-3">
            <label className="text-xs font-medium text-steel">Filter by Subject:</label>
            <select
              value={selectedSubject}
              onChange={(e) => setSelectedSubject(e.target.value)}
              className="input-field !py-1.5 !px-3 !w-[160px] !text-xs cursor-pointer"
            >
              <option value="all">All Subjects</option>
              {uniqueSubjects.map(code => (
                <option key={code} value={code}>{code}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className="mb-4 flex items-center justify-between px-2">
        <div className="text-[11px] text-steel">
          Showing <strong>{finalCandidates.length}</strong> repositories {reviewer !== 'all' ? `assigned to ${reviewer}` : 'total'}
        </div>
      </div>

      <CandidateTable
        candidates={finalCandidates}
        onApprove={handleApprove}
        onReject={handleReject}
      />

      {selectedCandidate && (
        <ApproveModal
          candidate={selectedCandidate}
          onConfirm={(description, techStacks, reviewNote) =>
            handleConfirmApprove(selectedCandidate.id, description, techStacks, reviewNote)
          }
          onClose={() => setApprovingId(null)}
        />
      )}
    </div>
  )
}
