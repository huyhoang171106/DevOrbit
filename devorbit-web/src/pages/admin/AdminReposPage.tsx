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

  if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Approved Repositories</h1>
      <ApprovedRepoTable repos={repos} onDeactivate={handleDeactivate} />
    </div>
  )
}
