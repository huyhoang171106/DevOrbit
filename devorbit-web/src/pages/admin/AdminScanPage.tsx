import { useState, useEffect, useRef } from 'react'
import { apiAdminPost, apiAdminGet } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth } from '../../lib/hooks'
import { ScanForm } from '../../components/admin/ScanForm'
import type { RepoCandidate } from '../../types/api'

export function AdminScanPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [candidates, setCandidates] = useState<RepoCandidate[]>([])
  const [scanning, setScanning] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [bulkScanning, setBulkScanning] = useState(false)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [courses, setCourses] = useState<any[]>([])
  
  const [logs, setLogs] = useState<string[]>([])
  const terminalRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    apiAdminGet<any[]>('/api/courses', token)
      .then(setCourses)
      .catch(console.error)
  }, [token])

  // Polling logs when bulk scanning
  useEffect(() => {
    let interval: number | undefined
    if (bulkScanning) {
      interval = window.setInterval(async () => {
        try {
          const res = await apiAdminGet<string[]>('/api/admin/github/scan-logs', token)
          setLogs(res)
          
          // Check if scan completed (last log contains "completed")
          if (res.length > 0 && res[res.length - 1].toLowerCase().includes('completed')) {
            setBulkScanning(false)
          }
        } catch (err) {
          console.error('Failed to fetch logs:', err)
        }
      }, 2000)
    }
    return () => {
      if (interval) clearInterval(interval)
    }
  }, [bulkScanning, token])

  // Auto scroll terminal to bottom
  useEffect(() => {
    if (terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight
    }
  }, [logs])

  async function handleScanAll() {
    if (!window.confirm('This will scan all courses on GitHub. It takes about 2-3 minutes to finish in the background. Continue?')) return
    
    setBulkScanning(true)
    setError(null)
    setSuccessMessage(null)
    setLogs(['[System] Initializing connection...'])
    
    try {
      await apiAdminPost('/api/admin/github/scan-all', token, {})
      setSuccessMessage('Bulk scan started! You can track the progress in the console below.')
    } catch (err) {
      setError('Failed to start bulk scan.')
      setBulkScanning(false)
      console.error(err)
    }
  }

  async function handleScan(courseId: number, query: string) {
    setScanning(true)
    setError(null)
    setSuccessMessage(null)
    setCandidates([])
    try {
      const res = await apiAdminPost<RepoCandidate[]>('/api/admin/github/scan', token, {
        courseId,
        query,
      })
      
      // Manually attach course code if missing from backend response
      const targetCourse = courses.find(c => c.id === courseId)
      const mappedResults = res.map(candidate => ({
        ...candidate,
        courseCode: candidate.courseCode || targetCourse?.code
      }))
      
      setCandidates(mappedResults)
    } catch (err) {
      setError('Scan failed.')
      console.error(err)
    } finally {
      setScanning(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl pb-20">
      <div className="mb-8 flex items-start justify-between">
        <div>
          <p className="section-subtitle mb-2">Discovery</p>
          <h1 className="page-title">Scan GitHub for Repositories</h1>
          <p className="mt-2 text-sm text-slate-400">
            Search GitHub for repositories related to a specific course.
          </p>
        </div>
        <button
          onClick={handleScanAll}
          disabled={bulkScanning}
          className="btn-secondary flex items-center gap-2 text-xs py-2"
        >
          {bulkScanning ? (
            <svg className="h-3 w-3 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
          ) : (
            <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
            </svg>
          )}
          Scan All Courses
        </button>
      </div>

      {successMessage && (
        <div className="mb-6 flex items-center gap-2 rounded-xl border border-emerald-500/20 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">
          <svg className="h-4 w-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          {successMessage}
        </div>
      )}

      <ScanForm onSubmit={handleScan} loading={scanning} />

      {/* Terminal UI */}
      {(bulkScanning || logs.length > 0) && (
        <div className="mt-8 rounded-2xl border border-white/[0.06] bg-[#0d1117] overflow-hidden shadow-2xl">
          <div className="px-4 py-2 bg-white/[0.04] border-b border-white/[0.06] flex items-center justify-between">
            <div className="flex gap-1.5">
              <div className="w-2.5 h-2.5 rounded-full bg-[#ff5f56]" />
              <div className="w-2.5 h-2.5 rounded-full bg-[#ffbd2e]" />
              <div className="w-2.5 h-2.5 rounded-full bg-[#27c93f]" />
            </div>
            <span className="text-[10px] font-mono text-slate-500 uppercase tracking-widest">discovery_terminal</span>
          </div>
          <div 
            ref={terminalRef}
            className="p-4 h-60 overflow-y-auto font-mono text-[11px] leading-relaxed scrollbar-thin scrollbar-thumb-white/10"
          >
            {logs.map((log, i) => (
              <div key={i} className="mb-0.5">
                <span className="text-emerald-500 mr-2">$</span>
                <span className={log.includes('!!') ? 'text-rose-400' : 'text-slate-300'}>{log}</span>
              </div>
            ))}
            {bulkScanning && (
              <div className="flex items-center gap-2 text-slate-500">
                <span className="text-emerald-500 mr-2">$</span>
                <span className="animate-pulse">_</span>
              </div>
            )}
          </div>
        </div>
      )}

      {error && (
        <div className="mt-4 flex items-center gap-2 rounded-xl border border-rose-500/20 bg-rose-500/10 px-4 py-3 text-sm text-rose-300">
          <svg className="h-4 w-4 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 8v4M12 16h.01" />
          </svg>
          {error}
        </div>
      )}

      {candidates.length > 0 && (
        <div className="mt-6 rounded-2xl border border-white/[0.06] bg-white/[0.02] overflow-hidden">
          <div className="px-5 py-3.5 border-b border-white/[0.04]">
            <h2 className="text-sm font-semibold text-slate-300">
              Found {candidates.length} candidate{candidates.length > 1 ? 's' : ''}
            </h2>
          </div>
          <ul className="divide-y divide-white/[0.04]">
            {candidates.map((c) => (
              <li key={c.id} className="flex items-center justify-between px-5 py-3.5 transition-colors hover:bg-white/[0.02]">
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-medium text-slate-100 truncate">
                      {c.githubOwner}/{c.githubName}
                    </span>
                    <span className={`inline-block rounded-full px-2 py-0.5 text-[11px] font-medium border ${
                      c.status === 'NEW'
                        ? 'bg-amber-500/10 text-amber-300 border-amber-500/10'
                        : c.status === 'APPROVED'
                        ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/10'
                        : 'bg-rose-500/10 text-rose-300 border-rose-500/10'
                    }`}>
                      {c.status}
                    </span>
                    {c.courseCode && (
                      <span className="inline-block rounded-full px-2 py-0.5 text-[11px] font-medium bg-cyan-500/10 text-cyan-400 border border-cyan-500/10">
                        {c.courseCode}
                      </span>
                    )}
                  </div>
                  {c.description && (
                    <p className="mt-1 text-xs text-slate-500 line-clamp-1">{c.description}</p>
                  )}
                </div>
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="ml-4 flex-shrink-0 text-xs text-cyan-400 hover:text-cyan-300 transition-colors inline-flex items-center gap-1"
                >
                  View
                  <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
                  </svg>
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}

      {!scanning && !error && candidates.length === 0 && !bulkScanning && logs.length === 0 && (
        <div className="mt-6 text-center py-10">
          <svg className="mx-auto mb-3 h-10 w-10 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" />
          </svg>
          <p className="text-sm text-slate-500">
            No candidates yet. Run a scan to find repositories.
          </p>
        </div>
      )}
    </div>
  )
}
