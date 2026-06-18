import { useState, useMemo, useCallback } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, LabelList,
} from 'recharts'
import { adminApi } from '../../../lib/adminApi'
import { useAdminFetch } from '../../../lib/adminHooks'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'

type TimeFilter = 'week' | 'month'

interface Bucket {
  key: string
  label: string
  start: Date
  end: Date
  count: number
}

interface WeekStat {
  monday: Date
  label: string
  count: number
}

const MIN_MONDAY = (() => {
  const d = new Date(2026, 3, 15)
  const day = d.getDay()
  const diff = day === 0 ? -6 : 1 - day
  d.setDate(d.getDate() + diff)
  d.setHours(0, 0, 0, 0)
  return d
})()

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

function getBucketKey(date: Date, filter: TimeFilter): string {
  const y = date.getFullYear()
  const m = pad2(date.getMonth() + 1)
  if (filter === 'month') return `${y}-${m}`
  return `${y}-${m}-${pad2(date.getDate())}`
}

function getBucketLabel(key: string, filter: TimeFilter): string {
  if (filter === 'week') {
    const [y, m, d] = key.split('-').map(Number)
    const date = new Date(y, m - 1, d)
    const vietDays = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
    return `${vietDays[date.getDay()]} ${formatDateShort(date)}`
  }
  const [, m] = key.split('-')
  return `Tháng ${parseInt(m, 10)}`
}

function getBucketRange(key: string, filter: TimeFilter): { start: Date; end: Date } {
  if (filter === 'month') {
    const [y, m] = key.split('-').map(Number)
    return {
      start: new Date(y, m - 1, 1, 0, 0, 0, 0),
      end: new Date(y, m, 0, 23, 59, 59, 999),
    }
  }
  const [y, m, d] = key.split('-').map(Number)
  return {
    start: new Date(y, m - 1, d, 0, 0, 0, 0),
    end: new Date(y, m - 1, d, 23, 59, 59, 999),
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

export function RepoStatsTab() {
  const [timeFilter, setTimeFilter] = useState<TimeFilter>('week')
  const [selectedBucket, setSelectedBucket] = useState<Bucket | null>(null)
  const [selectedMonday, setSelectedMonday] = useState<Date>(() => getMonday(new Date()))

  const { data: repos, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getApprovedRepos(t),
    [],
  )

  const currentMonday = useMemo(() => getMonday(new Date()), [])

  const goPrevWeek = useCallback(() => {
    setSelectedMonday((prev) => {
      const next = new Date(prev)
      next.setDate(next.getDate() - 7)
      if (next.getTime() < MIN_MONDAY.getTime()) return prev
      return next
    })
    setSelectedBucket(null)
  }, [])

  const goNextWeek = useCallback(() => {
    setSelectedMonday((prev) => {
      const next = new Date(prev)
      next.setDate(next.getDate() + 7)
      if (next.getTime() > currentMonday.getTime()) return prev
      return next
    })
    setSelectedBucket(null)
  }, [currentMonday])

  const weekOptions = useMemo(() => {
    const seen = new Set<number>()
    const options: { monday: Date; label: string }[] = []

    if (repos) {
      for (const r of repos) {
        if (!r.approvedAt) continue
        const d = new Date(r.approvedAt)
        if (isNaN(d.getTime())) continue
        const monday = getMonday(d)
        const ts = monday.getTime()
        if (seen.has(ts) || ts < MIN_MONDAY.getTime() || ts > currentMonday.getTime()) continue
        seen.add(ts)
        options.push({ monday, label: formatDateShort(monday) })
      }
    }

    const curTs = currentMonday.getTime()
    if (curTs >= MIN_MONDAY.getTime() && !seen.has(curTs)) {
      options.push({
        monday: currentMonday,
        label: formatDateShort(currentMonday),
      })
      seen.add(curTs)
    }

    const selTs = selectedMonday.getTime()
    if (!seen.has(selTs)) {
      options.push({ monday: selectedMonday, label: formatDateShort(selectedMonday) })
    }

    options.sort((a, b) => b.monday.getTime() - a.monday.getTime())
    return options
  }, [repos, currentMonday, selectedMonday])

  const weekStats = useMemo(() => {
    if (!repos || timeFilter !== 'week') return []
    const map = new Map<number, WeekStat>()
    for (const r of repos) {
      if (!r.approvedAt) continue
      const d = new Date(r.approvedAt)
      if (isNaN(d.getTime())) continue
      const monday = getMonday(d)
      const ts = monday.getTime()
      if (map.has(ts)) {
        map.get(ts)!.count++
      } else {
        map.set(ts, { monday, label: '', count: 1 })
      }
    }
    return Array.from(map.values())
      .filter((s) => s.monday.getTime() >= MIN_MONDAY.getTime() && s.monday.getTime() <= currentMonday.getTime())
      .sort((a, b) => b.monday.getTime() - a.monday.getTime())
      .map((s) => {
        const sunday = new Date(s.monday)
        sunday.setDate(sunday.getDate() + 6)
        return { ...s, label: `${formatDateShort(s.monday)} → ${formatDateShort(sunday)}` }
      })
  }, [repos, timeFilter, currentMonday])

  const buckets = useMemo(() => {
    if (!repos) return []

    const emptyBuckets: Bucket[] = []

    if (timeFilter === 'week') {
      for (let i = 0; i < 7; i++) {
        const d = new Date(selectedMonday)
        d.setDate(d.getDate() + i)
        const key = getBucketKey(d, 'week')
        emptyBuckets.push({
          key,
          label: getBucketLabel(key, 'week'),
          ...getBucketRange(key, 'week'),
          count: 0,
        })
      }
    } else {
      let minMonth = Infinity
      for (const r of repos) {
        if (!r.approvedAt) continue
        const d = new Date(r.approvedAt)
        if (isNaN(d.getTime())) continue
        const m = d.getFullYear() * 12 + d.getMonth()
        if (m < minMonth) minMonth = m
      }

      if (minMonth === Infinity) return []

      const startYear = Math.floor(minMonth / 12)
      const startMonth = minMonth % 12
      const now = new Date()
      const endMonth = now.getMonth() <= 6 ? 6 : now.getMonth()
      const endYear = now.getFullYear()

      let year = startYear
      let month = startMonth
      while (year < endYear || (year === endYear && month <= endMonth)) {
        const key = `${year}-${pad2(month + 1)}`
        emptyBuckets.push({
          key,
          label: getBucketLabel(key, 'month'),
          ...getBucketRange(key, 'month'),
          count: 0,
        })
        month++
        if (month > 11) {
          month = 0
          year++
        }
      }
    }

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
  }, [repos, timeFilter, selectedMonday])

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
  const totalRepos = buckets.reduce((s, b) => s + b.count, 0)
  const grandTotal = timeFilter === 'week'
    ? weekStats.reduce((s, w) => s + w.count, 0)
    : totalRepos

  return (
    <div>
      <div className="flex items-center gap-3 mb-4 flex-wrap">
        <select
          value={timeFilter}
          onChange={(e) => { setSelectedBucket(null); setTimeFilter(e.target.value as TimeFilter) }}
          className="input-field w-auto"
        >
          <option value="week">Tuần</option>
          <option value="month">Tháng</option>
        </select>

        {timeFilter === 'week' && (
          <>
            <select
              value={selectedMonday.getTime()}
              onChange={(e) => {
                setSelectedBucket(null)
                setSelectedMonday(new Date(Number(e.target.value)))
              }}
              className="input-field w-auto text-sm"
            >
              {weekOptions.map((opt) => (
                <option key={opt.monday.getTime()} value={opt.monday.getTime()}>
                  {opt.label}
                </option>
              ))}
            </select>

            <div className="flex items-center gap-1">
              <button
                onClick={goPrevWeek}
                disabled={selectedMonday.getTime() <= MIN_MONDAY.getTime()}
                className="flex items-center justify-center w-7 h-7 rounded-md border border-[#334155] bg-[var(--color-surface)] hover:bg-[#1e293b] disabled:opacity-30 disabled:cursor-not-allowed text-[#94a3b8] transition-colors"
                title="Tuần trước"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="15 18 9 12 15 6" />
                </svg>
              </button>

              <span className="text-sm text-[#94a3b8] min-w-[120px] text-center select-none">
                {formatDateShort(selectedMonday)} → {formatDateShort(new Date(selectedMonday.getTime() + 6 * 86400000))}
              </span>

              <button
                onClick={goNextWeek}
                disabled={selectedMonday.getTime() >= currentMonday.getTime()}
                className="flex items-center justify-center w-7 h-7 rounded-md border border-[#334155] bg-[var(--color-surface)] hover:bg-[#1e293b] disabled:opacity-30 disabled:cursor-not-allowed text-[#94a3b8] transition-colors"
                title="Tuần sau"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="9 18 15 12 9 6" />
                </svg>
              </button>
            </div>
          </>
        )}

        <span className="text-sm text-ink-muted">
          {totalRepos} repo
        </span>
      </div>

      <div className="rounded-xl p-5 mb-4 border border-[#1e293b] bg-[var(--color-surface)]">
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={chartData} margin={{ top: 20, right: 16, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="4 4" stroke="#334155" />
            <XAxis
              dataKey="name"
              tick={{ fontSize: 12, fill: '#94a3b8' }}
              interval={0}
              angle={timeFilter === 'month' ? -15 : 0}
              textAnchor={timeFilter === 'month' ? 'end' : 'middle'}
              height={timeFilter === 'month' ? 40 : 30}
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
              formatter={(value) => [value, 'Số repo']}
            />
            <Bar
              dataKey="count"
              radius={[4, 4, 0, 0]}
              cursor="pointer"
              onClick={(data) => {
                const payload = data as { bucket?: Bucket } | null
                if (payload && payload.bucket) {
                  setSelectedBucket(payload.bucket)
                }
              }}
            >
              <LabelList dataKey="count" position="top" fontSize={12} fill="#f1f5f9" fontWeight={600} />
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
      </div>

      <div className="rounded-xl mb-4 border border-[#1e293b] bg-[var(--color-surface)] overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-[#334155]">
              <th className="px-4 py-2.5 text-left text-xs font-medium text-[#94a3b8] uppercase tracking-wider">
                {timeFilter === 'week' ? 'Tuần' : 'Tháng'}
              </th>
              <th className="px-4 py-2.5 text-right text-xs font-medium text-[#94a3b8] uppercase tracking-wider">
                Số repo
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#1e293b]">
            {timeFilter === 'week'
              ? weekStats.map((s) => (
                  <tr
                    key={s.monday.getTime()}
                    onClick={() => {
                      setSelectedMonday(s.monday)
                      setSelectedBucket(null)
                    }}
                    className={`transition-colors cursor-pointer ${
                      s.monday.getTime() === selectedMonday.getTime()
                        ? 'bg-[#2563eb]/10'
                        : 'hover:bg-[#1e293b]/50'
                    }`}
                  >
                    <td className="px-4 py-2 text-sm text-[#f1f5f9]">{s.label}</td>
                    <td className="px-4 py-2 text-sm text-right text-[#f1f5f9] font-mono">{s.count}</td>
                  </tr>
                ))
              : buckets.map((b) => (
                  <tr key={b.key} className="hover:bg-[#1e293b]/50 transition-colors">
                    <td className="px-4 py-2 text-sm text-[#f1f5f9]">{b.label}</td>
                    <td className="px-4 py-2 text-sm text-right text-[#f1f5f9] font-mono">{b.count}</td>
                  </tr>
                ))}
          </tbody>
          <tfoot>
            <tr className="border-t border-[#334155]">
              <td className="px-4 py-2.5 text-sm font-semibold text-[#f1f5f9]">Tổng</td>
              <td className="px-4 py-2.5 text-sm font-semibold text-right text-[#f1f5f9] font-mono">{grandTotal}</td>
            </tr>
          </tfoot>
        </table>
      </div>

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
