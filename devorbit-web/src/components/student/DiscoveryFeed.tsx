import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import type { RepoSummary } from '../../types/api'
import { RepoCard } from './RepoCard'

export function DiscoveryFeed() {
  const [recentRepos, setRecentRepos] = useState<RepoSummary[]>([])
  const [topStacks, setTopStacks] = useState<string[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      apiGet<RepoSummary[]>('/api/discovery/recent-repos'),
      apiGet<string[]>('/api/discovery/top-stacks')
    ])
      .then(([repos, stacks]) => {
        setRecentRepos(repos)
        setTopStacks(stacks)
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="space-y-12 animate-pulse">
        <div className="h-64 bg-glass-surface rounded-3xl" />
        <div className="h-64 bg-glass-surface rounded-3xl" />
      </div>
    )
  }

  return (
    <div className="space-y-16">
      {/* Top Technologies */}
      <section>
        <div className="flex items-center justify-between mb-8">
          <h2 className="display-sm text-ink">Trending Technologies</h2>
          <span className="text-sm text-ink-muted">Most mapped stacks</span>
        </div>
        <div className="flex flex-wrap gap-3">
          {topStacks.map((stack) => (
            <div
              key={stack}
              className="px-6 py-3 glass-card border-opacity-30 hover:border-emerald-500/30 transition-all cursor-default"
            >
              <span className="heading-5 text-ink-secondary">{stack}</span>
            </div>
          ))}
        </div>
      </section>

      {/* Recent Repositories */}
      <section>
        <div className="flex items-center justify-between mb-8">
          <h2 className="display-sm text-ink">Recently Added</h2>
          <Link to="/courses" className="text-sm text-emerald-400 font-medium hover:underline">
            Browse All Courses
          </Link>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-2">
          {recentRepos.map((repo) => (
            <RepoCard key={repo.id} repo={repo} />
          ))}
        </div>
      </section>
    </div>
  )
}
