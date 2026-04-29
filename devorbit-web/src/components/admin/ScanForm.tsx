import { useState } from 'react'

type ScanFormProps = {
  onSubmit: (courseId: number, query: string) => void
  loading?: boolean
}

export function ScanForm({ onSubmit, loading }: ScanFormProps) {
  const [courseId, setCourseId] = useState('')
  const [query, setQuery] = useState('')

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!courseId || !query) return
    onSubmit(Number(courseId), query)
  }

  return (
    <form onSubmit={handleSubmit} className="glass-card space-y-5 p-6">
      <div>
        <label className="label">Course ID</label>
        <input
          type="number"
          value={courseId}
          onChange={(e) => setCourseId(e.target.value)}
          className="input-field"
          placeholder="e.g. 1"
          required
        />
      </div>
      <div>
        <label className="label">Search Query</label>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="input-field"
          placeholder="e.g. react course repository"
          required
        />
      </div>
      <button
        type="submit"
        disabled={loading}
        className="btn-primary w-full"
      >
        {loading ? 'Scanning...' : 'Scan GitHub'}
      </button>
    </form>
  )
}
