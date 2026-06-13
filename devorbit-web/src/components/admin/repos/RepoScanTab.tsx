import { useState, useEffect, useRef, useCallback } from 'react'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { getAdminToken } from '../../../lib/auth'
import type { CourseSummary } from '../../../types/api'

export function RepoScanTab() {
  const token = getAdminToken()
  const [selectedCourseId, setSelectedCourseId] = useState<number | ''>('')
  const [query, setQuery] = useState('')
  const [scanning, setScanning] = useState(false)
  const [scanningAll, setScanningAll] = useState(false)
  const [logs, setLogs] = useState<string[]>([])
  const [error, setError] = useState<string | null>(null)
  const logEndRef = useRef<HTMLDivElement>(null)

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
  )

  const fetchLogs = useCallback(async () => {
    if (!token) return
    try {
      const result = await adminApi.getScanLogs(token)
      setLogs(result)
    } catch { /* ignore polling errors */ }
  }, [token])

  useEffect(() => {
    fetchLogs()
    const interval = setInterval(fetchLogs, 2000)
    return () => clearInterval(interval)
  }, [fetchLogs])

  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [logs])

  const handleScan = async () => {
    if (!token || !selectedCourseId) return
    setScanning(true)
    setError(null)
    try {
      await adminApi.scan(token, Number(selectedCourseId), query)
      fetchLogs()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Scan thất bại')
    } finally {
      setScanning(false)
    }
  }

  const handleScanAll = async () => {
    if (!token) return
    setScanningAll(true)
    setError(null)
    try {
      await adminApi.scanAll(token)
      fetchLogs()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Scan hàng loạt thất bại')
    } finally {
      setScanningAll(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="glass-card p-6">
        <h3 className="heading-5 text-ink-primary mb-4">Scan GitHub Repos</h3>
        {error && (
          <div className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-sm text-red-400">
            {error}
          </div>
        )}
        <div className="space-y-4">
          <div>
            <label className="label">Môn học</label>
            <select
              value={selectedCourseId}
              onChange={(e) => setSelectedCourseId(e.target.value ? Number(e.target.value) : '')}
              className="input-field"
            >
              <option value="">Chọn môn học</option>
              {courses?.map((c: CourseSummary) => (
                <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">GitHub Search Query</label>
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="input-field"
              placeholder="VD: topic:react+language:java"
            />
          </div>
          <div className="flex gap-3">
            <button
              onClick={handleScan}
              disabled={scanning || !selectedCourseId}
              className="btn-primary text-sm"
            >
              {scanning ? 'Đang scan...' : 'Scan'}
            </button>
            <button
              onClick={handleScanAll}
              disabled={scanningAll}
              className="btn-ghost text-sm"
            >
              {scanningAll ? 'Đang scan...' : 'Scan tất cả'}
            </button>
          </div>
        </div>
      </div>

      <div className="glass-card p-6">
        <h3 className="heading-5 text-ink-primary mb-3">Nhật ký scan</h3>
        <div className="bg-black/60 rounded-lg p-4 max-h-64 overflow-y-auto font-mono text-xs leading-relaxed">
          {logs.length === 0 ? (
            <span className="text-ink-muted">Chưa có nhật ký</span>
          ) : (
            logs.map((log, i) => (
              <div key={i} className="text-green-400/80">
                &gt; {log}
              </div>
            ))
          )}
          <div ref={logEndRef} />
        </div>
      </div>
    </div>
  )
}
