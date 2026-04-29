import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiAdminPost } from '../../lib/api'
import { getAdminToken, isAuthenticated } from '../../lib/auth'
import { ScanForm } from '../../components/admin/ScanForm'

export function AdminScanPage() {
  const navigate = useNavigate()
  const [result, setResult] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const token = getAdminToken()

  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])

  async function handleScan(courseId: number, query: string) {
    setLoading(true)
    setResult(null)
    try {
      const res = await apiAdminPost<{ message?: string }>('/api/admin/github/scan', token!, {
        courseId,
        query,
      })
      setResult(res.message ?? 'Scan completed.')
    } catch (err) {
      setResult('Scan failed.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="page-title mb-8">Scan GitHub for Repositories</h1>
      <ScanForm onSubmit={handleScan} loading={loading} />
      {result && (
        <div className="mt-4 rounded-xl border border-cyan-500/20 bg-cyan-500/10 px-4 py-3 text-sm text-cyan-300">
          {result}
        </div>
      )}
    </div>
  )
}
