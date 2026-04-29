import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiAdminGet, apiAdminPost } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { CandidateTable } from '../../components/admin/CandidateTable'
import type { RepoCandidate } from '../../types/api'

export function AdminCandidatesPage() {
  const navigate = useNavigate()
  const [candidates, setCandidates] = useState<RepoCandidate[]>([])
  const [loading, setLoading] = useState(true)
  const token = getAdminToken()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  const fetchCandidates = useCallback(async () => {
    try {
      const data = await apiAdminGet<RepoCandidate[]>('/api/admin/repo-candidates', token!)
      setCandidates(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [token])

  useEffect(() => {
    fetchCandidates()
  }, [fetchCandidates])

  async function handleApprove(id: number) {
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/approve`, token!, {})
      fetchCandidates()
    } catch (err) {
      console.error(err)
    }
  }

  async function handleReject(id: number) {
    try {
      await apiAdminPost(`/api/admin/repo-candidates/${id}/reject`, token!, {})
      fetchCandidates()
    } catch (err) {
      console.error(err)
    }
  }

  if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Repo Candidates</h1>
      <CandidateTable
        candidates={candidates}
        onApprove={handleApprove}
        onReject={handleReject}
      />
    </div>
  )
}
