import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiAdminGet, apiAdminDelete } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { ApprovedRepoTable } from '../../components/admin/ApprovedRepoTable'
import type { RepoSummary } from '../../types/api'

export function AdminReposPage() {
  const navigate = useNavigate()
  const [repos, setRepos] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)
  const token = getAdminToken()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  const fetchRepos = useCallback(async () => {
    try {
      const data = await apiAdminGet<RepoSummary[]>('/api/admin/repos', token!)
      setRepos(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [token])

  useEffect(() => {
    fetchRepos()
  }, [fetchRepos])

  async function handleDeactivate(id: number) {
    if (!confirm('Deactivate this repo?')) return
    try {
      await apiAdminDelete(`/api/admin/repos/${id}`, token!)
      fetchRepos()
    } catch (err) {
      console.error(err)
    }
  }

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading...</div>
    </div>
  )

  return (
    <div>
      <h1 className="page-title mb-6">Approved Repositories</h1>
      <ApprovedRepoTable repos={repos} onDeactivate={handleDeactivate} />
    </div>
  )
}
