import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
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

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading repositories...</div>
    </div>
  )

  return (
    <div>
      <Link
        to="/courses"
        className="mb-6 inline-flex items-center gap-1 text-sm text-slate-400 transition-colors hover:text-amber-400"
      >
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Back to Courses
      </Link>
      <div className="mb-8 flex items-center gap-3">
        <svg className="h-8 w-8 text-cyan-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
          <path d="M3 3v18h18M7 16l4-8 4 4 4-6" />
        </svg>
        <h1 className="page-title">Course Repositories</h1>
      </div>
      {allStacks.length > 0 && (
        <RepoFilterBar techStacks={allStacks} onFilter={handleFilter} />
      )}
      <div className="space-y-4">
        {filtered.map((r) => (
          <RepoCard key={r.id} repo={r} />
        ))}
      </div>
      {filtered.length === 0 && (
        <p className="py-12 text-center text-slate-500">No repositories found.</p>
      )}
    </div>
  )
}
