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
    <form onSubmit={handleSubmit} className="space-y-4 rounded-lg border bg-white p-6 shadow-sm">
      <div>
        <label className="block text-sm font-medium text-gray-700">Course ID</label>
        <input
          type="number"
          value={courseId}
          onChange={(e) => setCourseId(e.target.value)}
          className="mt-1 w-full rounded border px-3 py-2 text-sm"
          placeholder="e.g. 1"
          required
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700">Search Query</label>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="mt-1 w-full rounded border px-3 py-2 text-sm"
          placeholder="e.g. react course repository"
          required
        />
      </div>
      <button
        type="submit"
        disabled={loading}
        className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? 'Scanning...' : 'Scan GitHub'}
      </button>
    </form>
  )
}
