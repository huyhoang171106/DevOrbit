import { useState, useEffect, useRef, useCallback } from 'react'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { getAdminToken } from '../../../lib/auth'
import type { CourseSummary, RepoCandidate } from '../../../types/api'

export function RepoScanTab() {
  const token = getAdminToken()
  const [selectedCourseId, setSelectedCourseId] = useState<number | ''>('')
  const [query, setQuery] = useState('')
  const [scanning, setScanning] = useState(false)
  const [scanningAll, setScanningAll] = useState(false)
  const [logs, setLogs] = useState<string[]>([])
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const successTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const [pollingActive, setPollingActive] = useState(true)
  const logEndRef = useRef<HTMLDivElement>(null)

  const showSuccess = (msg: string) => {
    setSuccessMessage(msg)
    if (successTimeoutRef.current) clearTimeout(successTimeoutRef.current)
    successTimeoutRef.current = setTimeout(() => setSuccessMessage(null), 3000)
  }

  const friendlyError = (raw: string): string => {
    if (raw.includes('422') || raw.includes('Validation Failed')) return 'Query GitHub không hợp lệ, vui lòng nhập lại'
    if (raw.includes('409') || raw.includes('already in progress')) return 'Đã có scan đang chạy, vui lòng đợi'
    if (raw.includes('429') || raw.includes('403') || raw.includes('rate limit')) return 'GitHub API bị giới hạn, hãy đợi vài phút rồi thử lại'
    if (raw.includes('401')) return 'Lỗi xác thực GitHub, kiểm tra GITHUB_TOKEN'
    if (raw.includes('500') || raw.includes('Internal Server Error')) return 'Lỗi máy chủ, vui lòng thử lại sau'
    return 'Scan thất bại, xem nhật ký để biết chi tiết'
  }

  const { data: courses } = useAdminFetch(
    (t) => adminApi.getCourses(t),
    [],
    'courses',
  )

  const fetchLogs = useCallback(async () => {
    if (!token) return
    try {
      const result = await adminApi.getScanLogs(token)
      setLogs(result)
    } catch { /* ignore polling errors */ }
  }, [token])

  useEffect(() => {
    if (!pollingActive) return
    fetchLogs()
    const interval = setInterval(fetchLogs, 2000)
    return () => clearInterval(interval)
  }, [fetchLogs, pollingActive])

  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [logs])

  const handleScan = async () => {
    if (!token || !selectedCourseId) return
    setScanning(true)
    setError(null)
    setSuccessMessage(null)
    try {
      const result: RepoCandidate[] = await adminApi.scan(token, Number(selectedCourseId), query)
      fetchLogs()
      showSuccess(`Tìm thấy ${result.length} repo mới`)
    } catch (e) {
      const raw = e instanceof Error ? e.message : ''
      setError(friendlyError(raw))
    } finally {
      setScanning(false)
    }
  }

  const handleClearLogs = async () => {
    if (!token) return
    try {
      await adminApi.clearScanLogs(token)
      setLogs([])
    } catch { /* ignore */ }
  }

  const handleScanAll = async () => {
    if (!token) return
    setScanningAll(true)
    setError(null)
    setSuccessMessage(null)
    try {
      await adminApi.scanAll(token)
      fetchLogs()
      showSuccess('Đã bắt đầu scan tất cả môn học trong nền')
    } catch (e) {
      const raw = e instanceof Error ? e.message : ''
      setError(friendlyError(raw))
    } finally {
      setScanningAll(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="glass-card p-6">
        <h3 className="heading-5 text-ink-primary mb-4">Scan GitHub Repos</h3>
        {successMessage && (
          <div className="mb-4 p-3 rounded-lg bg-green-500/10 border border-green-500/30 text-sm text-green-400">
            {successMessage}
          </div>
        )}
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
              disabled={scanning || !selectedCourseId || !query.trim()}
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
        <div className="flex items-center justify-between mb-3">
          <h3 className="heading-5 text-ink-primary">Nhật ký scan</h3>
          <div className="flex gap-2">
            <button
              onClick={() => setPollingActive((p) => !p)}
              className={`text-xs px-3 py-1 rounded transition-colors ${
                pollingActive
                  ? 'bg-yellow-500/20 text-yellow-400 hover:bg-yellow-500/30'
                  : 'bg-green-500/20 text-green-400 hover:bg-green-500/30'
              }`}
            >
              {pollingActive ? 'Tạm dừng' : 'Tiếp tục'}
            </button>
            <button
              onClick={handleClearLogs}
              className="text-xs px-3 py-1 rounded bg-red-500/20 text-red-400 hover:bg-red-500/30 transition-colors"
            >
              Xoá log
            </button>
          </div>
        </div>
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
