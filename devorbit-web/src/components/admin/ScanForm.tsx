import { useState, useEffect } from 'react'
import { apiGet } from '../../lib/api'
import type { CourseSummary } from '../../types/api'

type ScanFormProps = {
  onSubmit: (courseId: number, query: string) => void
  loading?: boolean
}

export function ScanForm({ onSubmit, loading }: ScanFormProps) {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [courseId, setCourseId] = useState('')
  const [query, setQuery] = useState('')
  const [fetchingCourses, setFetchingCourses] = useState(true)

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch(err => console.error('Failed to fetch courses:', err))
      .finally(() => setFetchingCourses(false))
  }, [])

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!courseId || !query) return
    onSubmit(Number(courseId), query)
  }

  return (
    <form onSubmit={handleSubmit} className="glass-card space-y-5 p-6">
      <div>
        <label className="label text-clay-text-muted">Target Course</label>
        {fetchingCourses ? (
          <div className="h-10 w-full animate-pulse rounded-xl bg-clay-surface" />
        ) : (
          <select
            value={courseId}
            onChange={(e) => setCourseId(e.target.value)}
            className="input-field appearance-none cursor-pointer"
            required
          >
            <option value="" disabled className="bg-clay-bg">Select a course to target...</option>
            {courses.map((c) => (
              <option key={c.id} value={c.id} className="bg-clay-bg">
                {c.code} - {c.name}
              </option>
            ))}
          </select>
        )}
      </div>
      <div>
        <label className="label text-clay-text-muted">Search Query</label>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="input-field"
          placeholder="e.g. react course repository"
          required
        />
        {courseId && (
          <div className="mt-2.5 flex flex-wrap gap-2">
            {(() => {
              const selected = courses.find((c) => String(c.id) === courseId)
              if (!selected) return null
              const suggestions = [
                `${selected.code} ${selected.name}`,
                `${selected.code} repository`,
                `${selected.name} tutorial`,
                selected.code,
              ]
              return suggestions.map((s) => (
                <button
                  key={s}
                  type="button"
                  onClick={() => setQuery(s)}
                  className="rounded-full border border-cyan-600/20 bg-cyan-500/5 px-2.5 py-1 text-[11px] font-medium text-cyan-400 transition-colors hover:bg-cyan-500/10 hover:border-cyan-500/30"
                >
                  {s}
                </button>
              ))
            })()}
          </div>
        )}
      </div>
      <button
        type="submit"
        disabled={loading || fetchingCourses || !courseId}
        className="btn-primary w-full"
      >
        {loading ? (
          <span className="flex items-center gap-2">
            <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Scanning...
          </span>
        ) : (
          <span className="flex items-center gap-2">
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="8" />
              <path d="M21 21l-4.35-4.35" />
            </svg>
            Scan GitHub
          </span>
        )}
      </button>
    </form>
  )
}
