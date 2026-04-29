import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import { RepoCard } from '../../components/student/RepoCard'
import { RepoFilterBar } from '../../components/student/RepoFilterBar'
import type { RepoSummary } from '../../types/api'

export function CourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>()
  const [repos, setRepos] = useState<RepoSummary[]>([])
  const [filtered, setFiltered] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!courseId) return
    apiGet<RepoSummary[]>(`/api/courses/${courseId}/repos`)
      .then((data) => {
        setRepos(data)
        setFiltered(data)
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [courseId])

  const allStacks = [...new Set(repos.flatMap((r) => r.techStacks))]

  function handleFilter(stack: string | null) {
    if (!stack) {
      setFiltered(repos)
    } else {
      setFiltered(repos.filter((r) => r.techStacks.includes(stack)))
    }
  }

  if (loading) return <div className="p-8 text-center text-gray-500">Loading repositories...</div>

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Course Repositories</h1>
      <RepoFilterBar techStacks={allStacks} onFilter={handleFilter} />
      <div className="space-y-4">
        {filtered.map((r) => (
          <RepoCard key={r.id} repo={r} />
        ))}
      </div>
      {filtered.length === 0 && (
        <p className="py-8 text-center text-gray-500">No repositories found.</p>
      )}
    </div>
  )
}
