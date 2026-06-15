import { useState } from 'react'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'
import { getAdminToken } from '../../../lib/auth'
import type { RepoCandidate } from '../../../types/api'
import type { CandidateReviewRequest } from '../../../types/admin'

export function RepoCandidatesTab() {
  const token = getAdminToken()
  const [approvalDialog, setApprovalDialog] = useState<{ candidate: RepoCandidate } | null>(null)
  const [actionError, setActionError] = useState<string | null>(null)
  const [reviewForm, setReviewForm] = useState<CandidateReviewRequest>({
    description: '',
    techStacks: [],
    reviewNote: '',
  })
  const [techStackInput, setTechStackInput] = useState('')

  const techStacks = reviewForm.techStacks ?? []

  const { data: candidates, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getCandidates(t),
    [],
  )

  const handleApprove = async (candidate: RepoCandidate) => {
    if (!token) return
    setActionError(null)
    try {
      await adminApi.approveCandidate(token, candidate.id, reviewForm)
      setApprovalDialog(null)
      setReviewForm({ description: '', techStacks: [], reviewNote: '' })
      refetch()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Duyệt thất bại')
    }
  }

  const handleReject = async (candidate: RepoCandidate) => {
    if (!token || !confirm('Từ chối candidate này?')) return
    setActionError(null)
    try {
      await adminApi.rejectCandidate(token, candidate.id)
      refetch()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Từ chối thất bại')
    }
  }

  const addTechStack = () => {
    const trimmed = techStackInput.trim()
    if (trimmed && !techStacks.includes(trimmed)) {
      setReviewForm((prev) => ({ ...prev, techStacks: [...(prev.techStacks ?? []), trimmed] }))
      setTechStackInput('')
    }
  }

  const removeTechStack = (stack: string) => {
    setReviewForm((prev) => ({ ...prev, techStacks: (prev.techStacks ?? []).filter((s) => s !== stack) }))
  }

  if (loading) return <AdminSpinner text="Đang tải candidates..." />
  if (error) return <AdminErrorBanner message={error} onRetry={refetch} />
  if (actionError) return <AdminErrorBanner message={actionError} onRetry={() => setActionError(null)} />

  return (
    <div>
      <div className="glass-card overflow-hidden border border-orbit-border">
        <table className="w-full">
          <thead>
            <tr className="border-b border-orbit-border bg-orbit-surface/50">
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Repo</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Môn học</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Ngôn ngữ</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Stars</th>
              <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-clay-border">
            {(!candidates || candidates.length === 0) && (
              <tr>
                <td colSpan={5} className="px-4 py-10 text-center body-sm text-ink-secondary">Không có candidates</td>
              </tr>
            )}
            {candidates?.map((candidate) => (
              <tr key={candidate.id} className="transition-colors hover:bg-orbit-surface/30">
                <td className="px-4 py-3 text-sm text-center">
                  <a href={candidate.githubUrl} target="_blank" rel="noopener noreferrer" className="font-medium text-ink-primary hover:text-orbit-accent transition-colors">
                    {candidate.githubName}
                  </a>
                  <span className="block text-xs text-ink-muted">{candidate.githubOwner}</span>
                </td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{candidate.courseCode}</td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{candidate.primaryLanguage ?? '-'}</td>
                <td className="px-4 py-3 text-sm text-center text-ink-secondary">{candidate.stars}</td>
                <td className="px-4 py-3 text-sm text-center">
                  <div className="flex justify-center gap-2">
                    <button
                      onClick={() => { setApprovalDialog({ candidate }); setReviewForm({ description: '', techStacks: [], reviewNote: '' }) }}
                      className="btn-ghost text-xs text-green-400 hover:text-green-300"
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => handleReject(candidate)}
                      className="btn-ghost text-xs text-red-400 hover:text-red-300"
                    >
                      Reject
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {approvalDialog && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
            <h3 className="heading-5 text-ink-primary mb-4">Approve — {approvalDialog.candidate.githubName}</h3>
            <div className="space-y-4">
              <div>
                <label className="label">Mô tả</label>
                <textarea
                  value={reviewForm.description}
                  onChange={(e) => setReviewForm((prev) => ({ ...prev, description: e.target.value }))}
                  className="input-field"
                  rows={2}
                />
              </div>
              <div>
                <label className="label">Tech Stacks</label>
                <div className="flex gap-2 mb-2">
                  <input
                    type="text"
                    value={techStackInput}
                    onChange={(e) => setTechStackInput(e.target.value)}
                    className="input-field flex-1"
                    placeholder="VD: React, Spring Boot"
                    onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addTechStack() } }}
                  />
                  <button onClick={addTechStack} className="btn-primary text-xs px-3">Thêm</button>
                </div>
                {techStacks.length > 0 && (
                  <div className="flex flex-wrap gap-2">
                    {techStacks.map((stack) => (
                      <span key={stack} className="inline-flex items-center gap-1 px-2 py-1 rounded bg-orbit-accent/10 text-xs text-orbit-accent">
                        {stack}
                        <button onClick={() => removeTechStack(stack)} className="hover:text-red-400">&times;</button>
                      </span>
                    ))}
                  </div>
                )}
              </div>
              <div>
                <label className="label">Ghi chú duyệt</label>
                <textarea
                  value={reviewForm.reviewNote}
                  onChange={(e) => setReviewForm((prev) => ({ ...prev, reviewNote: e.target.value }))}
                  className="input-field"
                  rows={2}
                />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button onClick={() => setApprovalDialog(null)} className="btn-ghost text-sm">Huỷ</button>
                <button onClick={() => handleApprove(approvalDialog.candidate)} className="btn-primary text-sm">Approve</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
