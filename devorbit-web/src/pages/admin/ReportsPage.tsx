import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { BookmarkSimple, Eye, Star, ThumbsUp, ArrowSquareOut, CaretLeft, CaretRight } from '@phosphor-icons/react'
import type { RepoStatsEntry } from '../../types/admin'

type SortTab = 'bookmarks' | 'rating' | 'upvotes'

const SORT_TABS: { key: SortTab; label: string; icon: React.ReactNode; header: string }[] = [
  { key: 'bookmarks', label: 'Bookmark', icon: <BookmarkSimple size={16} weight="duotone" />, header: 'Lượt bookmark' },
  { key: 'rating', label: 'Đánh giá sao', icon: <Star size={16} weight="duotone" />, header: 'Đánh giá TB (5)' },
  { key: 'upvotes', label: 'Upvote', icon: <ThumbsUp size={16} weight="duotone" />, header: 'Lượt upvote' },
]

const PAGE_SIZE = 20

function Pagination({ page, total, onPage }: { page: number; total: number; onPage: (p: number) => void }) {
  const totalPages = Math.ceil(total / PAGE_SIZE)
  if (totalPages <= 1) return null
  return (
    <div className="flex items-center justify-center gap-2 mt-4 pt-3 border-t border-orbit-border/50">
      <button
        onClick={() => onPage(page - 1)}
        disabled={page <= 1}
        className="p-1.5 rounded-lg text-ink-secondary hover:text-orbit-text hover:bg-orbit-surface disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
      >
        <CaretLeft size={16} />
      </button>
      <span className="text-xs text-ink-secondary tabular-nums">
        {page} / {totalPages}
      </span>
      <button
        onClick={() => onPage(page + 1)}
        disabled={page >= totalPages}
        className="p-1.5 rounded-lg text-ink-secondary hover:text-orbit-text hover:bg-orbit-surface disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
      >
        <CaretRight size={16} />
      </button>
    </div>
  )
}

function FavoriteTable({ entries, activeTab }: { entries: RepoStatsEntry[]; activeTab: SortTab }) {
  const [page, setPage] = useState(1)
  const tab = SORT_TABS.find(t => t.key === activeTab)!
  const start = (page - 1) * PAGE_SIZE
  const paged = entries.slice(start, start + PAGE_SIZE)

  return (
    <div className="orbit-card p-6">
      <div className="flex items-center gap-3 mb-5">
        <div className="h-10 w-10 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center text-orbit-accent">
          <BookmarkSimple size={22} weight="duotone" />
        </div>
        <h2 className="text-lg font-heading font-bold text-orbit-text">Top repos được yêu thích nhất</h2>
      </div>

      {entries.length === 0 ? (
        <p className="text-sm text-ink-secondary py-4">Chưa có dữ liệu</p>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-orbit-border">
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">#</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Tên repo</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Môn học</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Ngôn ngữ</th>
                  <th className="text-right py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">{tab.header}</th>
                  <th className="text-center py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Link</th>
                </tr>
              </thead>
              <tbody>
                {paged.map((repo, i) => (
                  <tr key={repo.repoId} className="border-b border-orbit-border/50 hover:bg-orbit-surface/30 transition-colors">
                    <td className="py-3 px-2 text-ink-muted font-mono">{start + i + 1}</td>
                    <td className="py-3 px-2 font-medium text-orbit-text">{repo.repoName}</td>
                    <td className="py-3 px-2 text-ink-secondary">{repo.courseName || '—'}</td>
                    <td className="py-3 px-2">
                      {repo.primaryLanguage ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-semibold bg-orbit-accent/10 text-orbit-accent">
                          {repo.primaryLanguage}
                        </span>
                      ) : (
                        <span className="text-ink-muted">—</span>
                      )}
                    </td>
                    <td className="py-3 px-2 text-right">
                      <span className="inline-flex items-center justify-center min-w-[28px] h-7 px-2 rounded-lg bg-orbit-accent/10 text-orbit-accent font-bold tabular-nums">
                        {activeTab === 'rating'
                          ? `${repo.averageRating.toFixed(1)}/5`
                          : repo.count}
                      </span>
                    </td>
                    <td className="py-3 px-2 text-center">
                      <a
                        href={repo.githubUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center justify-center h-8 w-8 rounded-lg text-ink-muted hover:text-orbit-accent hover:bg-orbit-surface/50 transition-colors"
                      >
                        <ArrowSquareOut size={16} />
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination page={page} total={entries.length} onPage={setPage} />
        </>
      )}
    </div>
  )
}

function ViewedTable({ entries }: { entries: RepoStatsEntry[] }) {
  const [page, setPage] = useState(1)
  const start = (page - 1) * PAGE_SIZE
  const paged = entries.slice(start, start + PAGE_SIZE)

  return (
    <div className="orbit-card p-6">
      <div className="flex items-center gap-3 mb-5">
        <div className="h-10 w-10 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center text-orbit-accent">
          <Eye size={22} weight="duotone" />
        </div>
        <h2 className="text-lg font-heading font-bold text-orbit-text">Top repos được xem nhiều nhất</h2>
      </div>

      {entries.length === 0 ? (
        <p className="text-sm text-ink-secondary py-4">Chưa có dữ liệu</p>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-orbit-border">
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">#</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Tên repo</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Môn học</th>
                  <th className="text-left py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Ngôn ngữ</th>
                  <th className="text-right py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Lượt xem</th>
                  <th className="text-center py-3 px-2 text-[10px] font-black text-ink-secondary uppercase tracking-[0.15em]">Link</th>
                </tr>
              </thead>
              <tbody>
                {paged.map((repo, i) => (
                  <tr key={repo.repoId} className="border-b border-orbit-border/50 hover:bg-orbit-surface/30 transition-colors">
                    <td className="py-3 px-2 text-ink-muted font-mono">{start + i + 1}</td>
                    <td className="py-3 px-2 font-medium text-orbit-text">{repo.repoName}</td>
                    <td className="py-3 px-2 text-ink-secondary">{repo.courseName || '—'}</td>
                    <td className="py-3 px-2">
                      {repo.primaryLanguage ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-semibold bg-orbit-accent/10 text-orbit-accent">
                          {repo.primaryLanguage}
                        </span>
                      ) : (
                        <span className="text-ink-muted">—</span>
                      )}
                    </td>
                    <td className="py-3 px-2 text-right">
                      <span className="inline-flex items-center justify-center min-w-[28px] h-7 px-2 rounded-lg bg-orbit-accent/10 text-orbit-accent font-bold tabular-nums">
                        {repo.count}
                      </span>
                    </td>
                    <td className="py-3 px-2 text-center">
                      <a
                        href={repo.githubUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center justify-center h-8 w-8 rounded-lg text-ink-muted hover:text-orbit-accent hover:bg-orbit-surface/50 transition-colors"
                      >
                        <ArrowSquareOut size={16} />
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination page={page} total={entries.length} onPage={setPage} />
        </>
      )}
    </div>
  )
}

export function ReportsPage() {
  const [sortBy, setSortBy] = useState<SortTab>('bookmarks')

  const { data, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getStats(t, sortBy),
    [sortBy],
  )

  return (
    <AdminPageLayout title="Báo cáo thống kê" description="">
      {loading && <AdminSpinner text="Đang tải báo cáo..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {data && (
        <div className="space-y-6">
          <div className="flex gap-1 mb-2 flex-wrap">
            {SORT_TABS.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setSortBy(tab.key)}
                className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                  sortBy === tab.key
                    ? 'bg-orbit-accent text-white shadow-sm'
                    : 'bg-orbit-surface/50 text-ink-secondary hover:bg-orbit-surface hover:text-orbit-text'
                }`}
              >
                {tab.icon}
                {tab.label}
              </button>
            ))}
          </div>

          <FavoriteTable entries={data.topFavoritedRepos} activeTab={sortBy} />
          <ViewedTable entries={data.topViewedRepos} />
        </div>
      )}
    </AdminPageLayout>
  )
}
