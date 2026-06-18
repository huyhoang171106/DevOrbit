import { useState, useMemo } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell
} from 'recharts'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'

type TimeFilter = 'all' | 'today' | 'week' | 'month'

interface Bucket {
  key: string
  label: string
  start: Date
  end: Date
  count: number
}

function getBucketKey(date: Date, filter: TimeFilter): string {
  switch (filter) {
    case 'all':
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
    case 'today':
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}`
    case 'week':
    case 'month':
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
}

function getBucketLabel(key: string, filter: TimeFilter): string {
  switch (filter) {
    case 'all': {
      const [y, m] = key.split('-')
      return `Tháng ${parseInt(m, 10)}/${y}`
    }
    case 'today':
      return `${key.slice(11)}h`
    case 'week': {
      const d = new Date(key)
      const dayOfWeek = d.getDay()
      const vietDays = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
      return `${vietDays[dayOfWeek]} ${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`
    }
    case 'month': {
      const [, m, day] = key.split('-')
      return `${String(parseInt(day, 10)).padStart(2, '0')}/${m}`
    }
  }
}

function getBucketRange(key: string, filter: TimeFilter): { start: Date; end: Date } {
  const start = new Date(key)
  const end = new Date(key)

  switch (filter) {
    case 'all': {
      const [y, m] = key.split('-')
      start.setFullYear(parseInt(y, 10), parseInt(m, 10) - 1, 1)
      start.setHours(0, 0, 0, 0)
      end.setFullYear(parseInt(y, 10), parseInt(m, 10), 0)
      end.setHours(23, 59, 59, 999)
      break
    }
    case 'today':
      start.setHours(parseInt(key.slice(11), 10), 0, 0, 0)
      end.setHours(parseInt(key.slice(11), 10), 59, 59, 999)
      break
    case 'week':
    case 'month':
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
  }

  return { start, end }
}

function formatDate(iso: string | null | undefined): string {
  if (!iso) return '-'
  const d = new Date(iso)
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

export function RepoStatsTab() {
  const [timeFilter, setTimeFilter] = useState<TimeFilter>('all')
  const [selectedBucket, setSelectedBucket] = useState<Bucket | null>(null)

  const { data: repos, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getApprovedRepos(t),
    [],
  )

  const buckets = useMemo(() => {
    if (!repos) return []
    const map = new Map<string, number>()

    for (const r of repos) {
      if (!r.approvedAt) continue
      const d = new Date(r.approvedAt)
      if (isNaN(d.getTime())) continue
      const key = getBucketKey(d, timeFilter)
      map.set(key, (map.get(key) || 0) + 1)
    }

    const keys = Array.from(map.keys()).sort()
    return keys.map((key) => {
      const range = getBucketRange(key, timeFilter)
      return {
        key,
        label: getBucketLabel(key, timeFilter),
        start: range.start,
        end: range.end,
        count: map.get(key) ?? 0,
      }
    })
  }, [repos, timeFilter])

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

  const chartData = buckets.map((b) => ({ name: b.label, count: b.count, bucket: b }))

  return (
    <div>
      <div className="flex items-center gap-3 mb-4">
        <select
          value={timeFilter}
          onChange={(e) => { setSelectedBucket(null); setTimeFilter(e.target.value as TimeFilter) }}
          className="input-field w-auto"
        >
          <option value="all">Tất cả</option>
          <option value="today">Hôm nay</option>
          <option value="week">Tuần này</option>
          <option value="month">Tháng này</option>
        </select>
        <span className="text-sm text-ink-muted">
          {buckets.reduce((s, b) => s + b.count, 0)} repo
        </span>
      </div>

      {chartData.length > 0 && (
        <div className="glass-card p-4 mb-4 border border-orbit-border">
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={chartData} margin={{ top: 8, right: 16, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)"/>
              <XAxis
                dataKey="name"
                tick={{ fontSize: 12, fill: 'var(--color-text-secondary)' }}
                interval={0}
                angle={timeFilter === 'all' ? -30 : 0}
                textAnchor={timeFilter === 'all' ? 'end' : 'middle'}
                height={timeFilter === 'all' ? 60 : 30}
              />
              <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: 'var(--color-text-secondary)' }} />
              <Tooltip
                contentStyle={{
                  background: 'var(--color-surface)',
                  border: '1px solid var(--color-border)',
                  borderRadius: '8px',
                  fontSize: '13px',
                }}
                formatter={(value) => [value, 'Số repo']}
              />
              <Bar
                dataKey="count"
                fill="#3b82f6"
                radius={[4, 4, 0, 0]}
                cursor="pointer"
                onClick={(data) => {
                  const payload = data as { bucket?: Bucket } | null
                  if (payload && payload.bucket) {
                    setSelectedBucket(payload.bucket)
                  }
                }}
              >
                {chartData.map((entry) => (
                  <Cell
                    key={entry.bucket.key}
                    fill={selectedBucket?.key === entry.bucket.key ? '#2563eb' : '#3b82f6'}
                    style={{ transition: 'fill 0.2s' }}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      <div className="glass-card overflow-hidden border border-orbit-border">
        {!selectedBucket ? (
          <div className="px-4 py-10 text-center body-sm text-ink-secondary">
            Click vào cột để xem danh sách repo
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
                  <td className="px-4 py-3 text-sm text-center text-ink-secondary whitespace-nowrap">{formatDate(repo.approvedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
