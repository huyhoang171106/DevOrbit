import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { apiGet, apiStudentPost } from '../../lib/api'
import { getStudentToken } from '../../lib/auth'
import { RepoCard } from '../../components/student/RepoCard'
import { RepoFilterBar } from '../../components/student/RepoFilterBar'
import type { RepoSummary } from '../../types/api'

export function CourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>()
  const [repos, setRepos] = useState<RepoSummary[]>([])
  const [filtered, setFiltered] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [savedRepoId, setSavedRepoId] = useState<number | null>(null)

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

  async function saveRepo(repoId: number) {
    const token = getStudentToken()
    if (!token) {
      window.location.href = '/student/login'
      return
    }
    await apiStudentPost('/api/student/bookmarks', token, { targetType: 'REPO', targetId: repoId })
    setSavedRepoId(repoId)
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-ink-secondary">
          <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading repositories...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[800px] mx-auto px-[32px] py-[64px]">
      <Link
        to="/courses"
        className="mb-6 inline-flex items-center gap-1.5 rounded-lg py-1.5 body-sm text-ink-secondary transition-all duration-200 hover:text-ink"
      >
        <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Back to Courses
      </Link>

      <div className="mb-[40px]">
        <h1 className="display-sm text-ink mb-2">Course Repositories</h1>
        <p className="body-md text-ink-secondary">Real-world projects mapped to this course.</p>
      </div>

      {allStacks.length > 0 && (
        <RepoFilterBar techStacks={allStacks} onFilter={handleFilter} />
      )}

      <div className="space-y-[16px] mt-8">
        {filtered.map((r) => (
          <div key={r.id} className="relative group">
            <RepoCard repo={r} />
            <button
              type="button"
              onClick={() => void saveRepo(r.id)}
              className="absolute right-4 top-4 btn-secondary !py-1 !px-2 !text-xs !bg-glass-surface"
            >
              {savedRepoId === r.id ? 'Saved' : 'Save'}
            </button>
          </div>
        ))}
      </div>

      {filtered.length === 0 && (
        <div className="glass-card py-16 text-center text-ink-secondary bg-transparent border-dashed border-glass-border mt-8">
          <svg className="mx-auto mb-3 h-10 w-10 text-ink-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p className="body-md">No repositories found.</p>
        </div>
      )}
    </div>
  )
}
