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

  const { data: candidates, loading, refetch: fetchCandidates } = useApiFetch(
    () => apiAdminGet<RepoCandidate[]>('/api/admin/repo-candidates', token),
    [token],
  )

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

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading...</div>
    </div>
  )

  return (
    <div>
      <h1 className="page-title mb-6">Repo Candidates</h1>
      <CandidateTable
        candidates={candidates ?? []}
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
