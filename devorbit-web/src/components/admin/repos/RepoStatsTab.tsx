import { useState, useMemo, useCallback } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, LabelList,
} from 'recharts'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'

type TimeFilter = 'day' | 'week' | 'month'

const FILTER_OPTIONS: { key: TimeFilter; label: string }[] = [
  { key: 'day', label: 'Ngày' },
  { key: 'week', label: 'Tuần' },
  { key: 'month', label: 'Tháng' },
]

/** Max bars to show per page */
const PAGE_LIMIT: Record<TimeFilter, number> = {
  day: 14,
  week: 12,
  month: 12,
}

interface Bucket {
  key: string
  label: string
  start: Date
  end: Date
  count: number
}

const MIN_DATE = new Date(2026, 3, 15)

function pad2(n: number): string {
  return String(n).padStart(2, '0')
}

function formatDateShort(d: Date): string {
  return `${pad2(d.getDate())}/${pad2(d.getMonth() + 1)}`
}

function getMonday(d: Date): Date {
  const date = new Date(d)
  const day = date.getDay()
  const diff = day === 0 ? -6 : 1 - day
  date.setDate(date.getDate() + diff)
  date.setHours(0, 0, 0, 0)
  return date
}

function getWeekKey(date: Date): string {
  const monday = getMonday(date)
  return `${monday.getFullYear()}-${pad2(monday.getMonth() + 1)}-${pad2(monday.getDate())}`
}

function getBucketKey(date: Date, filter: TimeFilter): string {
  const y = date.getFullYear()
  const m = pad2(date.getMonth() + 1)
  const d = pad2(date.getDate())
  switch (filter) {
    case 'day': return `${y}-${m}-${d}`
    case 'week': return getWeekKey(date)
    case 'month': return `${y}-${m}`
  }
}

function getBucketLabel(key: string, filter: TimeFilter): string {
  switch (filter) {
    case 'day': {
      const [y, m, d] = key.split('-').map(Number)
      const date = new Date(y, m - 1, d)
      const vietDays = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
      return `${vietDays[date.getDay()]} ${formatDateShort(date)}`
    }
    case 'week': {
      const [y, m, d] = key.split('-').map(Number)
      const monday = new Date(y, m - 1, d)
      const sunday = new Date(monday)
      sunday.setDate(sunday.getDate() + 6)
      return `${formatDateShort(monday)} \u2192 ${formatDateShort(sunday)}`
    }
    case 'month': {
      const [, mStr] = key.split('-')
      return `Tháng ${parseInt(mStr, 10)}`
    }
  }
}

function getBucketRange(key: string, filter: TimeFilter): { start: Date; end: Date } {
  switch (filter) {
    case 'day': {
      const [y, m, d] = key.split('-').map(Number)
      return {
        start: new Date(y, m - 1, d, 0, 0, 0, 0),
        end: new Date(y, m - 1, d, 23, 59, 59, 999),
      }
    }
    case 'week': {
      const [y, m, d] = key.split('-').map(Number)
      const monday = new Date(y, m - 1, d)
      monday.setHours(0, 0, 0, 0)
      const sunday = new Date(monday)
      sunday.setDate(sunday.getDate() + 6)
      sunday.setHours(23, 59, 59, 999)
      return { start: monday, end: sunday }
    }
    case 'month': {
      const [y, m] = key.split('-').map(Number)
      return {
        start: new Date(y, m - 1, 1, 0, 0, 0, 0),
        end: new Date(y, m, 0, 23, 59, 59, 999),
      }
    }
  }
}

function formatDateTime(iso: string | null | undefined): string {
  if (!iso) return '-'
  const d = new Date(iso)
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

/** Generate contiguous empty buckets between min date and now for the given filter */
function generateEmptyBuckets(
  repos: { approvedAt?: string | null }[],
  filter: TimeFilter,
): Bucket[] {
  const now = new Date()
  let minTime = Infinity

  for (const r of repos) {
    if (!r.approvedAt) continue
    const d = new Date(r.approvedAt)
    if (isNaN(d.getTime())) continue

    if (filter === 'day') {
      const dayStart = new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime()
      if (dayStart < minTime) minTime = dayStart
    } else if (filter === 'week') {
      const monday = getMonday(d).getTime()
      if (monday < minTime) minTime = monday
    } else {
      const monthStart = new Date(d.getFullYear(), d.getMonth(), 1).getTime()
      if (monthStart < minTime) minTime = monthStart
    }
  }

  if (minTime === Infinity) return []

  const buckets: Bucket[] = []
  const cursor = new Date(Math.max(minTime, MIN_DATE.getTime()))

  // align cursor to start of period
  if (filter === 'week') {
    const c = getMonday(cursor)
    cursor.setTime(c.getTime())
  } else if (filter === 'month') {
    cursor.setDate(1)
  } else {
    cursor.setHours(0, 0, 0, 0)
  }

  const periodEnd = new Date(now)
  if (filter === 'month') {
    // cap at current month
    periodEnd.setDate(1)
    periodEnd.setMonth(periodEnd.getMonth() + 1)
    periodEnd.setDate(0) // last day of current month
  }

  while (cursor.getTime() <= periodEnd.getTime()) {
    const key = getBucketKey(cursor, filter)
    const range = getBucketRange(key, filter)
    buckets.push({
      key,
      label: getBucketLabel(key, filter),
      start: range.start,
      end: range.end,
      count: 0,
    })

    // advance cursor
    switch (filter) {
      case 'day':
        cursor.setDate(cursor.getDate() + 1)
        break
      case 'week':
        cursor.setDate(cursor.getDate() + 7)
        break
      case 'month':
        cursor.setMonth(cursor.getMonth() + 1)
        break
    }
  }

  return buckets
}

export function RepoStatsTab() {
  const [timeFilter, setTimeFilter] = useState<TimeFilter>('month')
  const [selectedBucket, setSelectedBucket] = useState<Bucket | null>(null)
  const [page, setPage] = useState<number>(0)

  const { data: repos, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getApprovedRepos(t),
    [],
  )

  // Reset pagination when filter changes
  const handleFilterChange = useCallback((filter: TimeFilter) => {
    setSelectedBucket(null)
    setTimeFilter(filter)
    setPage(0)
  }, [])

  const buckets = useMemo(() => {
    if (!repos) return []

    const emptyBuckets = generateEmptyBuckets(repos, timeFilter)
    const bucketMap = new Map(emptyBuckets.map((b) => [b.key, b]))

    for (const r of repos) {
      if (!r.approvedAt) continue
      const d = new Date(r.approvedAt)
      if (isNaN(d.getTime())) continue
      const key = getBucketKey(d, timeFilter)
      const bucket = bucketMap.get(key)
      if (bucket) bucket.count++
    }

    return Array.from(bucketMap.values())
  }, [repos, timeFilter])

  // Pagination: page 0 = newest items
  const displayBuckets = useMemo(() => {
    const limit = PAGE_LIMIT[timeFilter]
    const end = buckets.length - page * limit
    const start = Math.max(0, end - limit)
    return buckets.slice(start, end)
  }, [buckets, timeFilter, page])

  const canGoOlder = (buckets.length - (page + 1) * PAGE_LIMIT[timeFilter]) > 0
  const canGoNewer = page > 0

  // Page info text: "1\u201314 / 42"
  const pageInfo = useMemo(() => {
    const limit = PAGE_LIMIT[timeFilter]
    const total = buckets.length
    if (total === 0) return ''
    const end = total - page * limit
    const start = Math.max(1, end - limit + 1)
    if (start >= end && page === 0) return `${total} m\u1ee5c`
    return `${start}\u2013${end} / ${total}`
  }, [buckets, timeFilter, page])

  const bucketRepos = useMemo(() => {
    if (!selectedBucket || !repos) return []
    return repos.filter((r) => {
      if (!r.approvedAt) return false
      const d = new Date(r.approvedAt)
      return !isNaN(d.getTime()) && d >= selectedBucket.start && d <= selectedBucket.end
    })
  }, [repos, selectedBucket])

  if (loading) return <AdminSpinner text="Đang tải thống kê..." />
  if (error) return <AdminErrorBanner message={error} onRetry={refetch} />

  const chartData = displayBuckets.map((b) => ({ name: b.label, count: b.count, bucket: b }))
  const totalRepos = buckets.reduce((s, b) => s + b.count, 0)

  return (
    <div>
      {/* Filter toggle */}
      <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
        <div className="flex gap-1 bg-[var(--color-surface)] rounded-lg p-1 border border-[#334155]">
          {FILTER_OPTIONS.map((opt) => (
            <button
              key={opt.key}
              onClick={() => handleFilterChange(opt.key)}
              className={`px-3 py-1.5 text-sm font-medium rounded-md transition-all ${
                timeFilter === opt.key
                  ? 'bg-[#3b82f6] text-white shadow-sm'
                  : 'text-[#94a3b8] hover:text-[#f1f5f9]'
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>
        <span className="text-sm text-[#94a3b8]">
          {totalRepos} repo
        </span>
      </div>

      {/* Chart */}
      <div className="rounded-xl p-5 mb-4 border border-[#1e293b] bg-[var(--color-surface)]">
        {chartData.length > 0 ? (
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={chartData} margin={{ top: 20, right: 16, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="4 4" stroke="#334155" />
              <XAxis
                dataKey="name"
                tick={{ fontSize: 11, fill: '#94a3b8' }}
                interval={0}
                angle={timeFilter === 'month' ? -25 : timeFilter === 'week' ? -15 : 0}
                textAnchor={timeFilter === 'month' ? 'end' : 'middle'}
                height={timeFilter === 'month' ? 50 : timeFilter === 'week' ? 45 : 35}
              />
              <YAxis allowDecimals={false} width={40} tick={{ fontSize: 12, fill: '#94a3b8' }} />
              <Tooltip
                contentStyle={{
                  background: '#0f172a',
                  border: '1px solid #334155',
                  borderRadius: 8,
                  color: '#f1f5f9',
                  fontSize: 13,
                  boxShadow: '0 4px 20px rgba(0,0,0,0.3)',
                }}
                formatter={(value: any) => [value, 'Số repo']}
              />
              <Bar
                dataKey="count"
                radius={[4, 4, 0, 0]}
                cursor="pointer"
                onClick={(data: unknown) => {
                  const payload = data as { bucket?: Bucket } | null
                  if (payload && payload.bucket) {
                    setSelectedBucket(payload.bucket)
                  }
                }}
              >
                <LabelList dataKey="count" position="top" fontSize={11} fill="#f1f5f9" fontWeight={600} />
                {chartData.map((entry) => (
                  <Cell
                    key={entry.bucket.key}
                    fill={
                      entry.bucket.count === 0
                        ? '#334155'
                        : selectedBucket?.key === entry.bucket.key
                          ? '#2563eb'
                          : '#3b82f6'
                    }
                    style={{ transition: 'fill 0.2s' }}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex items-center justify-center h-[250px] text-[#94a3b8]">
            Chưa có dữ liệu
          </div>
        )}
      </div>

      {/* Pagination bar */}
      <div className="flex items-center justify-between px-1 mb-4">
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={!canGoOlder}
          className="flex items-center gap-1 px-3 py-1.5 text-sm text-[#94a3b8] hover:text-[#f1f5f9] disabled:text-[#475569] disabled:cursor-not-allowed transition-colors rounded-md hover:bg-[#1e293b]/50"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="15 18 9 12 15 6" />
          </svg>
          Cũ hơn
        </button>

        <span className="text-xs text-[#64748b] select-none">{pageInfo}</span>

        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={!canGoNewer}
          className="flex items-center gap-1 px-3 py-1.5 text-sm text-[#94a3b8] hover:text-[#f1f5f9] disabled:text-[#475569] disabled:cursor-not-allowed transition-colors rounded-md hover:bg-[#1e293b]/50"
        >
          Mới hơn
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="9 18 15 12 9 6" />
          </svg>
        </button>
      </div>

      {/* Summary table */}
      <div className="rounded-xl mb-4 border border-[#1e293b] bg-[var(--color-surface)] overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-[#334155]">
              <th className="px-4 py-2.5 text-left text-xs font-medium text-[#94a3b8] uppercase tracking-wider">
                {timeFilter === 'day' ? 'Ngày' : timeFilter === 'week' ? 'Tuần' : 'Tháng'}
              </th>
              <th className="px-4 py-2.5 text-right text-xs font-medium text-[#94a3b8] uppercase tracking-wider">
                Số repo
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#1e293b]">
            {displayBuckets.map((b) => (
              <tr
                key={b.key}
                onClick={() => setSelectedBucket(b)}
                className={`transition-colors cursor-pointer ${
                  selectedBucket?.key === b.key
                    ? 'bg-[#2563eb]/10'
                    : 'hover:bg-[#1e293b]/50'
                }`}
              >
                <td className="px-4 py-2 text-sm text-[#f1f5f9]">{b.label}</td>
                <td className="px-4 py-2 text-sm text-right text-[#f1f5f9] font-mono">{b.count}</td>
              </tr>
            ))}
          </tbody>
          {/* Total row */}
          <tfoot>
            <tr className="border-t border-[#334155]">
              <td className="px-4 py-2.5 text-sm font-semibold text-[#f1f5f9]">Tổng</td>
              <td className="px-4 py-2.5 text-sm font-semibold text-right text-[#f1f5f9] font-mono">{totalRepos}</td>
            </tr>
          </tfoot>
        </table>
      </div>

      {/* Bucket detail */}
      <div className="glass-card overflow-hidden border border-orbit-border">
        {!selectedBucket ? (
          <div className="px-4 py-10 text-center body-sm text-ink-secondary">
            Click vào cột hoặc dòng để xem danh sách repo
          </div>
        ) : bucketRepos.length === 0 ? (
          <div className="px-4 py-10 text-center body-sm text-ink-secondary">
            Không có repo nào trong khoảng thời gian này
          </div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b border-orbit-border bg-orbit-surface/50">
                <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Repo</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Môn học</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Ngôn ngữ</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Stars</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Đã duyệt lúc</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-clay-border">
              {bucketRepos.map((repo) => (
                <tr key={repo.id} className="transition-colors hover:bg-orbit-surface/30">
                  <td className="px-4 py-3 text-sm text-center">
                    <a href={repo.githubUrl} target="_blank" rel="noopener noreferrer" className="font-medium text-ink-primary hover:text-orbit-accent transition-colors">
                      {repo.displayName}
                    </a>
                  </td>
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.courseName ?? '-'}</td>
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.primaryLanguage ?? '-'}</td>
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary">{repo.stars ?? '-'}</td>
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary whitespace-nowrap">{formatDateTime(repo.approvedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
