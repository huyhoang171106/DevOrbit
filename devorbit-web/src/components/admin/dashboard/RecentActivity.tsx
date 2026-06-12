import type { AdminStats } from '../../../types/admin'

interface RecentActivityProps {
  stats: AdminStats
}

function RecentSection({ title, items, render }: {
  title: string
  items: unknown[]
  render: (item: unknown, i: number) => React.ReactNode
}) {
  return (
    <div className="glass-card p-6">
      <h3 className="text-sm font-semibold text-ink-primary mb-3">{title}</h3>
      {items.length === 0 ? (
        <p className="text-xs text-ink-secondary">Chưa có</p>
      ) : (
        <ul className="space-y-2">
          {items.map((item, i) => (
            <li key={i} className="text-xs text-ink-secondary">
              {render(item, i)}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export function RecentActivity({ stats }: RecentActivityProps) {
  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <RecentSection
        title="Sinh viên mới"
        items={stats.recentStudents}
        render={(item: unknown) => {
          const s = item as { fullName?: string; studentCode?: string }
          return <span>{s.fullName} <span className="text-ink-muted">({s.studentCode})</span></span>
        }}
      />
      <RecentSection
        title="Đánh giá gần đây"
        items={stats.recentCourseReviews}
        render={(item: unknown) => {
          const r = item as { studentName?: string; courseName?: string; rating?: number }
          return <span>{r.studentName} đánh giá {r.courseName} — {r.rating}/5</span>
        }}
      />
      <RecentSection
        title="Bài nộp gần đây"
        items={stats.recentSubmissions}
        render={(item: unknown) => {
          const s = item as { githubUrl?: string; courseName?: string }
          return <span className="truncate block">{s.courseName || 'Repo'}</span>
        }}
      />
    </div>
  )
}
