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
    <div className="mx-auto max-w-2xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Scan GitHub for Repositories</h1>
      <ScanForm onSubmit={handleScan} loading={loading} />
      {result && (
        <div className="mt-4 rounded-lg border bg-blue-50 p-4 text-sm text-blue-800">
          {result}
        </div>
      )}
    </div>
  )
}
