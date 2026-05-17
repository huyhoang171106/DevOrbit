import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiGet } from '../../lib/api'
import type { RepoSummary } from '../../types/api'
import { RepoCard } from './RepoCard'
import { ArrowRight } from '@phosphor-icons/react'

export function DiscoveryFeed() {
  const [recentRepos, setRecentRepos] = useState<RepoSummary[]>([])
  const [topStacks, setTopStacks] = useState<string[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([
      apiGet<RepoSummary[]>('/api/discovery/recent-repos'),
      apiGet<string[]>('/api/discovery/top-stacks')
    ])
      .then(([repos, stacks]) => {
        setRecentRepos(repos)
        setTopStacks(stacks)
      })
      .catch((err) => {
        console.error(err)
        setError('Không thể tải dữ liệu khám phá.')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="space-y-12">
        <div className="skeleton h-16 w-64 rounded-3xl" />
        <div className="flex flex-wrap gap-4">
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} className="skeleton h-16 w-32 rounded-3xl" />
          ))}
        </div>
        <div className="skeleton h-16 w-48 rounded-3xl mt-12" />
        <div className="grid gap-6 sm:grid-cols-2">
          <div className="skeleton h-48 rounded-4xl" />
          <div className="skeleton h-48 rounded-4xl" />
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="orbit-card p-12 text-center border-dashed border-2 border-orbit-accent/10">
        <p className="body-md text-orbit-text-muted">{error}</p>
      </div>
    )
  }

  return (
    <div className="space-y-24">
      {/* Top Technologies */}
      <motion.section
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: '-100px' }}
        transition={{ type: 'spring', stiffness: 100, damping: 20 }}
      >
        <div className="flex items-center justify-between mb-10">
          <div className="space-y-2">
            <h2 className="heading-3 text-orbit-text">
              Công nghệ thịnh hành
            </h2>
            <p className="text-[13px] font-medium text-orbit-text-muted">
              Những ngôn ngữ và framework được sử dụng nhiều nhất
            </p>
          </div>
        </div>
        {topStacks.length > 0 ? (
          <div className="flex flex-wrap gap-4">
            {topStacks.map((stack, i) => (
              <motion.div
                key={stack}
                initial={{ opacity: 0, scale: 0.9 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.05, type: 'spring', stiffness: 100, damping: 20 }}
                className="px-8 py-4 rounded-3xl bg-orbit-surface border border-orbit-border hover:border-orbit-accent/30 hover:bg-orbit-accent/5 transition-all duration-300 cursor-default group"
              >
                <span className="text-[15px] font-bold text-orbit-text-secondary group-hover:text-orbit-accent transition-colors tracking-wide">
                  {stack}
                </span>
              </motion.div>
            ))}
          </div>
        ) : (
          <div className="orbit-card p-12 text-center border-dashed border-2 border-orbit-border/20">
            <p className="text-[13px] text-orbit-text-muted">Chưa có dữ liệu công nghệ.</p>
          </div>
        )}
      </motion.section>

      {/* Recent Repositories */}
      <motion.section
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: '-100px' }}
        transition={{ delay: 0.1, type: 'spring', stiffness: 100, damping: 20 }}
      >
        <div className="flex items-center justify-between mb-10">
          <div className="space-y-2">
            <h2 className="heading-3 text-orbit-text">
              Cập nhật mới nhất
            </h2>
            <p className="text-[13px] font-medium text-orbit-text-muted">
              Các kho mã nguồn vừa được kết nối vào hệ thống
            </p>
          </div>
          <Link to="/courses" className="btn-secondary text-[11px] px-6 py-3 group">
            Xem tất cả
            <ArrowRight className="h-4 w-4 group-hover:translate-x-0.5 transition-transform" weight="bold" />
          </Link>
        </div>
        {recentRepos.length > 0 ? (
          <div className="grid gap-6 sm:grid-cols-2">
            {recentRepos.map((repo, i) => (
              <motion.div
                key={repo.id}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.08, type: 'spring', stiffness: 80, damping: 18 }}
              >
                <RepoCard repo={repo} />
              </motion.div>
            ))}
          </div>
        ) : (
          <div className="orbit-card p-12 text-center border-dashed border-2 border-orbit-border/20">
            <p className="text-[13px] text-orbit-text-muted">Chưa có kho mã nguồn nào được kết nối.</p>
          </div>
        )}
      </motion.section>
    </div>
  )
}
