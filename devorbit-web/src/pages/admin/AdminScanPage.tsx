import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiAdminPost } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { ScanForm } from '../../components/admin/ScanForm'
import type { RepoCandidate } from '../../types/api'

export function AdminScanPage() {
  const navigate = useNavigate()
  const [candidates, setCandidates] = useState<RepoCandidate[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const token = getAdminToken()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  async function handleScan(courseId: number, query: string) {
    setLoading(true)
    setError(null)
    setCandidates([])
    try {
      const res = await apiAdminPost<RepoCandidate[]>('/api/admin/github/scan', token!, {
        courseId,
        query,
      })
      setCandidates(res)
    } catch (err) {
      setError('Scan failed.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="page-title mb-8">Scan GitHub for Repositories</h1>
      <ScanForm onSubmit={handleScan} loading={loading} />
      {error && (
        <div className="mt-4 rounded-xl border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {error}
        </div>
      )}
      {candidates.length > 0 && (
        <div className="mt-6 rounded-xl border border-white/5 bg-white/[0.02]">
          <h2 className="px-4 py-3 text-sm font-semibold text-slate-300 border-b border-white/5">
            Found {candidates.length} candidate{candidates.length > 1 ? 's' : ''}
          </h2>
          <ul className="divide-y divide-white/5">
            {candidates.map((c) => (
              <li key={c.id} className="flex items-center justify-between px-4 py-3">
                <div>
                  <span className="text-sm text-slate-100 font-medium">{c.githubOwner}/{c.githubName}</span>
                  <span className={`ml-3 inline-block rounded-full px-2 py-0.5 text-xs font-medium ${
                    c.status === 'NEW' ? 'bg-amber-500/10 text-amber-300' : ''
                  }`}>{c.status}</span>
                </div>
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-xs text-cyan-400 hover:text-cyan-300 transition-colors"
                >
                  View on GitHub →
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}
      {!loading && !error && candidates.length === 0 && (
        <p className="mt-6 text-center text-sm text-slate-500">
          No candidates yet. Run a scan to find repositories.
        </p>
      )}
    </div>
  )
}
