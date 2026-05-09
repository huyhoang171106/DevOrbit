import { useState } from 'react'
import { apiAdminGet, apiAdminPost } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { CandidateTable } from '../../components/admin/CandidateTable'
import { ApproveModal } from '../../components/admin/ApproveModal'
import { CustomSelect } from '../../components/admin/CustomSelect'
import type { RepoCandidate, ReviewerStats } from '../../types/api'

export function AdminCandidatesPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [approvingId, setApprovingId] = useState<number | null>(null)
  const [selectedSubject, setSelectedSubject] = useState<string>('all')
  const [reviewer, setReviewer] = useState<string>('all')
  const [approveError, setApproveError] = useState<string | null>(null)

  const { data: candidates, loading, refetch: fetchCandidates } = useApiFetch(
    () => apiAdminGet<RepoCandidate[]>(`/api/admin/repo-candidates?reviewer=${reviewer}`, token),
    [token, reviewer],
  )

  const { data: stats, refetch: refetchStats } = useApiFetch(
    () => apiAdminGet<ReviewerStats[]>('/api/admin/repo-candidates/stats', token),
    [token],
  )

  const mappedCandidates = (candidates ?? []).map(c => ({
    ...c,
    courseCode: c.courseCode ?? null,
  }))

  const filteredCandidates = selectedSubject === 'all'
    ? mappedCandidates
    : mappedCandidates.filter(c => c.courseCode === selectedSubject)

  const uniqueSubjects = Array.from(new Set(mappedCandidates.map(c => c.courseCode).filter(Boolean) as string[]))

  async function handleApprove(id: number) {
    setApprovingId(id)
  }

  async function handleConfirmApprove(id: number, description: string, techStacks: string[], reviewNote: string) {
    setApproveError(null)
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/approve`, token, {
        description,
        techStacks,
        reviewNote,
      })
      setApprovingId(null)
      fetchCandidates()
      refetchStats()
    } catch (err) {
      console.error(err)
      setApproveError(err instanceof Error ? err.message : String(err))
    }
  }

  async function handleReject(id: number) {
    setApproveError(null)
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/reject`, token, {})
      fetchCandidates()
      refetchStats()
    } catch (err) {
      console.error(err)
      setApproveError(err instanceof Error ? err.message : String(err))
    }
  }

  const selectedCandidate = approvingId != null
    ? (candidates ?? []).find((c) => c.id === approvingId) ?? null
    : null

  const reviewerRemaining = (stats ?? []).reduce((acc, s) => {
    acc[s.reviewer] = s.remaining
    return acc
  }, {} as Record<string, number>)

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-ink-secondary">
          <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Đang tải danh sách ứng viên...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1440px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 className="display-sm text-clay-text mb-1">Ứng viên Repository</h1>
          <p className="body-sm text-ink-secondary">
            Kiểm duyệt và phê duyệt các repository được phát hiện từ quét GitHub.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-4">
          <div className="flex items-center gap-3">
            <label className="text-sm font-semibold text-ink-secondary">Người phụ trách:</label>
            <div className="flex bg-glass-surface-raised border border-glass-border rounded-xl p-1 shadow-inner-sm">
              {['all', 'Bảo', 'Bắc', 'An'].map((p) => (
                <button
                  key={p}
                  onClick={() => setReviewer(p)}
                  className={`px-5 py-2 text-[13px] font-bold rounded-lg transition-all ${reviewer === p
                      ? 'bg-cosmic-surface text-emerald-400 shadow-md border border-glass-border scale-105 z-10'
                      : 'text-ink-secondary hover:text-clay-text hover:bg-cosmic-surface/50'
                    }`}
                >
                  {p === 'all' ? 'Tất cả' : p}
                  {p !== 'all' && reviewerRemaining[p] !== undefined && (
                    <span className="ml-1.5 text-[11px] opacity-70">({reviewerRemaining[p]})</span>
                  )}
                </button>
              ))}
            </div>
          </div>

          <div className="h-6 w-px bg-glass-border" />

          <CustomSelect
            label="Lọc theo môn học:"
            value={selectedSubject}
            onChange={setSelectedSubject}
            options={[
              { value: 'all', label: 'Tất cả môn học' },
              ...uniqueSubjects.map(code => ({ value: code, label: code }))
            ]}
          />
        </div>
      </div>

      {approveError && (
        <div className="mb-4 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 px-5 py-3 body-sm text-red-400">
          <svg className="h-5 w-5 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 8v4M12 16h.01" />
          </svg>
          <span className="flex-1">{approveError}</span>
          <button onClick={() => setApproveError(null)} className="text-red-400/60 hover:text-red-400 flex-shrink-0">
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
      )}

      <div className="mb-4 flex items-center justify-between px-2">
        <div className="text-[11px] text-ink-secondary">
          Hiển thị <strong>{filteredCandidates.length}</strong> repositories{reviewer !== 'all' ? ` được giao cho ${reviewer}` : ''}
        </div>
      </div>

      <CandidateTable
        candidates={filteredCandidates}
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
