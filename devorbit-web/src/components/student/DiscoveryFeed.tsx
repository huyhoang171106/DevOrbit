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
      <div className="space-y-16 animate-pulse">
        <div className="h-48 bg-clay-surface rounded-3xl border-[3px] border-clay-border/30" />
        <div className="h-64 bg-clay-surface rounded-3xl border-[3px] border-clay-border/30" />
      </div>
    )
  }

  return (
    <div className="space-y-24">
      {/* Top Technologies */}
      <section>
        <div className="flex items-center justify-between mb-12">
          <div className="space-y-1">
            <h2 className="heading-2 uppercase italic text-clay-text">Công nghệ thịnh hành</h2>
            <p className="text-[15px] font-black uppercase tracking-widest text-ink-muted">Những ngôn ngữ và framework được sử dụng nhiều nhất</p>
          </div>
        </div>
        <div className="flex flex-wrap gap-5">
          {topStacks.map((stack) => (
            <div
              key={stack}
              className="px-10 py-5 bg-clay-surface rounded-3xl border-[3px] border-clay-border shadow-[8px_8px_0px_0px_var(--color-clay-shadow-outer)] hover:shadow-none hover:translate-x-1 hover:translate-y-1 transition-all cursor-default group"
            >
              <span className="text-[18px] font-black text-clay-text uppercase tracking-widest group-hover:text-clay-primary transition-colors">{stack}</span>
            </div>
          ))}
        </div>
      </section>

      {/* Recent Repositories */}
      <section>
        <div className="flex items-center justify-between mb-12">
          <div className="space-y-1">
            <h2 className="heading-2 uppercase italic text-clay-text">Cập nhật mới nhất</h2>
            <p className="text-[15px] font-black uppercase tracking-widest text-ink-muted">Các kho mã nguồn vừa được kết nối vào hệ thống</p>
          </div>
          <Link to="/courses" className="btn-secondary px-8 py-4 text-sm">
            Xem tất cả
          </Link>
        </div>
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-2">
          {recentRepos.map((repo) => (
            <RepoCard key={repo.id} repo={repo} />
          ))}
        </div>
      </section>
    </div>
  )
}
